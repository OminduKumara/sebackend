package com.backend.mybungalow.service;

import com.backend.mybungalow.domain.Item;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class NotificationService {

    private final Set<Long> lowStockSentItems = new HashSet<>(); // track item IDs
    private final List<String> alerts = new ArrayList<>(); // store messages

    // Add alert if not already sent
    public void sendLowStockAlert(Item item) {
        if (item.getQuantityOnHand() < 5 && !lowStockSentItems.contains(item.getId())) {
            String message = "Item '" + item.getName() + "' is low stock! Current quantity: " + item.getQuantityOnHand();
            alerts.add(message);
            lowStockSentItems.add(item.getId());
        }
    }

    public List<String> getAlerts() {
        return new ArrayList<>(alerts); // return copy, never null
    }

    public void clearAlerts() {
        alerts.clear(); // clear stored messages
        lowStockSentItems.clear(); // reset tracking
    }
}
