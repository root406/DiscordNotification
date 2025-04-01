package de.root406.discordnotification;

import de.root406.discordnotification.utils.DiscordWebhook;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.module.ModulePostLoadEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostReloadEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostStartEvent;
import eu.cloudnetservice.driver.event.events.module.ModulePostStopEvent;
import eu.cloudnetservice.driver.event.events.service.CloudServiceLifecycleChangeEvent;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.module.ModuleWrapper;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import java.nio.file.Path;

import eu.cloudnetservice.driver.service.ServiceLifeCycle;
import eu.cloudnetservice.node.event.service.CloudServicePreLifecycleEvent;
import lombok.NonNull;

public class DiscordNotification extends DriverModule {

    private String webhookUrl;
    private boolean notifyModuleLoaded;
    private boolean notifyModuleStarted;
    private boolean notifyModuleStopped;
    private boolean notifyServiceStarted;
    private boolean notifyServiceStopped;

    @ModuleTask(lifecycle = ModuleLifeCycle.LOADED)
    public void onLoad() {
        loadConfig();
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void onStart() {
        if (notifyModuleStarted) {
            sendDiscordMessage("✅ Modul gestartet", "Das CloudNet-Discord-Modul wurde erfolgreich gestartet.");
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void onStop() {
        if (notifyModuleStopped) {
            sendDiscordMessage("❌ Modul gestoppt", "Das CloudNet-Discord-Modul wurde gestoppt.");
        }
    }

    @EventListener
    public void onModuleLoad(ModulePostLoadEvent event) {
        ModuleWrapper module = event.module();
        String moduleName = module.moduleConfiguration().name();
        if (notifyModuleLoaded) {
            sendDiscordMessage("📦 Modul geladen", "Das Modul **" + moduleName + "** wurde geladen.");
        }
    }

    @EventListener
    public void onModuleStart(ModulePostStartEvent event) {
        ModuleWrapper module = event.module();
        String moduleName = module.moduleConfiguration().name();
        if (notifyModuleStarted) {
            sendDiscordMessage("🚀 Modul gestartet", "Das Modul **" + moduleName + "** wurde gestartet.");
        }
    }

    @EventListener
    public void onModuleRestart(ModulePostReloadEvent event) {
        ModuleWrapper module = event.module();
        String moduleName = module.moduleConfiguration().name();
        if (notifyModuleStarted) {
            sendDiscordMessage("⭕ Modul Reloaded", "Das Modul **" + moduleName + "** wurde reloaded.");
        }
    }

    @EventListener
    public void onModuleStop(ModulePostStopEvent event) {
        ModuleWrapper module = event.module();
        String moduleName = module.moduleConfiguration().name();
        if (notifyModuleStopped) {
            sendDiscordMessage("🛑 Modul gestoppt", "Das Modul **" + moduleName + "** wurde gestoppt.");
        }
    }


    @EventListener
    public void onCloudServiceLifecycle(CloudServicePreLifecycleEvent event) {
        ServiceLifeCycle targetLifeCycle = event.targetLifecycle();
        String serviceName = event.serviceInfo().name();

        if (targetLifeCycle == ServiceLifeCycle.STOPPED && notifyServiceStopped) {
            sendDiscordMessage("🛑 Server wird gestoppt", "Der Server **" + serviceName + "** wird gestoppt.");
        }

        if (targetLifeCycle == ServiceLifeCycle.RUNNING && notifyServiceStarted) {
            sendDiscordMessage("🚀 Server wird gestartet", "Der Server **" + serviceName + "** wird gestartet.");
        }
    }


    private void loadConfig() {
        Path configPath = this.configPath();
        Document document = DocumentFactory.json().parse(configPath);

        this.webhookUrl = document.getString("webhookUrl", "");
        this.notifyModuleLoaded = document.getBoolean("notifyModuleLoaded", true);
        this.notifyModuleStarted = document.getBoolean("notifyModuleStarted", true);
        this.notifyModuleStopped = document.getBoolean("notifyModuleStopped", true);
        this.notifyServiceStarted = document.getBoolean("notifyServiceStarted", true);
        this.notifyServiceStopped = document.getBoolean("notifyServiceStopped", true);

        if (!document.contains("webhookUrl")) {
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
                .append("notifyServiceStarted", notifyServiceStarted)
                .append("notifyServiceStopped", notifyServiceStopped)
                .writeTo(this.configPath());
    }

    private void sendDiscordMessage(String title, String description) {
        if (webhookUrl == null || webhookUrl.isEmpty()) return;

        try {
            DiscordWebhook.WebhookMessage webhookMessage = new DiscordWebhook.WebhookMessage()
                    .addEmbed(new DiscordWebhook.EmbedObject()
                            .setTitle(title)
                            .setDescription(description)
                            .setColor(0x59136)
                    );
            DiscordWebhook.sendWebhook(webhookUrl, webhookMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
