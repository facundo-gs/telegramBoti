package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.rest_client.SolicitudesRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.SolicitudModificacionRequestDTO;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

// Uso: /cambiarestado <solicitudId> <estado> [descripcion]

@Component
public class CambiarEstadoSolicitudCommand extends AbstractBotCommand {

    private final SolicitudesRestClient solicitudesRestClient;

    public CambiarEstadoSolicitudCommand(SolicitudesRestClient solicitudesRestClient) {
        this.solicitudesRestClient = solicitudesRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.size() < 2) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /cambiarestado <solicitudId> <estado> [descripcion]\n\n" +
                            "Valores válidos para <estado>: " + String.join(", ",
                            EstadoSolicitudBorradoEnum.valuesAsString()) + "\n\n" +
                            "Ejemplo:\n" +
                            "/cambiarestado 5 ACEPTADA \"Descripción actualizada\""
            );
        }

        String solicitudId = params.get(0);
        String estadoStr = params.get(1);
        String descripcion = params.size() > 2 ? params.get(2) : null;

        try {
            EstadoSolicitudBorradoEnum estado = EstadoSolicitudBorradoEnum.valueOf(estadoStr.toUpperCase());

            SolicitudDTO solicitudActualizada = solicitudesRestClient.modificarEstado(
                    new SolicitudModificacionRequestDTO(solicitudId, estado, descripcion)
            );

            StringBuilder response = new StringBuilder();
            response.append(formatSuccess("Estado de la solicitud actualizado exitosamente!")).append("\n\n");
            response.append("🆔 *ID:* ").append(solicitudActualizada.id()).append("\n");
            response.append("📝 *Descripción:* ").append(solicitudActualizada.descripcion()).append("\n");
            response.append("⚙️ *Estado:* ").append(solicitudActualizada.estado()).append("\n");
            response.append("🚦 *Hecho ID:* ").append(solicitudActualizada.hechoId()).append("\n");

            return response.toString();

        } catch (IllegalArgumentException e) {
            return formatError("Estado inválido. Los valores válidos son: " +
                    String.join(", ", EstadoSolicitudBorradoEnum.valuesAsString()));
        } catch (Exception e) {
            return formatError("No se pudo actualizar la solicitud: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "cambiarestado";
    }

    @Override
    public String getDescription() {
        return "Cambia el estado de una solicitud existente";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "/cambiarestado 5 ACEPTADA \"Descripción actualizada\"";
    }
}
