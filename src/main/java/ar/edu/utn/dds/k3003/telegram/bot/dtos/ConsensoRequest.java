package ar.edu.utn.dds.k3003.telegram.bot.dtos;

public record ConsensoRequest(
        String tipo,        // "TODOS", "AL_MENOS_2", "ESTRICTO"
        String coleccion
) {}

