package ar.edu.utn.dds.k3003.telegram.bot.dtos;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para un resultado individual de búsqueda (usado por el bot).
 */
public record BusquedaResultadoDTO(
        String id,
        String titulo,
        String nombreColeccion,
        String descripcion,
        String ubicacion,
        String categoria,
        LocalDateTime fecha,
        List<String> etiquetas,
        List<String> etiquetasIA,
        String origen,
        double score
) {
}