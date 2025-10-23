package ar.edu.utn.dds.k3003.telegram.bot.controller;

import ar.edu.utn.dds.k3003.telegram.bot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller para mantener el servicio activo en plataformas cloud
 * Render y Heroku requieren un endpoint HTTP activo para no apagar el servicio
 */
@RestController
public class HealthController {

    @Autowired(required = false)
    private TelegramBot telegramBot;

    /**
     * Health check básico
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Sistema de Gestión - Telegram Bot");

        if (telegramBot != null) {
            health.put("telegram_bot", "ACTIVE");
            health.put("bot_username", telegramBot.getBotUsername());
            health.put("bot_stats", telegramBot.getBotStats());
        } else {
            health.put("telegram_bot", "NOT_CONFIGURED");
        }

        return ResponseEntity.ok(health);
    }

    /**
     * Ping endpoint simple
     * GET /ping
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong - " + LocalDateTime.now();
    }

    /**
     * Info del servicio
     * GET /info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("application", "Sistema de Gestión con Bot de Telegram");
        info.put("version", "1.0.0");
        info.put("description", "Sistema integrado con Agregador, Fuente, Solicitudes y PDI");

        if (telegramBot != null) {
            info.put("bot", "@" + telegramBot.getBotUsername());
        }

        return ResponseEntity.ok(info);
    }
}