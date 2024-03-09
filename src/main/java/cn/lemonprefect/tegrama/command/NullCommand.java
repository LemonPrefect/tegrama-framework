package cn.lemonprefect.tegrama.command;

import cn.lemonprefect.tegrama.annotation.OnCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@OnCommand(command = "nil")
public final class NullCommand extends Command{

    @Override
    public void resolve(Message message) throws TelegramApiException{
        User from = message.getFrom();
        SendMessage helloMessage = SendMessage.builder()
                .text("E_COMMAND_NOT_FOUND")
                .chatId(from.getId().toString())
                .build();

        bot.execute(helloMessage);
    }
}
