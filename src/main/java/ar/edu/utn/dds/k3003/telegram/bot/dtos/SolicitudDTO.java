package ar.edu.utn.dds.k3003.telegram.bot.dtos;

public record SolicitudDTO(String id, String descripcion, EstadoSolicitudBorradoEnum estado,
                           String hechoId) {

}