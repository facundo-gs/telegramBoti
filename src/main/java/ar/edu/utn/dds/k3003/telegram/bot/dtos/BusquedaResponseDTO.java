package ar.edu.utn.dds.k3003.telegram.bot.dtos;

import java.util.List;

/**
 * DTO para respuesta paginada de búsqueda (usado por el bot).
 */
public record BusquedaResponseDTO(
        List<BusquedaResultadoDTO> resultados,
        int paginaActual,
        int tamanio,
        long totalResultados,
        int totalPaginas,
        boolean tieneSiguiente,
        boolean tieneAnterior
) {
}