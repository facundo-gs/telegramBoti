package ar.edu.utn.dds.k3003.telegram.bot.dtos;

public enum EstadoSolicitudBorradoEnum {
    CREADA,
    VALIDADA,
    EN_DISCUCION,
    ACEPTADA,
    RECHAZADA;

    public static String[] valuesAsString() {
        return java.util.Arrays.stream(values())
                .map(Enum::name)
                .toArray(String[]::new);
    }
}
