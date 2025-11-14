package ar.edu.utn.dds.k3003.telegram.bot.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String MESSAGE = "message";

    @Override
    public String execute(Update update) {
        try {
            log.info("Ejecutando comando: {} por usuario: {}",
                    getCommandName(),
                    update.getMessage().getFrom().getUserName());

            if (requiresParameters()) {
                List<String> params = extractParameters(update);
                if (params.isEmpty()) {
                    return "❌ Este comando requiere parámetros.\n" +
                            "Ejemplo: " + getUsageExample();
                }
            }

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

        String withoutCommand = text.replaceFirst("^/\\S+\\s*", "");

        List<String> params = new ArrayList<>();

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

    protected String extractMessageFromException(Exception e) {
        // Try to parse the whole exception message as JSON first
        String msg = e.getMessage();
        if (msg == null) {
            return "error desconocido";
        }

        // attempt to parse direct JSON or JSON substring inside the message
        String candidate = msg;
        int firstBrace = msg.indexOf('{');
        int lastBrace = msg.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            candidate = msg.substring(firstBrace, lastBrace + 1);
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(candidate);
            if (root.has(MESSAGE) && root.get(MESSAGE).isTextual()) {
                return root.get(MESSAGE).asText();
            }
        } catch (JsonProcessingException ignored) {
            // fall through to return original message
        }

        // fallback: return the original message (trimmed)
        return msg.trim();
    }
}