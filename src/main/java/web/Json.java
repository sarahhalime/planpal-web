package web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

/**
 * Reading requests and writing json responses, shared by the api handlers.
 */
final class Json {

    static final int OK = 200;
    static final int BAD_REQUEST = 400;
    static final int UNAUTHORIZED = 401;
    static final int NOT_FOUND = 404;
    static final int SERVER_ERROR = 500;

    private Json() {
    }

    /**
     * Reads a json request body.
     *
     * @param exchange the request
     * @return the parsed body, empty when there was none
     * @throws IOException when the body cannot be read
     */
    static JSONObject read(HttpExchange exchange) throws IOException {
        final String body = new String(
                exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        final JSONObject json;

        if (body.isBlank()) {
            json = new JSONObject();
        }
        else {
            json = new JSONObject(body);
        }
        return json;
    }

    /**
     * Reads one decoded query parameter.
     *
     * @param exchange the request
     * @param name the parameter name
     * @return the value, or an empty string when absent
     */
    static String query(HttpExchange exchange, String name) {
        final Map<String, String> values = new HashMap<>();
        final String raw = exchange.getRequestURI().getRawQuery();

        if (raw != null && !raw.isBlank()) {
            for (final String pair : raw.split("&")) {
                final int equals = pair.indexOf('=');
                if (equals > 0) {
                    values.put(pair.substring(0, equals),
                            URLDecoder.decode(pair.substring(equals + 1), StandardCharsets.UTF_8));
                }
            }
        }
        return values.getOrDefault(name, "");
    }

    /**
     * Sends a successful json response.
     *
     * @param exchange the request
     * @param body what to send
     * @throws IOException when the response cannot be written
     */
    static void ok(HttpExchange exchange, JSONObject body) throws IOException {
        send(exchange, OK, body);
    }

    /**
     * Sends an error response.
     *
     * @param exchange the request
     * @param status the status code
     * @param message what went wrong
     * @throws IOException when the response cannot be written
     */
    static void fail(HttpExchange exchange, int status, String message) throws IOException {
        send(exchange, status, new JSONObject().put(
                "error", message == null || message.isBlank() ? "Something went wrong." : message));
    }

    /**
     * Sends the error when there is one, and the success body otherwise.
     *
     * @param exchange the request
     * @param error what the interactor reported, or null
     * @param success what to send when there was no error
     * @throws IOException when the response cannot be written
     */
    static void result(HttpExchange exchange, String error, JSONObject success) throws IOException {
        if (error == null) {
            ok(exchange, success);
        }
        else {
            fail(exchange, BAD_REQUEST, error);
        }
    }

    private static void send(HttpExchange exchange, int status, JSONObject body)
            throws IOException {
        final byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
