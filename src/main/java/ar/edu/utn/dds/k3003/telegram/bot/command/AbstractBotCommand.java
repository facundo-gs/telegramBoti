package ar.edu.utn.dds.k3003.telegram.bot.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Template Method
@Slf4j
public abstract class AbstractBotCommand implements BotCommand {

    @Override
    public String execute(Update update) {
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

    protected abstract String executeCommand(Update update) throws TelegramApiException;

    protected List<String> extractParameters(Update update) {
        String text = update.getMessage().getText();

        // Remove the command (the first token)
        String withoutCommand = text.replaceFirst("^/\\S+\\s*", "");

        List<String> params = new ArrayList<>();

        // Regex: capture sequences inside quotes or non-space sequences
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(withoutCommand);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                params.add(matcher.group(1)); // quoted value
            } else {
                params.add(matcher.group(2)); // unquoted value
            }
        }

        return params;
    }


    protected String getUsername(Update update) {
        return update.getMessage().getFrom().getUserName();
    }

    protected String formatError(String message) {
        return "❌ " + message;
    }

    protected String formatSuccess(String message) {
        return "✅ " + message;
    }

    protected String formatInfo(String message) {
        return "ℹ️ " + message;
    }
}