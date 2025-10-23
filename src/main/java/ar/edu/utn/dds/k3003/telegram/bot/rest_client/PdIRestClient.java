package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.PdIDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class PdIRestClient {

    private final RestClient restClient;

    public PdIRestClient(RestClient.Builder builder) {
        String endpoint = System.getenv().getOrDefault("DDS_PDI", "http://localhost:8081");
        this.restClient = builder.baseUrl(endpoint).build();
    }

    /**
     * Busca los PDIs asociados a un hecho específico.
     * Endpoint: GET /api/PdIs?hecho=<hechoId>
     */
    public List<PdIDTO> buscarPorHecho(String hechoId) {
        try {
            log.info("Buscando PDIs para hecho {}", hechoId);

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/PdIs")
                            .queryParam("hecho", hechoId)
                            .build())
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<List<PdIDTO>>() {});
        } catch (Exception e) {
            log.error("Error buscando PDIs para hecho {}: {}", hechoId, e.getMessage());
            throw new RuntimeException("Error conectando con PdI: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo PdI
     * Endpoint: POST /api/PdIs
     */
    public PdIDTO crearPdI(PdIDTO nuevoPdi) {
        try {
            log.info("Creando nuevo PdI para hecho {}", nuevoPdi.hechoId());

            return restClient.post()
                    .uri("/api/PdIs")
                    .body(nuevoPdi)
                    .retrieve()
                    .body(PdIDTO.class);
        } catch (Exception e) {
            log.error("Error creando PdI para hecho {}: {}", nuevoPdi.hechoId(), e.getMessage());
            throw new RuntimeException("Error conectando con PdI: " + e.getMessage(), e);
        }
    }

}

