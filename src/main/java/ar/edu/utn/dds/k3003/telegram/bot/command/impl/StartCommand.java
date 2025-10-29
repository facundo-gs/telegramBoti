package ar.edu.utn.dds.k3003.telegram.bot.command.impl;

import ar.edu.utn.dds.k3003.telegram.bot.command.AbstractBotCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class StartCommand extends AbstractBotCommand {

    @Override
    protected String executeCommand(Update update) {
        String username = getUsername(update);

        return """
                👋 ¡Hola %s! Bienvenido al Bot de Diseño de sistemas
                
                🚀 *Funcionalidades principales:*
                • Listar hechos de una colección
                • Ver detalles de un hecho con sus PDIs
                • Agregar nuevos hechos
                • Agregar PDIs a un hecho
                • Crear solicitudes de borrado
                • Cambiar el estado de solicitudes de borrado
                
                📝 Usa /help para ver todos los comandos disponibles.
                """.formatted(username != null ? username : "Usuario");
    }

    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Inicia el bot y muestra mensaje de bienvenida";
    }
}