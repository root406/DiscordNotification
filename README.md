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
  "notifyModuleReload": true,
  "notifyModuleStopped": true,
  "notifyServiceStarted": true,
  "notifyServiceStopped": true,
  "customMessages": {
    "moduleLoaded": {
      "message": "📦 Das Modul %module_name% wurde erfolgreich geladen.",
      "color": "0x00FF00"
    },
    "moduleStarted": {
      "message": "🚀 Das Modul %module_name% wurde erfolgreich gestartet.",
      "color": "0x1E90FF"
    },
    "moduleStopped": {
      "message": "🛑 Das Modul %module_name% wurde gestoppt.",
      "color": "0xFF0000"
    },
    "moduleReload": {
      "message": "🛑 Das Modul %module_name% wurde gestoppt.",
      "color": "0xFF0000"
    },
    "serviceStarted": {
      "message": "🚀 Der Server %service_name% wurde gestartet.",
      "color": "0x32CD32"
    },
    "serviceStopped": {
      "message": "🛑 Der Server %service_name% wurde gestoppt.",
      "color": "0xFF4500"
    }
  }
}

