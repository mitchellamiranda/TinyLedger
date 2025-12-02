package com.mitchell.tinyledger.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {}

    public static <T> T readBody(HttpExchange ex, Class<T> type) throws IOException {
        try (InputStream is = ex.getRequestBody()) {
            return MAPPER.readValue(is, type);
        }
    }

    public static byte[] toBytes(Object o) throws IOException {
        return MAPPER.writeValueAsString(o).getBytes(StandardCharsets.UTF_8);
    }

    public static void sendJson(HttpExchange ex, int status, Object payload) throws IOException {
        byte[] out = toBytes(payload);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, out.length);
        ex.getResponseBody().write(out);
        ex.getResponseBody().close();
    }

    public static void sendError(HttpExchange ex, int status, String message) throws IOException {
        sendJson(ex, status, new ErrorDTO(message));
    }

    private static class ErrorDTO {
        public final String error;
        ErrorDTO(String e) { this.error = e; }
    }
}
