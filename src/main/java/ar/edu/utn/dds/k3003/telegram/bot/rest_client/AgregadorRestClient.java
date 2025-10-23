package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

@Component
public class AgregadorRestClient {

    private final RestClient restClient;
    private String endpoint;


    public AgregadorRestClient(RestClient.Builder builder) {
        String endpoint = System.getenv().getOrDefault("DDS_AGREGADOR", "http://localhost:8082");
        this.restClient = builder.baseUrl(endpoint).build();
    }

    /**
     * Llama al endpoint GET /coleccion/{nombre}/hechos
     * para obtener los hechos asociados a una colección.
     */
    public List<HechoDTO> obtenerHechosDeColeccion(String nombreColeccion) {
        String url = endpoint + "/coleccion/" + nombreColeccion + "/hechos";

        try {
            ResponseEntity<HechoDTO[]> response = restClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(HechoDTO[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return List.of(response.getBody());
            }

            return Collections.emptyList();
        } catch (RestClientException e) {
            throw new RuntimeException("Error al obtener hechos de la colección " + nombreColeccion + ": " + e.getMessage(), e);
        }
    }

}
