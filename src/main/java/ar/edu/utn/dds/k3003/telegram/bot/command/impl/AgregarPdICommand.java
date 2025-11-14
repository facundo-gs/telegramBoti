package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.rest_client.FuenteRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;


// Uso: /agregarpdi <hecho_id> <descripcion> <lugar> <contenido> [imagenUrl]
@Slf4j
@Component
@RequiredArgsConstructor
public class AgregarPdICommand extends AbstractBotCommand {

    private final FuenteRestClient fuenteRestClient;

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);
        log.info("AgregarPdICommand - Parámetros recibidos: {}", params);
        if (params.size() < 4) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /agregarpdi <hecho_id> <descripcion> <lugar> <contenido> [imagenUrl]\n\n" +
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
            log.info("AgregarPdICommand - nuevo PDI: {}", nuevoPdi);
            PdIDTO pdiCreado = fuenteRestClient.crearPdI(nuevoPdi);
            log.info("AgregarPdICommand - PDI enviado a fuente: {}", pdiCreado);

            return formatSuccess("Procesando pieza de información!") + "\n\n" +
                    "📌 Hecho: " + pdiCreado.hechoId() + "\n" +
                    "📝 Descripción: " + pdiCreado.descripcion() + "\n" +
                    "📍 Lugar: " + pdiCreado.lugar() + "\n" +
                    "📄 Contenido: " + pdiCreado.contenido() + "\n";
        } catch (Exception e) {
            String userMessage = extractMessageFromException(e);
            return formatError("Error al procesar pdi: " + userMessage);
        }
    }

    @Override
    public String getCommandName() {
        return "agregarpdi";
    }

    @Override
    public String getDescription() {
        return "Agrega una pieza de información a un hecho";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "\nEjemplo 1 (sin imagen):\n" +
                "/agregarpdi HECHO-001 \"Descripcion\" \"Ubicación\" \"Contenido\"\n\n" +
                "Ejemplo 2 (con imagen):\n" +
                "/agregarpdi HECHO-001 \"Descripción\" \"Ubicación\" \"Contenido\" https://imagen.jpg";
    }
}
