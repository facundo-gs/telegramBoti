package ar.edu.utn.dds.k3003.telegram.bot.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CommandRegistry {

    private final Map<String, BotCommand> commands;

    public CommandRegistry(List<BotCommand> commandList) {
        this.commands = new ConcurrentHashMap<>();

        for (BotCommand command : commandList) {
            registerCommand(command);
        }

        log.info("Command Registry inicializado con {} comandos", commands.size());
    }

    private void registerCommand(BotCommand command) {
        String commandName = command.getCommandName().toLowerCase();
        commands.put(commandName, command);
        log.info("Comando registrado: /{} - {}", commandName, command.getDescription());
    }

    public Optional<BotCommand> getCommand(String commandName) {
        String normalizedName = commandName.toLowerCase().replace("/", "");
        return Optional.ofNullable(commands.get(normalizedName));
    }

    public Collection<BotCommand> getAllCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    public List<BotCommand> getSortedCommands() {
        List<BotCommand> sorted = new ArrayList<>(commands.values());
        sorted.sort(Comparator.comparing(BotCommand::getCommandName));
        return sorted;
    }

}