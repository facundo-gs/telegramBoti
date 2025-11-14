package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.rest_client.SolicitudesRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

// Uso: /crearsolicitud <hecho_id> <descripcion>
@Component
@RequiredArgsConstructor
public class CrearSolicitudCommand extends AbstractBotCommand {

    private final SolicitudesRestClient solicitudesRestClient;

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.size() < 2) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /crearsolicitud <hechoId> <descripcion>\n\n" +
                            "Ejemplo:\n" +
                            "/crearsolicitud \"2\" \"Una descripción increíble\""
            );
        }

        String hechoId = params.get(0);
        String descripcion = params.get(1);

        try {
            SolicitudDTO nuevaSolicitud = new SolicitudDTO(
                    null,
                    descripcion,
                    null,
                    hechoId
            );

            SolicitudDTO solicitudCreada = solicitudesRestClient.agregar(nuevaSolicitud);

            return formatSuccess("Solicitud creada exitosamente!") + "\n\n" +
                    "🆔 *ID:* " + solicitudCreada.id() + "\n" +
                    "📄 *Descripción:* " + solicitudCreada.descripcion() + "\n" +
                    "⚙️ *Estado:* " + solicitudCreada.estado() + "\n" +
                    "🚦 *Hecho ID:* " + solicitudCreada.hechoId() + "\n";
        } catch (Exception e) {
            String userMessage = extractMessageFromException(e);
            return formatError("Error al crear solicitud: " + userMessage);
        }
    }

    @Override
    public String getCommandName() {
        return "crearsolicitud";
    }

    @Override
    public String getDescription() {
        return "Crea una nueva solicitud";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "/crearsolicitud 'HechoId' 'Descripcion'";
    }
}
