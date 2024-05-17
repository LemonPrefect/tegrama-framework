package cn.lemonprefect.tegrama.manager;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class ConfigurationManager{

    private static ConfigurationManager instance;
    private final CompositeConfiguration configuration;
    private static final String envFilePath = System.getProperty("TEGRAMA_PROPERTIES");
    private static final String resourceFilePath = "tegrama.properties";
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);

    private ConfigurationManager() throws ConfigurationException, IOException{
        configuration = new CompositeConfiguration();
        Configurations configs = new Configurations();

        configuration.addConfiguration(configs.properties(envFilePath));
        logger.info("Local Configuration file: {}", envFilePath);
        configuration.addConfiguration(configs.properties(getClass().getClassLoader().getResource(resourceFilePath)));
        logger.info("Preset Configuration file: {}", resourceFilePath);
    }

    public static synchronized ConfigurationManager getInstance(){
        if(instance == null){
            try{
                instance = new ConfigurationManager();
            }catch(ConfigurationException | IOException e){
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return instance;
    }

    public String getProperty(String key){
        return configuration.getString(key);
    }

    public String[] getStringArray(String key){
        String value = configuration.getString(key);
        if(value == null || value.equals("")){
            logger.warn("{} is empty or null", key);
            return new String[0];
        }
        return Arrays.stream(value.split(",")).map(String::strip).toArray(String[]::new);
    }
}
