package de.root406.discordnotification;

import de.root406.discordnotification.utils.CustomMessage;
import de.root406.discordnotification.utils.DiscordWebhook;
import de.root406.discordnotification.utils.Messages;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.module.ModulePostLoadEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostStartEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostStopEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostReloadEvent;
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
        handleNotification(
                notifyModuleStarted,
                "Modul gestartet",
                customMessages.getMessage("moduleStarted").message,
                customMessages.getMessage("moduleStarted").color,
                "%module_name%",
                "CloudNet-Discord-Modul"
        );
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void onStop() {
        handleNotification(
                notifyModuleStopped,
                "Modul gestoppt",
                customMessages.getMessage("moduleStopped").message,
                customMessages.getMessage("moduleStopped").color,
                "%module_name%",
                "CloudNet-Discord-Modul"
        );
    }

    // Event für das Laden eines Moduls
    @EventListener
    public void onModuleLoaded(ModulePostLoadEvent event) {
        String moduleName = event.module().moduleConfiguration().name();
        handleNotification(
                notifyModuleLoaded,
                "Modul geladen",
                customMessages.getMessage("moduleLoaded").message,
                customMessages.getMessage("moduleLoaded").color,
                "%module_name%",
                moduleName
        );
    }

    // Event für das Starten eines Moduls
    @EventListener
    public void onModuleStarted(ModulePostStartEvent event) {
        String moduleName = event.module().moduleConfiguration().name();
        handleNotification(
                notifyModuleStarted,
                "Modul gestartet",
                customMessages.getMessage("moduleStarted").message,
                customMessages.getMessage("moduleStarted").color,
                "%module_name%",
                moduleName
        );
    }

    // Event für das Stoppen eines Moduls
    @EventListener
    public void onModuleStopped(ModulePostStopEvent event) {
        String moduleName = event.module().moduleConfiguration().name();
        handleNotification(
                notifyModuleStopped,
                "Modul gestoppt",
                customMessages.getMessage("moduleStopped").message,
                customMessages.getMessage("moduleStopped").color,
                "%module_name%",
                moduleName
        );
    }

    // Event für das Neuladen eines Moduls
    @EventListener
    public void onModuleReloaded(ModulePostReloadEvent event) {
        String moduleName = event.module().moduleConfiguration().name();
        handleNotification(
                notifyModuleReload,
                "Modul neu geladen",
                customMessages.getMessage("moduleReload").message,
                customMessages.getMessage("moduleReload").color,
                "%module_name%",
                moduleName
        );
    }

    // Event für das Starten und Stoppen eines Servers
    @EventListener
    public void onCloudServiceLifecycle(CloudServicePreLifecycleEvent event) {
        String serviceName = event.serviceInfo().name();
        ServiceLifeCycle targetLifeCycle = event.targetLifecycle();

        if (targetLifeCycle == ServiceLifeCycle.RUNNING) {
            handleNotification(
                    notifyServiceStarted,
                    "Server gestartet",
                    customMessages.getMessage("serviceStarted").message,
                    customMessages.getMessage("serviceStarted").color,
                    "%service_name%",
                    serviceName
            );
        } else if (targetLifeCycle == ServiceLifeCycle.STOPPED) {
            handleNotification(
                    notifyServiceStopped,
                    "Server gestoppt",
                    customMessages.getMessage("serviceStopped").message,
                    customMessages.getMessage("serviceStopped").color,
                    "%service_name%",
                    serviceName
            );
        } else {
            LOGGER.warning("Unbekannter Zielstatus: " + targetLifeCycle + " für Service: " + serviceName);
        }
    }

    private void loadConfig() {
        Path configPath = this.configPath();
        Document document = DocumentFactory.json().parse(configPath);

        this.webhookUrl = document.getString("webhookUrl", "");
        this.notifyModuleLoaded = document.getBoolean("notifyModuleLoaded", true);
        this.notifyModuleStarted = document.getBoolean("notifyModuleStarted", true);
        this.notifyModuleStopped = document.getBoolean("notifyModuleStopped", true);
        this.notifyModuleReload = document.getBoolean("notifyModuleReload", true);
        this.notifyServiceStarted = document.getBoolean("notifyServiceStarted", true);
        this.notifyServiceStopped = document.getBoolean("notifyServiceStopped", true);

        this.customMessages = new Messages(
                createMessage(document, "customMessages.moduleLoaded", "📦 Das Modul %module_name% wurde erfolgreich geladen.", "0x00FF00"),
                createMessage(document, "customMessages.moduleStarted", "🚀 Das Modul %module_name% wurde erfolgreich gestartet.", "0x1E90FF"),
                createMessage(document, "customMessages.moduleStopped", "🛑 Das Modul %module_name% wurde gestoppt.", "0xFF0000"),
                createMessage(document, "customMessages.moduleReload", "🔄 Das Modul %module_name% wurde neu geladen.", "0xFFA500"),
                createMessage(document, "customMessages.serviceStarted", "🚀 Der Server %service_name% wurde gestartet.", "0x32CD32"),
                createMessage(document, "customMessages.serviceStopped", "🛑 Der Server %service_name% wurde gestoppt.", "0xFF4500")
        );

        if (!document.contains("webhookUrl")) {
            LOGGER.warning("Webhook-URL fehlt in der Konfiguration. Standardwerte werden verwendet.");
            saveConfig();
        }
    }

    private void saveConfig() {
        Path configPath = this.configPath();
        DocumentFactory.json()
                .newDocument()
                .append("webhookUrl", webhookUrl)
                .append("notifyModuleLoaded", notifyModuleLoaded)
                .append("notifyModuleStarted", notifyModuleStarted)
                .append("notifyModuleStopped", notifyModuleStopped)
                .append("notifyModuleReload", notifyModuleReload)
                .append("notifyServiceStarted", notifyServiceStarted)
                .append("notifyServiceStopped", notifyServiceStopped)
                .append("customMessages.moduleLoaded.message", customMessages.getMessage("moduleLoaded").message)
                .append("customMessages.moduleLoaded.color", customMessages.getMessage("moduleLoaded").color)
                .append("customMessages.moduleStarted.message", customMessages.getMessage("moduleStarted").message)
                .append("customMessages.moduleStarted.color", customMessages.getMessage("moduleStarted").color)
                .append("customMessages.moduleStopped.message", customMessages.getMessage("moduleStopped").message)
                .append("customMessages.moduleStopped.color", customMessages.getMessage("moduleStopped").color)
                .append("customMessages.moduleReload.message", customMessages.getMessage("moduleReload").message)
                .append("customMessages.moduleReload.color", customMessages.getMessage("moduleReload").color)
                .append("customMessages.serviceStarted.message", customMessages.getMessage("serviceStarted").message)
                .append("customMessages.serviceStarted.color", customMessages.getMessage("serviceStarted").color)
                .append("customMessages.serviceStopped.message", customMessages.getMessage("serviceStopped").message)
                .append("customMessages.serviceStopped.color", customMessages.getMessage("serviceStopped").color)
                .writeTo(configPath);

        LOGGER.info("Konfiguration wurde gespeichert: " + configPath.toString());
    }

    private CustomMessage createMessage(Document document, String key, String defaultMessage, String defaultColor) {
        return new CustomMessage(
                document.getString(key + ".message", defaultMessage),
                validateHexColor(document.getString(key + ".color", defaultColor))
        );
    }

    private void handleNotification(boolean condition, String title, String messageTemplate, String color, String placeholder, String value) {
        if (condition) {
            String message = messageTemplate.replace(placeholder, value);
            sendDiscordMessage(title, message, color);
        }
    }

    private void sendDiscordMessage(String title, String description, String hexColor) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            LOGGER.warning("Webhook-URL ist leer. Nachricht wird nicht gesendet.");
            return;
        }

        try {
            int color = Integer.parseInt(hexColor.replace("0x", ""), 16);
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
        }
    }

    private String validateHexColor(String hexColor) {
        if (hexColor == null || !hexColor.matches("0x[0-9A-Fa-f]{6}")) {
            return "0xFFFFFF"; // Standardfarbe (Weiß)
        }
        return hexColor;
    }
}