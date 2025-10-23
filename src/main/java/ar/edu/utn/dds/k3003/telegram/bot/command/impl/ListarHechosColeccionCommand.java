package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.AgregadorRestClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Requerimiento 1: Listar hechos de una colección del agregador
 * Comando: /listarhechos <coleccionId>
 */
@Component
public class ListarHechosColeccionCommand extends AbstractBotCommand {

    private final AgregadorRestClient agregadorRestClient;

    public ListarHechosColeccionCommand(AgregadorRestClient agregadorRestClient) {
        this.agregadorRestClient = agregadorRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.isEmpty()) {
            return formatError("""
                    Debes proporcionar el ID o nombre de la colección.
                    Uso: /listarhechos <coleccionId>

                    Ejemplo: /listarhechos COL-001
                    """);
        }

        String coleccionId = params.get(0);

        try {
            // Obtener hechos de la colección
            List<HechoDTO> hechos = agregadorRestClient.obtenerHechosDeColeccion(coleccionId);

            if (hechos == null || hechos.isEmpty()) {
                return formatInfo("La colección *" + coleccionId + "* no tiene hechos registrados.");
            }

            StringBuilder response = new StringBuilder();
            response.append("📚 *Colección:* ").append(coleccionId).append("\n");
            response.append("📋 *Hechos encontrados:* ").append(hechos.size()).append("\n\n");

            for (HechoDTO hecho : hechos) {
                response.append("🔹 *ID:* ").append(hecho.id()).append("\n");
                response.append("   *Título:* ").append(nullToEmpty(hecho.titulo())).append("\n");

                if (hecho.categoria() != null) {
                    response.append("   *Categoría:* ").append(hecho.categoria()).append("\n");
                }

                if (hecho.fecha() != null) {
                    response.append("   *Fecha:* ").append(hecho.fecha()).append("\n");
                }

                if (hecho.ubicacion() != null) {
                    response.append("   *Ubicación:* ").append(hecho.ubicacion()).append("\n");
                }

                if (hecho.etiquetas() != null && !hecho.etiquetas().isEmpty()) {
                    response.append("   *Etiquetas:* ").append(String.join(", ", hecho.etiquetas())).append("\n");
                }

                response.append("\n");
            }

            return response.toString();

        } catch (Exception e) {
            return formatError("Error al obtener hechos de la colección *" + coleccionId + "*: " + e.getMessage());
        }
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "(sin dato)";
    }

    @Override
    public String getCommandName() {
        return "listarhechos";
    }

    @Override
    public String getDescription() {
        return "Lista todos los hechos de una colección del agregador";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "/listarhechos COL-001";
    }
}
