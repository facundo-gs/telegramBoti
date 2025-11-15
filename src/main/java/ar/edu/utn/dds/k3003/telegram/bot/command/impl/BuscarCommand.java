package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.BusquedaResponseDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.BusquedaResultadoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.BusquedaRestClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Comando: /buscar <consulta> [tag:valor]
 *
 * Ejemplos:
 * /buscar incendio
 * /buscar incendio tag:CABA
 * /buscar "manifestación pacífica" tag:CABA tag:urgente
 */
@Slf4j
@Component
public class BuscarCommand extends AbstractBotCommand {

    private final BusquedaRestClient busquedaRestClient;
    @Getter
    private final Map<Long, BusquedaCache> cacheBusquedas = new ConcurrentHashMap<>();

    public BuscarCommand(BusquedaRestClient busquedaRestClient) {
        this.busquedaRestClient = busquedaRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);
        Long chatId = update.getMessage().getChatId();

        if (params.isEmpty()) {
            return formatError(
                    "Debes proporcionar una consulta de búsqueda.\n\n" +
                            "Uso: /buscar <consulta> [tag:valor]\n\n" +
                            "Ejemplos:\n" +
                            "• /buscar incendio\n" +
                            "• /buscar \"manifestación pacífica\"\n" +
                            "• /buscar incendio tag:CABA\n" +
                            "• /buscar protesta tag:CABA tag:urgente\n\n" +
                            "Para ver más resultados usa:\n" +
                            "• /siguiente\n" +
                            "• /anterior"
            );
        }

        try {
            ParseResult parseResult = parsearParametros(params);

            BusquedaResponseDTO response = busquedaRestClient.buscar(
                    parseResult.consulta,
                    parseResult.tags,
                    0,
                    10
            );

            cacheBusquedas.put(chatId, new BusquedaCache(
                    parseResult.consulta,
                    parseResult.tags,
                    response
            ));

            return formatearResultados(response, parseResult.consulta, parseResult.tags);

        } catch (Exception e) {
            log.error("Error en búsqueda: {}", e.getMessage(), e);
            return formatError("Error al realizar la búsqueda: " + e.getMessage());
        }
    }

    private ParseResult parsearParametros(List<String> params) {
        StringBuilder consultaBuilder = new StringBuilder();
        List<String> tags = new ArrayList<>();

        for (String param : params) {
            if (param.startsWith("tag:")) {
                String tag = param.substring(4).trim();
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            } else {
                if (consultaBuilder.length() > 0) {
                    consultaBuilder.append(" ");
                }
                consultaBuilder.append(param);
            }
        }

        String consulta = consultaBuilder.toString().trim();
        if (consulta.isEmpty()) {
            throw new IllegalArgumentException("La consulta no puede estar vacía");
        }

        return new ParseResult(consulta, tags.isEmpty() ? null : tags);
    }

    public String formatearResultados(BusquedaResponseDTO response, String consulta, List<String> tags) {
        StringBuilder sb = new StringBuilder();

        sb.append("🔍 *Resultados de búsqueda*\n");
        sb.append("═══════════════════════════\n\n");

        sb.append("📝 *Consulta:* ").append(consulta).append("\n");

        if (tags != null && !tags.isEmpty()) {
            sb.append("🏷️ *Tags:* ").append(String.join(", ", tags)).append("\n");
        }

        sb.append("📊 *Total:* ").append(response.totalResultados()).append(" resultado(s)\n");
        sb.append("📄 *Página:* ").append(response.paginaActual() + 1)
                .append("/").append(response.totalPaginas()).append("\n\n");

        if (response.resultados().isEmpty()) {
            sb.append("❌ No se encontraron resultados.\n\n");
            sb.append("💡 *Sugerencias:*\n");
            sb.append("• Intenta con palabras más generales\n");
            sb.append("• Verifica la ortografía\n");
            sb.append("• Quita los filtros de tags\n");
        } else {
            int numero = response.paginaActual() * response.tamanio() + 1;

            for (BusquedaResultadoDTO resultado : response.resultados()) {
                sb.append("*").append(numero++).append(". ").append(resultado.titulo()).append("*\n");

                sb.append("   🆔 ID: ").append(resultado.id()).append("\n");
                sb.append("   📚 Colección: ").append(resultado.nombreColeccion()).append("\n");

                if (resultado.descripcion() != null && !resultado.descripcion().isBlank()) {
                    String desc = resultado.descripcion();
                    if (desc.length() > 100) {
                        desc = desc.substring(0, 97) + "...";
                    }
                    sb.append("   📝 ").append(desc).append("\n");
                }

                if (resultado.ubicacion() != null) {
                    sb.append("   📍 ").append(resultado.ubicacion()).append("\n");
                }

                if (resultado.fecha() != null) {
                    sb.append("   📅 ").append(resultado.fecha().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                }

                if (resultado.etiquetas() != null && !resultado.etiquetas().isEmpty()) {
                    sb.append("   🏷️ ").append(String.join(", ", resultado.etiquetas())).append("\n");
                }

                if (resultado.etiquetasIA() != null && !resultado.etiquetasIA().isEmpty()) {
                    sb.append("   🤖 IA: ").append(String.join(", ", resultado.etiquetasIA())).append("\n");
                }

                sb.append("\n");
            }

            sb.append("═══════════════════════════\n\n");

            if (response.totalPaginas() > 1) {
                sb.append("📖 *Navegación:*\n");

                if (response.tieneAnterior()) {
                    sb.append("• /anterior - Página anterior\n");
                }
                if (response.tieneSiguiente()) {
                    sb.append("• /siguiente - Página siguiente\n");
                }
            }

            sb.append("\n💡 Usa `/visualizarhecho <id>` para ver detalles completos");
        }

        return sb.toString();
    }

    @Override
    public String getCommandName() {
        return "buscar";
    }

    @Override
    public String getDescription() {
        return "Busca hechos por palabra clave y tags";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "/buscar incendio tag:CABA";
    }

    private record ParseResult(String consulta, List<String> tags) {}

    public static class BusquedaCache {
        public final String consulta;
        public final List<String> tags;
        public BusquedaResponseDTO ultimaRespuesta;
        public final long timestamp;

        public BusquedaCache(String consulta, List<String> tags, BusquedaResponseDTO respuesta) {
            this.consulta = consulta;
            this.tags = tags;
            this.ultimaRespuesta = respuesta;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean esValido() {
            return System.currentTimeMillis() - timestamp < 600_000; // 10 minutos
        }
    }

}