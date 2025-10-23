package ar.edu.utn.dds.k3003.telegram.bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Command Pattern: Interface para todos los comandos del bot
 * Esto permite agregar nuevos comandos fácilmente sin modificar el bot principal
 */
public interface BotCommand {

    /**
     * Ejecuta el comando
     * @param update El update de Telegram con el mensaje
     * @return Mensaje de respuesta para el usuario
     */
    String execute(Update update) throws TelegramApiException;

    /**
     * Obtiene el nombre del comando (ej: "listar", "ver", "agregar")
     * @return Nombre del comando
     */
    String getCommandName();

    /**
     * Obtiene la descripción del comando para el /help
     * @return Descripción del comando
     */
    String getDescription();

    /**
     * Verifica si este comando requiere parámetros adicionales
     * @return true si requiere parámetros
     */
    default boolean requiresParameters() {
        return false;
    }

    /**
     * Obtiene un ejemplo de uso del comando
     * @return Ejemplo de uso
     */
    default String getUsageExample() {
        return "/" + getCommandName();
    }
}
