package ar.edu.utn.dds.k3003.telegram.bot;

import ar.edu.utn.dds.k3003.telegram.bot.command.BotCommand;
import ar.edu.utn.dds.k3003.telegram.bot.command.CommandRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;


@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final CommandRegistry commandRegistry;

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            CommandRegistry commandRegistry) {
        super(botToken);
        this.botUsername = botUsername;
        this.commandRegistry = commandRegistry;
        log.info("✅ Telegram Bot inicializado exitosamente");
        log.info("📱 Bot Username: @{}", botUsername);
        log.info("🔧 Comandos registrados: {}", commandRegistry.getAllCommands().size());
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Validar que el update tenga un mensaje con texto
        if (!isValidUpdate(update)) {
            log.debug("Update recibido sin mensaje de texto, ignorando");
            return;
        }

        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String username = getUsernameFromUpdate(update);

        log.info("📨 Mensaje recibido de @{} (chatId: {}): {}", username, chatId, messageText);

        try {
            String response = processCommand(update, messageText);
            sendMessage(chatId, response);
            log.info("✅ Respuesta enviada exitosamente a @{}", username);
        } catch (Exception e) {
            log.error("❌ Error procesando mensaje de @{}: {}", username, e.getMessage(), e);
            sendErrorMessage(chatId, e);
        }
    }


    private boolean isValidUpdate(Update update) {
        return update != null &&
                update.hasMessage() &&
                update.getMessage().hasText() &&
                update.getMessage().getText() != null &&
                !update.getMessage().getText().trim().isEmpty();
    }


    private String getUsernameFromUpdate(Update update) {
        if (update.getMessage().getFrom() != null &&
                update.getMessage().getFrom().getUserName() != null) {
            return update.getMessage().getFrom().getUserName();
        }

        // Fallback a firstName o ID si no tiene username
        if (update.getMessage().getFrom().getFirstName() != null) {
            return update.getMessage().getFrom().getFirstName();
        }

        return "Usuario" + update.getMessage().getFrom().getId();
    }


    private String processCommand(Update update, String messageText) {
        // Extraer el nombre del comando
        String commandName = extractCommandName(messageText);

        if (commandName.isEmpty()) {
            log.warn("Mensaje sin comando válido recibido: {}", messageText);
            return "❓ No entiendo ese mensaje.\n\n" +
                    "Los comandos deben empezar con /\n" +
                    "Usa /help para ver los comandos disponibles.";
        }

        // Buscar el comando en el registry
        Optional<BotCommand> commandOpt = commandRegistry.getCommand(commandName);

        if (commandOpt.isEmpty()) {
            log.warn("Comando no reconocido: {}", commandName);
            return formatUnknownCommandMessage(commandName);
        }

        // Ejecutar el comando
        BotCommand command = commandOpt.get();
        log.debug("Ejecutando comando: {}", command.getCommandName());

        try {
            return command.execute(update);
        } catch (Exception e) {
            log.error("Error ejecutando comando {}: {}", commandName, e.getMessage(), e);
            return "❌ Error ejecutando el comando: " + e.getMessage();
        }
    }


    private String extractCommandName(String messageText) {
        if (messageText == null || !messageText.startsWith("/")) {
            return "";
        }

        // Remover el '/' y obtener solo el nombre del comando (antes del primer espacio)
        String[] parts = messageText.substring(1).split("\\s+");
        return parts[0].toLowerCase();
    }


    private String formatUnknownCommandMessage(String commandName) {
        StringBuilder message = new StringBuilder();
        message.append("❌ Comando no reconocido: /").append(commandName).append("\n\n");
        message.append("Usa /help para ver todos los comandos disponibles.\n\n");

        // Sugerir comandos similares si es posible
        String suggestion = findSimilarCommand(commandName);
        if (suggestion != null) {
            message.append("💡 ¿Quisiste decir /").append(suggestion).append("?");
        }

        return message.toString();
    }


    private String findSimilarCommand(String commandName) {
        return commandRegistry.getAllCommands().stream()
                .map(BotCommand::getCommandName)
                .filter(name -> name.contains(commandName) || commandName.contains(name))
                .findFirst()
                .orElse(null);
    }


    public void sendMessage(Long chatId, String text) {
        if (chatId == null || text == null || text.trim().isEmpty()) {
            log.warn("Intento de enviar mensaje inválido (chatId: {}, text vacio: {})",
                    chatId, text == null || text.trim().isEmpty());
            return;
        }

        // Telegram tiene un límite de 4096 caracteres por mensaje
        if (text.length() > 4096) {
            log.warn("Mensaje demasiado largo ({}), dividiendo...", text.length());
            sendLongMessage(chatId, text);
            return;
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .disableWebPagePreview(true) // Evitar preview de URLs
                .build();

        try {
            execute(message);
            log.debug("✅ Mensaje enviado a chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("❌ Error enviando mensaje a chatId {}: {}", chatId, e.getMessage());
            // Intentar enviar sin Markdown si falla
            retryWithoutMarkdown(chatId, text);
        }
    }

    /**
     * Envía mensajes largos dividiéndolos
     */
    private void sendLongMessage(Long chatId, String text) {
        int chunkSize = 4000;
        for (int i = 0; i < text.length(); i += chunkSize) {
            int end = Math.min(text.length(), i + chunkSize);
            String chunk = text.substring(i, end);
            sendMessage(chatId, chunk);

            // Pequeña pausa entre mensajes para evitar rate limiting
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Reintenta enviar mensaje sin formato Markdown
     */
    private void retryWithoutMarkdown(Long chatId, String text) {
        SendMessage plainMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();

        try {
            execute(plainMessage);
            log.debug("✅ Mensaje enviado sin Markdown a chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("❌ Error crítico enviando mensaje a chatId {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Envía un mensaje de error al usuario
     */
    private void sendErrorMessage(Long chatId, Exception e) {
        String errorMessage = "❌ *Error interno del bot*\n\n" +
                "Ocurrió un error al procesar tu solicitud.\n" +
                "Por favor, intenta nuevamente más tarde.\n\n" +
                "Error: " + e.getMessage();

        sendMessage(chatId, errorMessage);
    }

    /**
     * Envía una imagen al usuario
     */
    public void sendPhoto(Long chatId, String photoUrl, String caption) {
        if (chatId == null || photoUrl == null || photoUrl.trim().isEmpty()) {
            log.warn("Intento de enviar foto inválida");
            return;
        }

        SendPhoto photo = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(new InputFile(photoUrl))
                .caption(caption != null ? caption : "")
                .parseMode("Markdown")
                .build();

        try {
            execute(photo);
            log.debug("✅ Imagen enviada a chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("❌ Error enviando imagen a chatId {}: {}", chatId, e.getMessage());
            // Fallback: enviar solo el texto con la URL
            String fallbackMessage = (caption != null ? caption + "\n\n" : "") +
                    "🖼️ Imagen: " + photoUrl;
            sendMessage(chatId, fallbackMessage);
        }
    }

    /**
     * Envía un mensaje con formato personalizado
     */
    public void sendFormattedMessage(Long chatId, String text, boolean enableMarkdown) {
        if (chatId == null || text == null || text.trim().isEmpty()) {
            return;
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();

        if (enableMarkdown) {
            message.setParseMode("Markdown");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("❌ Error enviando mensaje formateado: {}", e.getMessage());
            // Fallback sin formato
            if (enableMarkdown) {
                sendFormattedMessage(chatId, text, false);
            }
        }
    }

    /**
     * Obtiene estadísticas del bot
     */
    public String getBotStats() {
        return String.format(
                "📊 Estadísticas del Bot\n" +
                        "Bot: @%s\n" +
                        "Comandos disponibles: %d",
                botUsername,
                commandRegistry.getAllCommands().size()
        );
    }
}