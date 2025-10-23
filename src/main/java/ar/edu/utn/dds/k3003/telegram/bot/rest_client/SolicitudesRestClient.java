package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.SolicitudModificacionRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class SolicitudesRestClient {

    private final RestClient restClient;

    public SolicitudesRestClient() {
        var env = System.getenv();
        String endpoint = env.getOrDefault("DDS_SOLICITUDES", "http://localhost:8080");

        this.restClient = RestClient.builder()
                .baseUrl(endpoint)
                .build();
    }

    /**
     * Crea una nueva solicitud
     * Endpoint: POST /api/solicitudes
     */
    public SolicitudDTO agregar(SolicitudDTO solicitud) {
        try {
            log.info("Creando nueva solicitud");

            return restClient.post()
                    .uri("/api/solicitudes")
                    .body(solicitud)
                    .retrieve()
                    .body(SolicitudDTO.class);
        } catch (Exception e) {
            log.error("Error creando solicitud: {}", e.getMessage());
            throw new RuntimeException("Error conectando con el servicio de solicitudes: " + e.getMessage(), e);
        }
    }

    /**
     * Modifica el estado y/o descripción de una solicitud
     * Endpoint: PATCH /api/solicitudes
     */
    public SolicitudDTO modificarEstado(SolicitudModificacionRequestDTO body) {
        try {
            log.info("Modificando estado de la solicitud {}", body.getId());

            return restClient.patch()
                    .uri("/api/solicitudes")
                    .body(body)
                    .retrieve()
                    .body(SolicitudDTO.class);

        } catch (Exception e) {
            log.error("Error modificando solicitud: {}", e.getMessage());
            throw new RuntimeException("Error conectando con el servicio de solicitudes: " + e.getMessage(), e);
        }
    }


}