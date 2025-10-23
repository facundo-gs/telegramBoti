package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.FuenteRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando /agregarhecho - Agrega un nuevo hecho a una fuente
 * Uso: /agregarhecho <descripcion> <lugar>
 */
@Component
public class AgregarHechoCommand extends AbstractBotCommand {

    private final FuenteRestClient fuenteRestClient;

    public AgregarHechoCommand(FuenteRestClient fuenteRestClient) {
        this.fuenteRestClient = fuenteRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.size() < 2) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /agregarhecho <descripcion> <lugar>\n\n" +
                            "Ejemplo:\n" +
                            "/agregarhecho Manifestacion_pacifica Buenos_Aires"
            );
        }

        String descripcion = params.get(0);
        String lugar = params.get(1);

        try {
            // Preparar los datos para enviar al RestClient
            Map<String, Object> hechoData = new HashMap<>();
            hechoData.put("titulo", descripcion); // tu DTO usa 'titulo' en vez de 'descripcion'
            hechoData.put("ubicacion", lugar);    // tu DTO usa 'ubicacion' en vez de 'lugar'
            hechoData.put("fecha", LocalDateTime.now().toString());
            hechoData.put("origen", getUsername(update));

            // Crear el hecho
            HechoDTO hechoCreado = fuenteRestClient.crearHecho(hechoData);

            // Construir respuesta para Telegram
            StringBuilder response = new StringBuilder();
            response.append(formatSuccess("Hecho creado exitosamente!")).append("\n\n");
            response.append("🆔 *ID:* ").append(hechoCreado.id()).append("\n");
            response.append("📝 *Título:* ").append(hechoCreado.titulo()).append("\n");
            response.append("📍 *Ubicación:* ").append(hechoCreado.ubicacion()).append("\n");

            if (hechoCreado.fecha() != null) {
                response.append("📅 *Fecha:* ").append(hechoCreado.fecha()).append("\n");
            }

            return response.toString();

        } catch (Exception e) {
            return formatError("Error al crear hecho: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "agregarhecho";
    }

    @Override
    public String getDescription() {
        return "Agrega un nuevo hecho a una fuente";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "/agregarhecho Descripcion Lugar";
    }
}
