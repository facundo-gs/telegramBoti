package ar.edu.utn.dds.k3003.telegram.bot.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SolicitudModificacionRequestDTO {
    private String id;
    private EstadoSolicitudBorradoEnum estado;
    private String descripcion;
}
