package de.root406.discordnotification.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;

public class DiscordWebhook {

    public static void sendWebhook(String webhookUrl, WebhookMessage message) throws IOException {
        if (webhookUrl == null) {
            throw new IllegalArgumentException("Webhook URL cannot be null.");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }

        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonPayload = message.toJson();
        if (jsonPayload == null) {
            throw new IllegalStateException("The JSON payload is null.");
        }

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(errorStream, "utf-8"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
            }
        }
    }

    public static class WebhookMessage {
        private String content;
        private String username;
        private String avatarUrl;
        private EmbedObject[] embeds;

        public WebhookMessage setContent(String content) {
            this.content = content;
            return this;
        }

        public WebhookMessage setUsername(String username) {
            this.username = username;
            return this;
        }

        public WebhookMessage setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public WebhookMessage addEmbed(EmbedObject embed) {
            if (embeds == null) {
                embeds = new EmbedObject[] { embed };
            } else {
                EmbedObject[] newEmbeds = new EmbedObject[embeds.length + 1];
                System.arraycopy(embeds, 0, newEmbeds, 0, embeds.length);
                newEmbeds[embeds.length] = embed;
                embeds = newEmbeds;
            }
            return this;
        }

        public String toJson() {
            Gson gson = new GsonBuilder().serializeNulls().create(); // Ensure nulls are serialized if needed
            return gson.toJson(this);
        }
    }

    public static class EmbedObject {
        private String title;
        private String description;
        private String url;
        private String timestamp;
        private Footer footer;
        private Image image;
        private Thumbnail thumbnail;
        private int color; // Als Hex-String
        private Field[] fields;

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EmbedObject setFooter(String text, String iconUrl) {
            if (text != null && !text.isEmpty()) {
                this.footer = new Footer(text, iconUrl);
            }
            return this;
        }

        public EmbedObject setImage(String imageUrl) {
            this.image = new Image(imageUrl);
            return this;
        }

        public EmbedObject setThumbnail(String thumbnailUrl) {
            this.thumbnail = new Thumbnail(thumbnailUrl);
            return this;
        }

        public EmbedObject setColor(int color) {
            this.color = color;
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            if (fields == null) {
                fields = new Field[] { new Field(name, value, inline) };
            } else {
                Field[] newFields = new Field[fields.length + 1];
                System.arraycopy(fields, 0, newFields, 0, fields.length);
                newFields[fields.length] = new Field(name, value, inline);
                fields = newFields;
            }
            return this;
        }

        public String toJson() {
            Gson gson = new GsonBuilder().serializeNulls().create(); // Ensure nulls are serialized if needed
            return gson.toJson(this);
        }
    }

    public static class Field {
        private String name;
        private String value;
        private boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }

    public static class Footer {
        private String text;
        private String icon_url;

        public Footer(String text, String icon_url) {
            this.text = text;
            this.icon_url = (icon_url != null && !icon_url.equals("null")) ? icon_url : null;
        }
    }

    public static class Image {
        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Thumbnail {
        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }
    }
}