package de.root406.discordnotification;

import de.root406.discordnotification.utils.CustomMessage;
import de.root406.discordnotification.utils.DiscordWebhook;
import de.root406.discordnotification.utils.Messages;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.module.ModulePostLoadEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostReloadEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostStartEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostStopEvent;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.service.ServiceLifeCycle;
import eu.cloudnetservice.node.event.service.CloudServicePreLifecycleEvent;

import java.nio.file.Path;
import java.util.logging.Logger;

public class DiscordNotification extends DriverModule {

    private static final Logger LOGGER = Logger.getLogger(DiscordNotification.class.getName());

    private String webhookUrl;
    private boolean notifyModuleLoaded;
    private boolean notifyModuleStarted;
    private boolean notifyModuleStopped;
    private boolean notifyModuleReload;
    private boolean notifyServiceStarted;
    private boolean notifyServiceStopped;
    private Messages customMessages;

    @ModuleTask(lifecycle = ModuleLifeCycle.LOADED)
    public void onLoad() {
        loadConfig();
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void onStart() {
        if (notifyModuleStarted) {
            sendDiscordMessage(
                    "Modul gestartet",
                    customMessages.moduleStarted.message.replace("%module_name%", "CloudNet-Discord-Modul"),
                    customMessages.moduleStarted.color
            );
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void onStop() {
        if (notifyModuleStopped) {
            sendDiscordMessage(
                    "Modul gestoppt",
                    customMessages.moduleStopped.message.replace("%module_name%", "CloudNet-Discord-Modul"),
                    customMessages.moduleStopped.color
            );
        }
    }

    @EventListener
    public void onModuleLoad(ModulePostLoadEvent event) {
        if (notifyModuleLoaded) {
            sendDiscordMessage(
                    "Modul geladen",
                    customMessages.moduleLoaded.message.replace("%module_name%", event.module().moduleConfiguration().name()),
                    customMessages.moduleLoaded.color
            );
        }
    }

    @EventListener
    public void onModuleReload(ModulePostReloadEvent event) {
        if (notifyModuleReload) {
            sendDiscordMessage(
                    "Modul Neugeladen",
                    customMessages.moduleReload.message.replace("%module_name%", event.module().moduleConfiguration().name()),
                    customMessages.moduleReload.color
            );
        }
    }

    @EventListener
    public void onModuleStart(ModulePostStartEvent event) {
        if (notifyModuleStarted) {
            sendDiscordMessage(
                    "Modul gestartet",
                    customMessages.moduleStarted.message.replace("%module_name%", event.module().moduleConfiguration().name()),
                    customMessages.moduleStarted.color
            );
        }
    }

    @EventListener
    public void onModuleStop(ModulePostStopEvent event) {
        if (notifyModuleStopped) {
            sendDiscordMessage(
                    "Modul gestoppt",
                    customMessages.moduleStopped.message.replace("%module_name%", event.module().moduleConfiguration().name()),
                    customMessages.moduleStopped.color
            );
        }
    }

    @EventListener
    public void onCloudServiceLifecycle(CloudServicePreLifecycleEvent event) {
        String serviceName = event.serviceInfo().name();
        ServiceLifeCycle targetLifeCycle = event.targetLifecycle();

        if (targetLifeCycle == ServiceLifeCycle.RUNNING && notifyServiceStarted) {
            sendDiscordMessage(
                    "Server gestartet",
                    customMessages.serviceStarted.message.replace("%service_name%", serviceName),
                    customMessages.serviceStarted.color
            );
        } else if (targetLifeCycle == ServiceLifeCycle.STOPPED && notifyServiceStopped) {
            sendDiscordMessage(
                    "Server gestoppt",
                    customMessages.serviceStopped.message.replace("%service_name%", serviceName),
                    customMessages.serviceStopped.color
            );
        }
    }

    private void loadConfig() {
        Path configPath = this.configPath();
        Document document = DocumentFactory.json().parse(configPath);

        this.webhookUrl = document.getString("webhookUrl", "");
        this.notifyModuleLoaded = document.getBoolean("notifyModuleLoaded", true);
        this.notifyModuleStarted = document.getBoolean("notifyModuleStarted", true);
        this.notifyModuleStopped = document.getBoolean("notifyModuleStopped", true);
        this.notifyModuleReload = document.getBoolean("notifyModuleReload", true); // Korrektur hier
        this.notifyServiceStarted = document.getBoolean("notifyServiceStarted", true);
        this.notifyServiceStopped = document.getBoolean("notifyServiceStopped", true);

        this.customMessages = new Messages(
                new CustomMessage(
                        document.getString("customMessages.moduleLoaded.message", "📦 Das Modul %module_name% wurde erfolgreich geladen."),
                        document.getString("customMessages.moduleLoaded.color", "0x00FF00")
                ),
                new CustomMessage(
                        document.getString("customMessages.moduleStarted.message", "🚀 Das Modul %module_name% wurde erfolgreich gestartet."),
                        document.getString("customMessages.moduleStarted.color", "0x1E90FF")
                ),
                new CustomMessage(
                        document.getString("customMessages.moduleStopped.message", "🛑 Das Modul %module_name% wurde gestoppt."),
                        document.getString("customMessages.moduleStopped.color", "0xFF0000")
                ),
                new CustomMessage(
                        document.getString("customMessages.moduleReload.message", "🔄 Das Modul %module_name% wurde neu geladen."),
                        document.getString("customMessages.moduleReload.color", "0xFFA500")
                ),
                new CustomMessage(
                        document.getString("customMessages.serviceStarted.message", "🚀 Der Server %service_name% wurde gestartet."),
                        document.getString("customMessages.serviceStarted.color", "0x32CD32")
                ),
                new CustomMessage(
                        document.getString("customMessages.serviceStopped.message", "🛑 Der Server %service_name% wurde gestoppt."),
                        document.getString("customMessages.serviceStopped.color", "0xFF4500")
                )
        );

        if (!document.contains("webhookUrl")) {
            LOGGER.warning("Webhook-URL fehlt in der Konfiguration. Standardwerte werden verwendet.");
            saveConfig();
        }
    }

    private void saveConfig() {
        DocumentFactory.json()
                .newDocument()
                .append("webhookUrl", webhookUrl)
                .append("notifyModuleLoaded", notifyModuleLoaded)
                .append("notifyModuleStarted", notifyModuleStarted)
                .append("notifyModuleStopped", notifyModuleStopped)
                .append("notifyModuleReload", notifyModuleReload)
                .append("notifyServiceStarted", notifyServiceStarted)
                .append("notifyServiceStopped", notifyServiceStopped)
                .append("customMessages.moduleLoaded.message", customMessages.moduleLoaded.message)
                .append("customMessages.moduleLoaded.color", customMessages.moduleLoaded.color)
                .append("customMessages.moduleStarted.message", customMessages.moduleStarted.message)
                .append("customMessages.moduleStarted.color", customMessages.moduleStarted.color)
                .append("customMessages.moduleReload.message", customMessages.moduleReload.message)
                .append("customMessages.moduleReload.color", customMessages.moduleReload.color)
                .append("customMessages.moduleStopped.message", customMessages.moduleStopped.message)
                .append("customMessages.moduleStopped.color", customMessages.moduleStopped.color)
                .append("customMessages.serviceStarted.message", customMessages.serviceStarted.message)
                .append("customMessages.serviceStarted.color", customMessages.serviceStarted.color)
                .append("customMessages.serviceStopped.message", customMessages.serviceStopped.message)
                .append("customMessages.serviceStopped.color", customMessages.serviceStopped.color)
                .writeTo(this.configPath());
    }

    private void sendDiscordMessage(String title, String description, String hexColor) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            LOGGER.warning("Webhook-URL ist leer. Nachricht wird nicht gesendet.");
            return;
        }

        try {
            int color = Integer.parseInt(hexColor.replace("0x", ""), 16); // Hex zu int konvertieren
            DiscordWebhook.WebhookMessage webhookMessage = new DiscordWebhook.WebhookMessage()
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle(title)
                            .setDescription(description)
                            .setColor(color)
                    );
            DiscordWebhook.sendWebhook(webhookUrl, webhookMessage);
        } catch (NumberFormatException e) {
            LOGGER.severe("Ungültige Hex-Farbe: " + hexColor);
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Senden der Discord-Nachricht: " + e.getMessage());
            e.printStackTrace();
        }
    }
}