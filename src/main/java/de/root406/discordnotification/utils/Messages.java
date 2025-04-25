package de.root406.discordnotification.utils;

import java.util.HashMap;
import java.util.Map;

public class Messages {

    private final Map<String, CustomMessage> messages;

    public Messages(CustomMessage moduleLoaded, CustomMessage moduleStarted, CustomMessage moduleStopped, CustomMessage moduleReload, CustomMessage serviceStarted, CustomMessage serviceStopped) {
        this.messages = new HashMap<>();
        this.messages.put("moduleLoaded", moduleLoaded);
        this.messages.put("moduleStarted", moduleStarted);
        this.messages.put("moduleStopped", moduleStopped);
        this.messages.put("moduleReload", moduleReload);
        this.messages.put("serviceStarted", serviceStarted);
        this.messages.put("serviceStopped", serviceStopped);
    }

    // Getter für das Abrufen von Nachrichten basierend auf dem Schlüssel
    public CustomMessage getMessage(String key) {
        return this.messages.getOrDefault(key, null);
    }

    // Methode zum dynamischen Hinzufügen oder Aktualisieren einer Nachricht
    public void addOrUpdateMessage(String key, CustomMessage message) {
        this.messages.put(key, message);
    }

    // Überprüfen, ob eine Nachricht vorhanden ist
    public boolean containsMessage(String key) {
        return this.messages.containsKey(key);
    }
}