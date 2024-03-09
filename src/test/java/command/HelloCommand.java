package command;

import cn.lemonprefect.tegrama.annotation.Broadcaster;
import cn.lemonprefect.tegrama.annotation.OnCommand;
import cn.lemonprefect.tegrama.command.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@OnCommand(command = "hello")
public final class HelloCommand extends Command{

    @Override
    public void resolve(Message message) throws TelegramApiException{
        User from = message.getFrom();
        SendMessage helloMessage = SendMessage.builder()
                .text(String.format("Hello %s, %s", from.getUserName(), from.getId()))
                .chatId(from.getId().toString())
                .build();

        bot.execute(helloMessage);
    }

    @Broadcaster(receivers = "hello")
    public void hello() throws TelegramApiException{
        String[] receivers = getReceiver("hello");
        for(String receiver : receivers){
            SendMessage helloMessage = SendMessage.builder()
                    .text(String.format("Hello %s", receiver))
                    .chatId(receiver)
                    .build();
            bot.execute(helloMessage);
        }
    }
}
