package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.dtos.FuenteDTO;
import ar.edu.utn.dds.k3003.telegram.bot.rest_client.AgregadorRestClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ListarFuentesCommand extends AbstractBotCommand {
    private final AgregadorRestClient agregador;

    public ListarFuentesCommand(AgregadorRestClient agregador) {
        this.agregador = agregador;
    }

    @Override
    protected String executeCommand(Update update) {
        try {
            List<FuenteDTO> fuentes = agregador.listarFuentes();

            if (fuentes == null || fuentes.isEmpty()) {
                return formatInfo("No hay fuentes cargadas.");
            }

            // De-dup visual: preferimos ID; si no hay, usamos nombre|endpoint
            Map<String, FuenteDTO> unique = new LinkedHashMap<>();
            for (FuenteDTO f : fuentes) {
                String key = (f.id() != null && !f.id().isBlank())
                        ? "id:" + f.id()
                        : "ne:" + (safe(f.nombre()) + "|" + safe(f.endpoint()));
                unique.putIfAbsent(key, f);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("📚 *Fuentes disponibles:*\n\n");

            int i = 0;
            for (FuenteDTO f : unique.values()) {
                String id = safe(f.id());
                String nombre = safe(f.nombre());
                String endpoint = safe(f.endpoint());

                // Tarjeta “tipo JSON bonito” (sin code blocks para evitar escapes en Markdown clásico)
                sb.append("🧩 *Fuente ").append(++i).append("*\n")
                  .append("  • *id:* ").append(id).append("\n")
                  .append("  • *nombre:* ").append(nombre).append("\n")
                  .append("  • *endpoint:* ").append(endpoint).append("\n")
                  .append("────────────────────\n");
            }

            return sb.toString();
        } catch (Exception e) {
            return formatError("No se pudieron listar las fuentes: " + e.getMessage());
        }
    }

    private String safe(String v) {
        return (v == null || v.isBlank()) ? "(sin dato)" : v;
    }

    @Override public String getCommandName() { return "listarfuentes"; }
    @Override public String getDescription() { return "Lista las fuentes disponibles"; }
}
