package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class AgregadorRestClient {

    private final RestClient restClient;

    public AgregadorRestClient(RestClient.Builder builder) {
        String endpoint = System.getenv().getOrDefault("DDS_AGREGADOR", "http://localhost:8082");
        this.restClient = builder.baseUrl(endpoint).build();
    }

    /**
     * Llama al endpoint GET /coleccion/{nombre}/hechos
     * para obtener los hechos asociados a una colección.
     */
    public List<HechoDTO> obtenerHechosDeColeccion(String nombreColeccion) {
        try {
            log.info("Obteniendo hechos de la colección {}", nombreColeccion);

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/coleccion/{nombre}/hechos")
                            .build(nombreColeccion))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<HechoDTO>>() {});

        } catch (RestClientException e) {
            log.error("Error al obtener hechos de la colección {}: {}", nombreColeccion, e.getMessage());
            throw new RuntimeException("Error al obtener hechos de la colección " + nombreColeccion + ": " + e.getMessage(), e);
        }
    }

}
