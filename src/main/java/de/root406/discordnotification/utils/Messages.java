package de.root406.discordnotification.utils;

public class Messages {
    public final CustomMessage moduleLoaded;
    public final CustomMessage moduleStarted;
    public final CustomMessage moduleStopped;
    public final CustomMessage moduleReload;
    public final CustomMessage serviceStarted;
    public final CustomMessage serviceStopped;

    public Messages(CustomMessage moduleLoaded, CustomMessage moduleStarted, CustomMessage moduleStopped, CustomMessage serviceStarted, CustomMessage serviceStopped, CustomMessage moduleReload) {
        this.moduleLoaded = moduleLoaded;
        this.moduleStarted = moduleStarted;
        this.moduleStopped = moduleStopped;
        this.moduleReload = moduleReload;
        this.serviceStarted = serviceStarted;
        this.serviceStopped = serviceStopped;
    }
}