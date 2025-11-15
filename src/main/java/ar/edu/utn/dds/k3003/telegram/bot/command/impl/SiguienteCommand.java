package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.BusquedaResponseDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.BusquedaRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Comando: /siguiente
 * Muestra la siguiente página de resultados de búsqueda.
 */
@Slf4j
@Component
public class SiguienteCommand extends AbstractBotCommand {

    private final BusquedaRestClient busquedaRestClient;
    private final BuscarCommand buscarCommand;

    public SiguienteCommand(BusquedaRestClient busquedaRestClient, BuscarCommand buscarCommand) {
        this.busquedaRestClient = busquedaRestClient;
        this.buscarCommand = buscarCommand;
    }

    @Override
    protected String executeCommand(Update update) {
        Long chatId = update.getMessage().getChatId();

        BuscarCommand.BusquedaCache cache = buscarCommand.getCacheBusquedas().get(chatId);

        if (cache == null || !cache.esValido()) {
            return formatError(
                    "No hay búsqueda activa.\n" +
                            "Usa /buscar <consulta> para realizar una nueva búsqueda."
            );
        }

        BusquedaResponseDTO ultimaRespuesta = cache.ultimaRespuesta;

        if (!ultimaRespuesta.tieneSiguiente()) {
            return formatInfo("Ya estás en la última página de resultados.");
        }

        try {
            int siguientePagina = ultimaRespuesta.paginaActual() + 1;

            BusquedaResponseDTO response = busquedaRestClient.buscar(
                    cache.consulta,
                    cache.tags,
                    siguientePagina,
                    10
            );

            cache.ultimaRespuesta = response;

            return buscarCommand.formatearResultados(response, cache.consulta, cache.tags);

        } catch (Exception e) {
            log.error("Error obteniendo siguiente página: {}", e.getMessage(), e);
            return formatError("Error al obtener la siguiente página: " + e.getMessage());
        }
    }

    @Override
    public String getCommandName() {
        return "siguiente";
    }

    @Override
    public String getDescription() {
        return "Muestra la siguiente página de resultados de búsqueda";
    }
}