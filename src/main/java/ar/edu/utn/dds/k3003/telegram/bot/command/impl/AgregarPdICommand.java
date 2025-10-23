package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.rest_client.PdIRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comando /agregarpdi - Agrega un nuevo PDI a un hecho
 * Uso: /agregarpdi <hechoId> <descripcion> <lugar> <contenido> [imagenUrl]
 */
@Component
public class AgregarPdICommand extends AbstractBotCommand {

    private final PdIRestClient pdIRestClient;

    public AgregarPdICommand(PdIRestClient pdIRestClient) {
        this.pdIRestClient = pdIRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.size() < 4) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /agregarpdi <hechoId> <descripcion> <lugar> <contenido> [imagenUrl]\n\n" +
                            "Ejemplo 1 (sin imagen):\n" +
                            "/agregarpdi HECHO-001 \"Descripcion del evento\" \"Buenos Aires\" \"Contenido del evento\"\n\n" +
                            "Ejemplo 2 (con imagen):\n" +
                            "/agregarpdi HECHO-001 \"Evento con imagen\" \"Buenos Aires\" \"Contenido del evento\" https://imagen.jpg"
            );
        }

        String hechoId = params.get(0);
        String descripcion = params.get(1);
        String lugar = params.get(2);
        String contenido = params.get(3);
        String imagenUrl = params.size() > 4 ? params.get(4) : null;

        try {
            PdIDTO nuevoPdi = new PdIDTO(
                    null,
                    hechoId,
                    descripcion,
                    lugar,
                    LocalDateTime.now(),
                    contenido,
                    imagenUrl
            );

            PdIDTO pdiCreado = pdIRestClient.crearPdI(nuevoPdi);

            StringBuilder response = new StringBuilder();
            response.append(formatSuccess("PDI creado exitosamente!")).append("\n\n");
            response.append("🆔 *ID:* ").append(pdiCreado.id()).append("\n");
            response.append("📌 *Hecho:* ").append(pdiCreado.hechoId()).append("\n");
            response.append("📝 *Descripción:* ").append(pdiCreado.descripcion()).append("\n");
            response.append("📍 *Lugar:* ").append(pdiCreado.lugar()).append("\n");
            response.append("📄 *Contenido:* ").append(pdiCreado.contenido()).append("\n");

            if (imagenUrl != null) {
                response.append("\n🖼️ *Imagen:* Sí\n");
            }

            return response.toString();
        } catch (Exception e) {
            return formatError("No se pudo crear el PDI: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "agregarpdi";
    }

    @Override
    public String getDescription() {
        return "Agrega un nuevo PDI a un hecho";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "Ejemplo 1 (sin imagen):\n" +
                "/agregarpdi HECHO-001 \"Descripcion del evento\" \"Buenos Aires\" \"Contenido del evento\"\n\n" +
                "Ejemplo 2 (con imagen):\n" +
                "/agregarpdi HECHO-001 \"Evento con imagen\" \"Buenos Aires\" \"Contenido del evento\" https://imagen.jpg";
    }
}
