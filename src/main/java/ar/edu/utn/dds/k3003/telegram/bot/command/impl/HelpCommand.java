package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.command.BotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.command.CommandRegistry;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class HelpCommand extends AbstractBotCommand {

    private final CommandRegistry commandRegistry;

    public HelpCommand(@Lazy CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    protected String executeCommand(Update update) {
        // Mapear nombres de comando a secciones
        Set<String> agregador = setOf(
                "agregarfuente", "listarfuentes", "limpiarfuentes",
                "consenso", "listarhechos"
        );

        Set<String> hechosYpdis = setOf(
                "agregarhecho", "agregarpdi", "visualizarhecho"
        );

        Set<String> solicitudes = setOf(
                "crearsolicitud", "cambiarestado"
        );

        Set<String> sistema = setOf(
                "start", "help"
        );

        // Índice rápido por nombre
        Map<String, BotCommand> byName = commandRegistry.getAllCommands().stream()
                .collect(Collectors.toMap(BotCommand::getCommandName, c -> c));

        StringBuilder out = new StringBuilder();

        appendSection(out, "-- Sección Agregador --",
                orderedFilter(agregador, byName));
        appendSection(out, "-- Hechos & PDIs --",
                orderedFilter(hechosYpdis, byName));
        appendSection(out, "-- Solicitudes --",
                orderedFilter(solicitudes, byName));
        appendSection(out, "-- Sistema --",
                orderedFilter(sistema, byName));

        // Comandos no clasificados (por si aparece alguno nuevo)
        List<BotCommand> unclassified = commandRegistry.getSortedCommands().stream()
                .filter(c -> !agregador.contains(c.getCommandName())
                        && !hechosYpdis.contains(c.getCommandName())
                        && !solicitudes.contains(c.getCommandName())
                        && !sistema.contains(c.getCommandName()))
                .toList();

        if (!unclassified.isEmpty()) {
            appendSection(out, "-- Otros --", unclassified);
        }
        return out.toString();
    }

    private static void appendSection(StringBuilder out, String title, List<BotCommand> commands) {
        if (commands == null || commands.isEmpty()) return;

        out.append("*").append(title).append("*\n\n"); // título en negrita (Markdown)
        for (BotCommand c : commands) {
            out.append("/")
               .append(c.getCommandName())
               .append(" - ")
               .append(c.getDescription() != null ? c.getDescription() : "")
               .append("\n");

            if (c.requiresParameters()) {
                String usage = c.getUsageExample();
                if (usage != null && !usage.isBlank()) {
                    out.append("   _Ejemplo/s:_ ")
                       .append(usage)
                       .append("\n");
                }
            }
            out.append("\n");
        }
        out.append("\n");
    }

    private static List<BotCommand> orderedFilter(Set<String> names, Map<String, BotCommand> byName) {
        List<BotCommand> out = new ArrayList<>();
        for (String n : names) {
            BotCommand c = byName.get(n);
            if (c != null) out.add(c);
        }
        return out;
    }

    private static Set<String> setOf(String... items) {
        return new LinkedHashSet<>(Arrays.asList(items));
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Muestra la lista de comandos disponibles, agrupados por sección";
    }
}
