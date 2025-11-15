package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.BusquedaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Cliente REST para el servicio de búsqueda.
 */
@Component
@Slf4j
public class BusquedaRestClient {

    private final RestClient restClient;

    public BusquedaRestClient(RestClient.Builder builder) {
        String endpoint = System.getenv().getOrDefault("DDS_BUSQUEDA", "http://localhost:8085");
        this.restClient = builder.baseUrl(endpoint).build();
    }

    /**
     * Realiza una búsqueda de hechos.
     *
     * @param consulta Texto a buscar
     * @param tags Lista de tags para filtrar (criterio AND)
     * @param pagina Número de página (0-indexed)
     * @param tamanio Tamaño de página
     * @return Resultados de búsqueda paginados
     */
    public BusquedaResponseDTO buscar(String consulta, List<String> tags, int pagina, int tamanio) {
        try {
            log.info("Buscando: '{}', tags: {}, página: {}", consulta, tags, pagina);

            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromPath("/api/busqueda")
                    .queryParam("q", consulta)
                    .queryParam("page", pagina)
                    .queryParam("size", tamanio);

            if (tags != null && !tags.isEmpty()) {
                builder.queryParam("tags", String.join(",", tags));
            }

            String uri = builder.build().toUriString();

            BusquedaResponseDTO response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(BusquedaResponseDTO.class);

            log.info("Búsqueda exitosa: {} resultados",
                    response != null ? response.totalResultados() : 0);

            return response;

        } catch (Exception e) {
            log.error("❌ Error en búsqueda: {}", e.getMessage(), e);
            throw new RuntimeException("Error conectando con servicio de búsqueda: " + e.getMessage(), e);
        }
    }
}