package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.AgregadorRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgregarFuenteCommand extends AbstractBotCommand {
    private final AgregadorRestClient agregador;

    @Override
    protected String executeCommand(Update update) {
        List<String> p = extractParameters(update);

        if (p.size() < 2) {
            return formatError("""
                Faltan parámetros.
                Uso: /agregarfuente "Nombre" "https://endpoint"
                Ejemplo: /agregarfuente "Fuente 1" "https://dsifuente.onrender.com/"
                """);
        }

        String nombre = p.get(0).trim();
        String endpoint = p.get(1).trim();

        if (nombre.isEmpty() || endpoint.isEmpty()) {
            return formatError("Nombre y endpoint no pueden estar vacíos.");
        }

        try {
            agregador.agregarFuente(nombre, endpoint);
            return "✅ Fuente agregada";
        } catch (Exception e) {
            return formatError("No se pudo agregar la fuente: " + e.getMessage());
        }
    }

    @Override public String getCommandName() { return "agregarfuente"; }
    @Override public String getDescription() { return "Agrega una nueva fuente"; }
    @Override public boolean requiresParameters() { return true; }
    @Override public String getUsageExample() { return "/agregarfuente \"Fuente 1\" \"https://dsifuente.onrender.com/\""; }
}
