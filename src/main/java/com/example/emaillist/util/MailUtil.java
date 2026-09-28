package com.example.emaillist.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Sends transactional email through Brevo's HTTPS API. */
public final class MailUtil {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private MailUtil() {
    }

    public static void sendWelcomeEmail(String to, String firstName)
            throws IOException {
        String apiKey = requiredEnvironmentVariable("BREVO_API_KEY");
        String senderEmail = requiredEnvironmentVariable("BREVO_SENDER_EMAIL");

        String subject = "Cảm ơn bạn đã đăng ký";
        String body = "Chào " + firstName + ",\n\n"
                + "Cảm ơn bạn đã đăng ký vào email list của chúng tôi. "
                + "Bạn sẽ nhận được các cập nhật mới qua email này.\n\n"
                + "Chúc bạn một ngày tốt lành!";

        String payload = "{"
                + "\"sender\":{\"name\":\"Email List\",\"email\":" + json(senderEmail) + "},"
                + "\"to\":[{\"email\":" + json(to) + ",\"name\":" + json(firstName) + "}],"
                + "\"subject\":" + json(subject) + ","
                + "\"textContent\":" + json(body)
                + "}";

        HttpRequest request = HttpRequest.newBuilder(URI.create(BREVO_API_URL))
                .timeout(Duration.ofSeconds(15))
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<Void> response;
        try {
            response = HTTP_CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );
        } catch (IOException e) {
            throw new IOException("Could not connect to Brevo API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Sending email was interrupted", e);
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Brevo API returned HTTP " + response.statusCode());
        }
    }

    private static String requiredEnvironmentVariable(String name)
            throws IOException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IOException("Missing required environment variable: " + name);
        }
        return value.trim();
    }

    private static String json(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                }
            }
        }
        return escaped.append('"').toString();
    }
}
