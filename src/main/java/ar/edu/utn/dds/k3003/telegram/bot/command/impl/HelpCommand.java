package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.command.CommandRegistry;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Comando /help - Muestra ayuda con todos los comandos
 */
@Component
public class HelpCommand extends AbstractBotCommand {

    private final CommandRegistry commandRegistry;

    public HelpCommand(@Lazy CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    protected String executeCommand(Update update) {
        return commandRegistry.getHelpMessage();
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Muestra la lista de comandos disponibles";
    }
}