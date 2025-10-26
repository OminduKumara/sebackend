package com.backend.mybungalow.controller;

import com.backend.mybungalow.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // GET endpoint to fetch alerts
    @GetMapping
    public List<String> getAlerts() {
        return notificationService.getAlerts();
    }

    // Optional: clear all alerts
    @DeleteMapping
    public void clearAlerts() {
        notificationService.clearAlerts();
    }
}
