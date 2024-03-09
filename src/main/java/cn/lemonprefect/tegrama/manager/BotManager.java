package cn.lemonprefect.tegrama.manager;

import cn.lemonprefect.tegrama.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotManager{
    private static Bot bot;
    private static final Logger logger = LoggerFactory.getLogger(BotManager.class);
    protected static final ConfigurationManager configurationManager = ConfigurationManager.getInstance();



    private BotManager(){
    }

    public static synchronized Bot getBot(){
        if(bot == null){
            String proxyHost = configurationManager.getProperty("tegrama.proxy.host");
            String proxyPort = configurationManager.getProperty("tegrama.proxy.port");

            DefaultBotOptions botOptions = new DefaultBotOptions();

            if(proxyHost != null && proxyPort != null){
                botOptions.setProxyHost(proxyHost);
                botOptions.setProxyPort(Integer.parseInt(proxyPort));
                botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

                logger.info("Proxy: {}:{}", proxyHost, proxyPort);
            }
            try{
                bot = new Bot(botOptions);
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(bot);
            }catch(TelegramApiException e){
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return bot;
    }
}
