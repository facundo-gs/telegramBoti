package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.FuenteDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.ConsensoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
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

    // ===== Hechos por colección =====
    public List<HechoDTO> obtenerHechosDeColeccion(String nombreColeccion) {
        try {
            log.info("Obteniendo hechos de la colección {}", nombreColeccion);
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/coleccion/{nombre}/hechos").build(nombreColeccion))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<HechoDTO>>() {});
        } catch (RestClientException e) {
            throw new RuntimeException("Error al obtener hechos de la colección " + nombreColeccion + ": " + e.getMessage(), e);
        }
    }

    // ===== Fuentes =====
public void agregarFuente(String nombre, String endpoint) {
    try {
        var body = java.util.Map.of(
                "nombre", nombre,
                "endpoint", endpoint
        );
        restClient.post()
                .uri("/fuentes")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity(); // <- NO esperamos body (puede ser 204/201 sin contenido)
    } catch (org.springframework.web.client.RestClientException e) {
        throw new RuntimeException("Error al agregar fuente: " + e.getMessage(), e);
    }
}


    public List<FuenteDTO> listarFuentes() {
        try {
            return restClient.get()
                    .uri("/fuentes")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<FuenteDTO>>() {});
        } catch (RestClientException e) {
            throw new RuntimeException("Error al listar fuentes: " + e.getMessage(), e);
        }
    }

    public void limpiarFuentes() {
        try {
            restClient.delete()
                    .uri("/fuentes/limpiar")
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new RuntimeException("Error al limpiar fuentes: " + e.getMessage(), e);
        }
    }

    // ===== Consenso =====
    public void aplicarConsenso(String tipo, String coleccion) {
        try {
            var req = new ConsensoRequest(tipo, coleccion);
            restClient.patch()
                    .uri("/consenso")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new RuntimeException("Error al aplicar consenso: " + e.getMessage(), e);
        }
    }
}
