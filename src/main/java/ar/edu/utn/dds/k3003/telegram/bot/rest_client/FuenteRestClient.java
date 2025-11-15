package ar.edu.utn.dds.k3003.telegram.bot.rest_client;

import ar.edu.utn.dds.k3003.telegram.bot.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.PdIDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class FuenteRestClient {

    private final RestClient restClient;

    public FuenteRestClient(RestClient.Builder builder) {
        String endpoint = System.getenv().getOrDefault("DDS_FUENTEy", "http://localhost:8082");
        this.restClient = builder.baseUrl(endpoint).build();
    }

//    /**
//     * Lista todos los hechos de la fuente
//     */
//    public List<HechoDTO> listarHechos() {
//        try {
//            log.info("Listando hechos desde Fuente");
//
//            return restClient.get()
//                    .uri("/api/hecho")
//                    .retrieve()
//                    .body(new org.springframework.core.ParameterizedTypeReference<List<HechoDTO>>() {});
//        } catch (Exception e) {
//            log.error("Error listando hechos: {}", e.getMessage());
//            throw new RuntimeException("Error conectando con Fuente: " + e.getMessage(), e);
//        }
//    }

    /**
     * Obtiene un hecho específico por ID
     * Endpoint: GET /api/hecho/{id}
     */
    public HechoDTO obtenerHecho(String hechoId) {
        try {
            log.info("Obteniendo hecho {} desde Fuente", hechoId);

            return restClient.get()
                    .uri("/api/hecho/{id}", hechoId)
                    .retrieve()
                    .body(HechoDTO.class);
        } catch (Exception e) {
            log.error("Error obteniendo hecho {}: {}", hechoId, e.getMessage());
            throw new RuntimeException("Error obteniendo hecho: " + e.getMessage(), e);
        }
    }

    /**
     * Crea un nuevo hecho en la fuente
     * Endpoint: POST /api/hecho
     */
    public HechoDTO crearHecho(HechoDTO hechoDTO) {
        try {
            log.info("Creando nuevo hecho en Fuente: {}", hechoDTO);

            return restClient.post()
                    .uri("/api/hecho")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(hechoDTO)
                    .retrieve()
                    .body(HechoDTO.class);
        } catch (Exception e) {
            log.error("Error creando hecho: {}", e.getMessage());
            throw new RuntimeException("Error conectando con el servicio de fuentes: " + e.getMessage(), e);
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
                    .uri("/api/hecho/PdIs")
                    .body(nuevoPdi)
                    .retrieve()
                    .body(PdIDTO.class);
        } catch (Exception e) {
            log.error("Error creando PdI para hecho {}: {}", nuevoPdi.hechoId(), e.getMessage());
            throw new RuntimeException("Error conectando con PdI: " + e.getMessage(), e);
        }
    }

}
