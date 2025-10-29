package ar.edu.utn.dds.k3003.telegram.bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public interface BotCommand {

    String execute(Update update) throws TelegramApiException;

    String getCommandName();

    String getDescription();

    default boolean requiresParameters() {
        return false;
    }

    default String getUsageExample() {
        return "/" + getCommandName();
    }
}
