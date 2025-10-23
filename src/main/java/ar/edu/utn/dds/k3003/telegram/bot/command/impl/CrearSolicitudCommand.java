package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.rest_client.SolicitudesRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comando /crearsolicitud - Agrega una nueva solicitud
 * Uso: /crearsolicitud <hechoId> <descripcion>
 */
@Component
public class CrearSolicitudCommand extends AbstractBotCommand {

    private final SolicitudesRestClient solicitudesRestClient;

    public CrearSolicitudCommand(SolicitudesRestClient solicitudesRestClient) {
        this.solicitudesRestClient = solicitudesRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.size() < 4) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /crearsolicitud <hechoId> <descripcion> \n\n" +
                            "Ejemplo:\n" +
                            "/crearsolicitud '2' 'Una descripción increible' "
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

            StringBuilder response = new StringBuilder();
            response.append(formatSuccess("Solicitud creada exitosamente!")).append("\n\n");
            response.append("🆔 *ID:* ").append(solicitudCreada.id()).append("\n");
            response.append("📄 *Descripción:* ").append(solicitudCreada.descripcion()).append("\n");
            response.append("⚙️ *Estado:* ").append(solicitudCreada.estado()).append("\n");
            response.append("🚦 *Hecho ID:* ").append(solicitudCreada.hechoId()).append("\n");

            return response.toString();
        } catch (Exception e) {
            return formatError("No se pudo crear la solicitud: " + e.getMessage());
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
        return "/crearsolicitud 'Titulo' 'Descripcion' 'Tipo' 'Prioridad'";
    }
}
