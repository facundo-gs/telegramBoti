package ar.edu.utn.dds.k3003.telegram.bot.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

/**
 * Template Method Pattern: Clase base para comandos del bot
 * Define el flujo común de ejecución y validación
 */
@Slf4j
public abstract class AbstractBotCommand implements BotCommand {

    @Override
    public String execute(Update update) throws TelegramApiException {
        try {
            log.info("Ejecutando comando: {} por usuario: {}",
                    getCommandName(),
                    update.getMessage().getFrom().getUserName());

            // Validar parámetros si son requeridos
            if (requiresParameters()) {
                List<String> params = extractParameters(update);
                if (params.isEmpty()) {
                    return "❌ Este comando requiere parámetros.\n" +
                            "Ejemplo: " + getUsageExample();
                }
            }

            // Ejecutar la lógica específica del comando
            String result = executeCommand(update);

            log.info("Comando {} ejecutado exitosamente", getCommandName());
            return result;

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en comando {}: {}", getCommandName(), e.getMessage());
            return "❌ Error: " + e.getMessage();
        } catch (Exception e) {
            log.error("Error ejecutando comando {}: {}", getCommandName(), e.getMessage(), e);
            return "❌ Error inesperado: " + e.getMessage();
        }
    }

    /**
     * Método abstracto que cada comando debe implementar con su lógica específica
     */
    protected abstract String executeCommand(Update update) throws TelegramApiException;

    /**
     * Extrae los parámetros del mensaje
     */
    protected List<String> extractParameters(Update update) {
        String text = update.getMessage().getText();
        String[] parts = text.split("\\s+");

        // Remover el comando (primera parte) y retornar el resto
        if (parts.length > 1) {
            return Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
        }
        return List.of();
    }

    /**
     * Obtiene el chat ID del usuario
     */
    protected Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    /**
     * Obtiene el username del usuario
     */
    protected String getUsername(Update update) {
        return update.getMessage().getFrom().getUserName();
    }

    /**
     * Formatea una respuesta de error
     */
    protected String formatError(String message) {
        return "❌ " + message;
    }

    /**
     * Formatea una respuesta de éxito
     */
    protected String formatSuccess(String message) {
        return "✅ " + message;
    }

    /**
     * Formatea información
     */
    protected String formatInfo(String message) {
        return "ℹ️ " + message;
    }
}