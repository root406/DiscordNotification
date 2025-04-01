# CloudNet Discord Notification

🚀 Ein CloudNet-Modul zur Benachrichtigung über Discord-Webhooks bei wichtigen Ereignissen wie Modul- und Server-Start/Stop.

## 📌 Features
- Sendet Discord-Nachrichten bei:
  - Modul geladen, gestartet, gestoppt
  - Server gestartet, gestoppt
- Anpassbare Konfiguration über eine JSON-Datei
- Einfach zu installieren und zu nutzen

## 📥 Installation
1. Lade die neueste Version des Moduls herunter.
2. Platziere die JAR-Datei im `modules`-Ordner von CloudNet.
3. Starte CloudNet neu, um das Modul zu laden.

## ⚙️ Konfiguration
Nach dem ersten Start wird eine Konfigurationsdatei (`config.json`) generiert.  
Du kannst die Einstellungen anpassen:

```json
{
  "webhookUrl": "DEIN_DISCORD_WEBHOOK",
  "notifyModuleLoaded": true,
  "notifyModuleStarted": true,
  "notifyModuleStopped": true,
  "notifyServiceStarted": true,
  "notifyServiceStopped": true
}
