package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.local.LocalCollectionsStore;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ListarColeccionesLocalesCommand extends AbstractBotCommand {
    private final LocalCollectionsStore store;

    public ListarColeccionesLocalesCommand(LocalCollectionsStore store) {
        this.store = store;
    }

    @Override
    protected String executeCommand(Update update) {
        List<String> col = store.listar();

        StringBuilder sb = new StringBuilder("📚 *Colecciones locales usadas:*\n\n");
        if (col.isEmpty()) {
            sb.append("(no hay colecciones aún)\n");
        } else {
            col.forEach(c -> sb.append("• ").append(c).append("\n"));
        }
        sb.append("\n➕ (otro)");

        sb.append("\n\n💡 Podés agregar colecciones simplemente usando /consenso <TIPO> <coleccion> o /listarhechos \"coleccion\"");

        return sb.toString();
    }

    @Override public String getCommandName() { return "colecciones"; }
    @Override public String getDescription() { return "Muestra colecciones guardadas localmente"; }
}

