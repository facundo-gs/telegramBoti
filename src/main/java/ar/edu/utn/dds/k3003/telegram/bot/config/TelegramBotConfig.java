package ar.edu.utn.dds.k3003.telegram.bot.config;

import ar.edu.utn.dds.k3003.telegram.bot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Configuración del bot de Telegram
 * Registra el bot con la API de Telegram al iniciar la aplicación
 */
@Configuration
@Slf4j
public class TelegramBotConfig {

    /**
     * Inicializa y registra el bot con Telegram
     * @param telegramBot El bot a registrar
     * @return TelegramBotsApi instance
     * @throws RuntimeException si falla el registro
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) {
        try {
            log.info("🚀 Iniciando registro del bot con Telegram API...");

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);

            log.info("✅ Bot registrado exitosamente: @{}", telegramBot.getBotUsername());
            log.info("📡 Bot en modo Long Polling - esperando mensajes...");

            return botsApi;

        } catch (TelegramApiException e) {
            log.error("❌ Error crítico registrando el bot de Telegram", e);
            log.error("💡 Verifica que:");
            log.error("   - La variable TELEGRAM_BOT_TOKEN esté correctamente configurada");
            log.error("   - El token sea válido (obtenido de @BotFather)");
            log.error("   - Tengas conexión a internet");

            throw new RuntimeException("No se pudo inicializar el bot de Telegram: " + e.getMessage(), e);
        }
    }
}