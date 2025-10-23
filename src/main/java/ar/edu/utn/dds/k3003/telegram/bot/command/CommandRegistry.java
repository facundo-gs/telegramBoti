package ar.edu.utn.dds.k3003.telegram.bot.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry Pattern: Registro central de todos los comandos disponibles
 * Facilita la extensibilidad - solo agregar un @Component y automáticamente se registra
 */
@Component
@Slf4j
public class CommandRegistry {

    private final Map<String, BotCommand> commands;

    /**
     * Spring inyecta automáticamente TODOS los beans que implementan BotCommand
     */
    public CommandRegistry(List<BotCommand> commandList) {
        this.commands = new ConcurrentHashMap<>();

        // Registrar todos los comandos
        for (BotCommand command : commandList) {
            registerCommand(command);
        }

        log.info("Command Registry inicializado con {} comandos", commands.size());
    }

    /**
     * Registra un comando
     */
    private void registerCommand(BotCommand command) {
        String commandName = command.getCommandName().toLowerCase();
        commands.put(commandName, command);
        log.info("Comando registrado: /{} - {}", commandName, command.getDescription());
    }

    /**
     * Obtiene un comando por su nombre
     */
    public Optional<BotCommand> getCommand(String commandName) {
        String normalizedName = commandName.toLowerCase().replace("/", "");
        return Optional.ofNullable(commands.get(normalizedName));
    }

    /**
     * Verifica si un comando existe
     */
    public boolean hasCommand(String commandName) {
        String normalizedName = commandName.toLowerCase().replace("/", "");
        return commands.containsKey(normalizedName);
    }

    /**
     * Obtiene todos los comandos disponibles
     */
    public Collection<BotCommand> getAllCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    /**
     * Obtiene la lista de comandos ordenados alfabéticamente
     */
    public List<BotCommand> getSortedCommands() {
        List<BotCommand> sorted = new ArrayList<>(commands.values());
        sorted.sort(Comparator.comparing(BotCommand::getCommandName));
        return sorted;
    }

    /**
     * Genera el mensaje de ayuda con todos los comandos
     */
    public String getHelpMessage() {
        StringBuilder help = new StringBuilder();
        help.append("📋 *Comandos Disponibles:*\n\n");

        for (BotCommand command : getSortedCommands()) {
            help.append("/").append(command.getCommandName())
                    .append(" - ").append(command.getDescription())
                    .append("\n");

            if (command.requiresParameters()) {
                help.append("   _Ejemplo: ").append(command.getUsageExample()).append("_\n");
            }
            help.append("\n");
        }

        return help.toString();
    }
}