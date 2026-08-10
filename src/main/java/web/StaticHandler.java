package web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Serves the single page front end from the packaged resources.
 */
final class StaticHandler implements HttpHandler {

    private static final String ROOT = "/web";
    private static final String INDEX = "/index.html";
    private static final int NOT_FOUND = 404;
    private static final int OK = 200;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if ("/".equals(path) || path.isBlank()) {
            path = INDEX;
        }

        final byte[] body = read(ROOT + path);

        if (body == null) {
            respond(exchange, NOT_FOUND, "text/plain", "Not found".getBytes(StandardCharsets.UTF_8));
        }
        else {
            respond(exchange, OK, contentType(path), body);
        }
    }

    private static byte[] read(String resource) throws IOException {
        final byte[] bytes;

        try (InputStream stream = StaticHandler.class.getResourceAsStream(resource)) {
            if (stream == null) {
                bytes = null;
            }
            else {
                bytes = stream.readAllBytes();
            }
        }
        return bytes;
    }

    private static String contentType(String path) {
        final String type;

        if (path.endsWith(".html")) {
            type = "text/html; charset=utf-8";
        }
        else if (path.endsWith(".css")) {
            type = "text/css; charset=utf-8";
        }
        else if (path.endsWith(".js")) {
            type = "application/javascript; charset=utf-8";
        }
        else if (path.endsWith(".png")) {
            type = "image/png";
        }
        else {
            type = "application/octet-stream";
        }
        return type;
    }

    private static void respond(HttpExchange exchange, int status, String type, byte[] body)
            throws IOException {
        exchange.getResponseHeaders().add("Content-Type", type);
        exchange.sendResponseHeaders(status, body.length);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}
