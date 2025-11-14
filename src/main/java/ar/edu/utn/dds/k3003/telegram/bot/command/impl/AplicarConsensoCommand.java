package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.AgregadorRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AplicarConsensoCommand extends AbstractBotCommand {

    public static final String AL_MENOS_2 = "AL_MENOS_2";
    public static final String TODOS = "TODOS";
    public static final String ESTRICTO = "ESTRICTO";
    private final AgregadorRestClient agregador;

    @Override
    protected String executeCommand(Update update) {
        List<String> p = extractParameters(update);

        if (p.size() < 2) {
            return formatError("""
                Faltan parámetros.
                Uso: /consenso <TIPO> <coleccion>
                Ejemplo: /consenso ESTRICTO coleccion1
                Tipos disponibles: [TODOS-AL_MENOS_2-ESTRICTO]
                """);
        }

        String rawTipo = p.get(0).trim().toUpperCase();
        String tipo = normalizeTipo(rawTipo);
        String coleccion = p.get(1).trim();

        if (!Set.of(TODOS, AL_MENOS_2, ESTRICTO).contains(tipo)) {
            return formatError("Tipo inválido. Debe ser: TODOS, AL_MENOS_2 o ESTRICTO.");
        }

        try {
            agregador.aplicarConsenso(tipo, coleccion);
            return formatSuccess("Consenso aplicado") +
                    "\n🧩 Tipo: " + tipo +
                    "\n📚 Colección: " + coleccion;
        } catch (Exception e) {
            return formatError("Error al aplicar consenso: " + e.getMessage());
        }
    }

    private String normalizeTipo(String t) {
        if ("ALMENOS2".equals(t) || AL_MENOS_2.equals(t)) return AL_MENOS_2;
        return t;
    }

    @Override public String getCommandName() { return "consenso"; }
    @Override public String getDescription() { return "Aplica consenso en una colección"; }

    @Override public boolean requiresParameters() { return false; }

    @Override public String getUsageExample() { return "/consenso ESTRICTO coleccion1"; }
}
