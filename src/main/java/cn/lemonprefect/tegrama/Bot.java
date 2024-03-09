package cn.lemonprefect.tegrama;

import cn.lemonprefect.tegrama.annotation.OnCommand;
import cn.lemonprefect.tegrama.command.Command;
import cn.lemonprefect.tegrama.manager.ConfigurationManager;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

public class Bot extends TelegramLongPollingBot{

    private final HashMap<String, Class<? extends Command>> commands = new HashMap<String, Class<? extends Command>>();
    private static final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    public Bot(DefaultBotOptions options){
        super(options);
        setController();
    }

    public Bot(){
        super();
        setController();
    }

    private void setController(){
        String reflectionPackageName = configurationManager.getProperty("tegrama.reflect.command");
        logger.info("Reflecting commands from {}", reflectionPackageName);
        Reflections reflections = new Reflections(reflectionPackageName);

        for(Class<? extends Command> clazz : reflections.getSubTypesOf(Command.class)){
            String command = clazz.getAnnotation(OnCommand.class).command();
            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(method.getAnnotation(OnCommand.class) == null) continue;
                String subCommand = method.getAnnotation(OnCommand.class).command();
                commands.put(String.format("%s_%s", command, subCommand), clazz);
                logger.info("{}#{} --> {}#{}", command, subCommand, clazz.getName(), method.getName());
            }
            commands.put(command, clazz);
            logger.info("{} --> {}", command, clazz.getName());
        }
    }

    @Override
    public String getBotUsername(){
        return configurationManager.getProperty("tegrama.bot.name");
    }

    @Override
    public String getBotToken(){
        return configurationManager.getProperty("tegrama.bot.token");
    }

    @Override
    public void onUpdateReceived(Update update){
        String textMessage = update.getMessage().getText();
        String command = textMessage.startsWith("/") ? textMessage.split(" ")[0].toLowerCase().substring(1) : "nil";

        try{
            String[] args = command.split("_");
            if(args.length == 1){
                if(commands.get(command) == null){
                    command = "nil";
                }
                logger.info("trigger {} --> {}", command, commands.get(command).getName());
                commands.get(command).newInstance().resolve(update.getMessage());
            }else{
                Class<? extends Command> clazz = commands.get(String.format("%s_%s", args[0], args[1]));
                if(clazz == null){
                    logger.info("trigger {} --> {}", command, commands.get(args[0]).getName());
                    commands.get(args[0]).newInstance().resolve(update.getMessage());
                }else{
                    Command instance = commands.get(args[0]).newInstance();
                    Method[] methods = clazz.getMethods();
                    Method method = clazz.getMethod("resolve", Message.class);
                    for(Method _method : methods){
                        OnCommand onCommand = _method.getAnnotation(OnCommand.class);
                        if(onCommand == null) continue;
                        String subCommand = onCommand.command();
                        if(Objects.equals(args[1], subCommand)){
                            method = _method;
                            break;
                        }
                    }
                    logger.info("trigger {} --> {}#{}", command, clazz.getName(), method.getName());
                    method.invoke(instance, update.getMessage());
                }
            }
        }catch(InstantiationException | IllegalAccessException | TelegramApiException | InvocationTargetException | NoSuchMethodException e){
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
