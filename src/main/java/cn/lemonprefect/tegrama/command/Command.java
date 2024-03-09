package cn.lemonprefect.tegrama.command;

import cn.lemonprefect.tegrama.Bot;
import cn.lemonprefect.tegrama.annotation.Broadcaster;
import cn.lemonprefect.tegrama.annotation.OnCommand;
import cn.lemonprefect.tegrama.manager.BotManager;
import cn.lemonprefect.tegrama.manager.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

public abstract class Command{
    protected static final Bot bot = BotManager.getBot();
    protected static final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract void resolve(Message message) throws TelegramApiException;

    protected String[] getReceiver(String methodName){
        String receiversAnnotation = null;
        String classAnnotation = null;
        try{
            classAnnotation = this.getClass().getAnnotation(OnCommand.class).command();
            receiversAnnotation = this.getClass().getMethod(methodName).getAnnotation(Broadcaster.class).receivers();
        }catch(NoSuchMethodException e){
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        String receiversKey = String.format("%s.%s.receivers", classAnnotation, receiversAnnotation);
        String[] receivers = Arrays.stream(configurationManager.getStringArray(receiversKey)).distinct().toArray(String[]::new);
        logger.info(String.join("Receivers: , ", receivers));
        return receivers;
    }
}
