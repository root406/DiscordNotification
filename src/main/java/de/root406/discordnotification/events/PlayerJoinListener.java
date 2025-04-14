package de.root406.discordnotification.events;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.modules.bridge.event.BridgeProxyPlayerLoginEvent;
import de.root406.discordnotification.utils.DiscordWebhook;
import eu.cloudnetservice.modules.bridge.player.CloudPlayer;

public class PlayerJoinListener {

    private final String webhookUrl;

    public PlayerJoinListener(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @EventListener
    public void handlePlayerJoin(BridgeProxyPlayerLoginEvent event) {
        // Debug-Ausgabe
        System.out.println("PlayerJoinListener triggered!");

        // Holen Sie sich den CloudPlayer aus dem Event
        CloudPlayer player = event.cloudPlayer();

        // Debug-Ausgabe
        System.out.println("Player: " + player.name());

        // Nachricht an Discord senden
        sendDiscordMessage("Player Joined", "Player " + player.name() + " has joined the game!", "0x1E90FF");
    }

    private void sendDiscordMessage(String title, String description, String hexColor) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            int color = Integer.parseInt(hexColor.replace("0x", ""), 16);  // Umwandlung von Hex zu int
            DiscordWebhook.WebhookMessage webhookMessage = new DiscordWebhook.WebhookMessage()
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle(title)
                            .setDescription(description)
                            .setColor(color)
                    );
            DiscordWebhook.sendWebhook(webhookUrl, webhookMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}