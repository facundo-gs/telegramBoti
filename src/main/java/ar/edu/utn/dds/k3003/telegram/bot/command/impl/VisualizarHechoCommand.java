package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.FuenteRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.PdIRestClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Requerimiento 2: Visualizar un hecho, que incluye visualizar los PDIs y las imágenes
 * Comando: /visualizarhecho <hecho_id>
 *     FUNCIONA!
 */
@Component
public class VisualizarHechoCommand extends AbstractBotCommand {

    private final FuenteRestClient fuenteRestClient;
    private final PdIRestClient pdiRestClient;

    public VisualizarHechoCommand(FuenteRestClient fuenteRestClient, PdIRestClient pdIRestClient) {
        this.fuenteRestClient = fuenteRestClient;
        this.pdiRestClient = pdIRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);

        if (params.isEmpty()) {
            return formatError(
                    "Debes proporcionar el ID del hecho.\n" +
                            "Uso: /visualizarhecho <hecho_id>\n\n" +
                            "Ejemplo: /visualizarhecho \"HECHO-001\""
            );
        }

        String hechoId = params.get(0);

        try {
            // 1. Obtener información del hecho desde Fuente
            HechoDTO hecho = fuenteRestClient.obtenerHecho(hechoId);

            // 2. Obtener PDIs asociados al hecho
            List<PdIDTO> pdis = pdiRestClient.buscarPorHecho(hechoId);

            // 3. Formatear respuesta completa
            StringBuilder response = new StringBuilder();

            response.append("📋 *Visualización Completa del Hecho*\n\n");
            response.append("═══════════════════════════\n");
            response.append("🆔 *ID:* ").append(hecho.id()).append("\n");

            if (hecho.titulo() != null) {
                response.append("📝 *Título:* ").append(hecho.titulo()).append("\n");
            }

            if (hecho.nombreColeccion() != null) {
                response.append("📚 *Colección:* ").append(hecho.nombreColeccion()).append("\n");
            }

            if (hecho.ubicacion() != null) {
                response.append("📍 *Ubicación:* ").append(hecho.ubicacion()).append("\n");
            }

            if (hecho.fecha() != null) {
                response.append("📅 *Fecha:* ").append(hecho.fecha()).append("\n");
            }

            if (hecho.categoria() != null) {
                response.append("⚙️ *Categoría:* ").append(hecho.categoria()).append("\n");
            }

            if (hecho.etiquetas() != null && !hecho.etiquetas().isEmpty()) {
                response.append("🏷️ *Etiquetas:* ")
                        .append(String.join(", ", hecho.etiquetas()))
                        .append("\n");
            }

            if (hecho.origen() != null) {
                response.append("🌐 *Origen:* ").append(hecho.origen()).append("\n");
            }

            response.append("═══════════════════════════\n\n");

            // PDIs asociados
            if (pdis == null || pdis.isEmpty()) {
                response.append("ℹ️ Este hecho no tiene PDIs asociados\n\n");
                response.append("💡 Usa `/agregarpdi ").append(hechoId)
                        .append(" <descripcion> <lugar> <contenido> [imagen_url]` para agregar uno");
            } else {
                response.append("📄 *PDIs Asociados:* ").append(pdis.size()).append("\n\n");

                int count = 0;
                for (PdIDTO pdi : pdis) {
                    count++;

                    response.append("▸ *PDI #").append(count).append("*\n");
                    response.append("  🆔 ID: ").append(pdi.id()).append("\n");

                    if (pdi.descripcion() != null) {
                        response.append("  📝 Descripción: ").append(pdi.descripcion()).append("\n");
                    }

                    if (pdi.lugar() != null) {
                        response.append("  📍 Lugar: ").append(pdi.lugar()).append("\n");
                    }

                    if (pdi.momento() != null) {
                        response.append("  ⏰ Momento: ").append(pdi.momento()).append("\n");
                    }

                    if (pdi.imagenUrl() != null && !pdi.imagenUrl().isEmpty()) {
                        response.append("  🖼️ *Imagen disponible*\n");
                        response.append("  📎 URL: ").append(pdi.imagenUrl()).append("\n");
                    }

                    if (pdi.estadoProcesamiento() != null) {
                        response.append("  ⚙️ Estado: ").append(pdi.estadoProcesamiento()).append("\n");
                    }

                    if (pdi.ocrText() != null && !pdi.ocrText().isEmpty()) {
                        String ocrPreview = pdi.ocrText().length() > 50
                                ? pdi.ocrText().substring(0, 50) + "..."
                                : pdi.ocrText();
                        response.append("  📝 OCR: ").append(ocrPreview).append("\n");
                    }

                    if (pdi.etiquetasIA() != null && !pdi.etiquetasIA().isEmpty()) {
                        response.append("  🏷️ Tags: ").append(String.join(", ", pdi.etiquetasIA())).append("\n");
                    }

                    response.append("\n");

                    // Limitar a 5 PDIs para no exceder límite de Telegram
                    if (count >= 5 && pdis.size() > 5) {
                        response.append("... y ").append(pdis.size() - 5).append(" PDI(s) más\n\n");
                        break;
                    }
                }

            }

            return response.toString();

        } catch (Exception e) {
            return formatError("Error al visualizar hecho: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "visualizarhecho";
    }

    @Override
    public String getDescription() {
        return "Visualiza un hecho completo con todos sus PDIs e imágenes";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "/visualizarhecho 1";
    }
}