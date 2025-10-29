package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.CategoriaHechoEnum;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.FuenteRestClient;
import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

// Uso: /agregarhecho <nombre_coleccion> <titulo> <ubicacion> [categoria]
@Slf4j
@Component
public class AgregarHechoCommand extends AbstractBotCommand {

    private final FuenteRestClient fuenteRestClient;

    public AgregarHechoCommand(FuenteRestClient fuenteRestClient) {
        this.fuenteRestClient = fuenteRestClient;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> params = extractParameters(update);
        log.info("AgregarHechoCommand - Parámetros recibidos: {}", params);
        if (params.size() < 3) {
            return formatError(
                    "Faltan parámetros.\n" +
                            "Uso: /agregarhecho <nombre_coleccion> <titulo> <ubicacion> [categoria]\n\n" +
                            "Ejemplo 1 (sin categoría):\n" +
                            "/agregarhecho coleccion1 \"Manifestación pacífica\" \"Buenos Aires\"\n\n" +
                            "Ejemplo 2 (con categoría):\n" +
                            "/agregarhecho coleccion1 \"Charla sobre IA\" \"UTN Buenos Aires\" EDUCACIONAL"
            );
        }

        String nombreColeccion = params.get(0);
        String titulo = params.get(1);
        String ubicacion = params.get(2);

        // Si el usuario pasó una categoría, la intentamos parsear. Caso contrario, usamos OTRO.
        CategoriaHechoEnum categoria = CategoriaHechoEnum.OTRO;
        if (params.size() >= 4) {
            try {
                categoria = CategoriaHechoEnum.valueOf(params.get(3).toUpperCase());
            } catch (IllegalArgumentException e) {
                return formatError("Categoría inválida. Categorías válidas: " +
                        "ENTRETENIMIENTO, EDUCACIONAL, POLITICO, DESASTRE, OTRO");
            }
        }

        try {
            HechoDTO nuevoHecho = new HechoDTO(
                    null,                        // id (generado por backend)
                    nombreColeccion,              // ✅ nombre_coleccion indicado por el usuario
                    titulo,
                    Collections.emptyList(),      // etiquetas (vacías)
                    categoria,                    // categoría seleccionada
                    ubicacion,
                    LocalDateTime.now(),
                    getUsername(update)           // origen: usuario Telegram
            );
            log.info("AgregarHechoCommand - Hecho creado: {}", nuevoHecho);
            HechoDTO hechoCreado = fuenteRestClient.crearHecho(nuevoHecho);

            StringBuilder response = new StringBuilder();
            response.append(formatSuccess("Hecho creado exitosamente!")).append("\n\n");
            response.append("🆔 *ID:* ").append(hechoCreado.id()).append("\n");
            response.append("📚 *Colección:* ").append(hechoCreado.nombreColeccion()).append("\n");
            response.append("📝 *Título:* ").append(hechoCreado.titulo()).append("\n");
            response.append("📍 *Ubicación:* ").append(hechoCreado.ubicacion()).append("\n");
            response.append("🏷️ *Categoría:* ").append(hechoCreado.categoria()).append("\n");

            if (hechoCreado.fecha() != null) {
                response.append("📅 *Fecha:* ").append(hechoCreado.fecha()).append("\n");
            }

            if (hechoCreado.origen() != null) {
                response.append("👤 *Origen:* ").append(hechoCreado.origen()).append("\n");
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
        return "Agrega un nuevo hecho a una fuente indicando la colección, título, ubicación y categoría opcional.";
    }

    @Override
    public boolean requiresParameters() {
        return true;
    }

    @Override
    public String getUsageExample() {
        return "Ejemplo 1 (sin categoría):\n" +
                "/agregarhecho coleccion1 \"Manifestación pacífica\" \"Buenos Aires\"\n\n" +
                "Ejemplo 2 (con categoría):\n" +
                "/agregarhecho coleccion1 \"Charla sobre IA\" \"UTN Buenos Aires\" EDUCACIONAL";
    }
}
