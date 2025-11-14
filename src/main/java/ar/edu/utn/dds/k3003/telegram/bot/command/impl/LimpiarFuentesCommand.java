package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.AgregadorRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LimpiarFuentesCommand extends AbstractBotCommand {
    private final AgregadorRestClient agregador;

    @Override
    protected String executeCommand(Update update) {
        try {
            agregador.limpiarFuentes();
            return "✅ Fuentes limpiadas correctamente";
        } catch (Exception e) {
            return formatError("No se pudieron limpiar las fuentes: " + e.getMessage());
        }
    }

    @Override public String getCommandName() { return "limpiarfuentes"; }
    @Override public String getDescription() { return "Borra todas las fuentes"; }
}
