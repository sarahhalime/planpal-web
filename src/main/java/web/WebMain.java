package web;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

import data_access.CurrencyApiDataAccessObject;
import data_access.FileEventDataAccessObject;
import data_access.SqliteSocialDataAccessObject;
import data_access.SqliteUserDataAccessObject;
import entity.CommonActivityFactory;
import entity.CommonEventFactory;
import entity.CommonExpenseFactory;
import entity.CommonUserFactory;

/**
 * Serves PlanPal over http so it can be used from a phone browser.
 *
 * <p>The entity and use case layers are shared with the desktop application unchanged. Only
 * the delivery mechanism differs: instead of Swing views calling controllers, http handlers
 * call the same interactors and render the results as json.</p>
 */
public final class WebMain {

    private static final int DEFAULT_PORT = 8080;
    private static final String PORT_ENV = "PORT";
    private static final int SHUTDOWN_DELAY_SECONDS = 2;
    private static final int THREAD_POOL_SIZE = 16;

    private WebMain() {
    }

    /**
     * Starts the web server.
     *
     * @param args ignored
     * @throws IOException when the port cannot be bound
     */
    public static void main(String[] args) throws IOException {
        final int port = resolvePort();

        final FileEventDataAccessObject eventDataAccess = new FileEventDataAccessObject(
                "events.json",
                new CommonEventFactory(),
                new CommonExpenseFactory(),
                new CommonActivityFactory()
        );
        final SqliteUserDataAccessObject userDataAccess =
                new SqliteUserDataAccessObject("users.db", new CommonUserFactory());
        final SqliteSocialDataAccessObject socialDataAccess =
                new SqliteSocialDataAccessObject("users.db");

        final CurrencyApiDataAccessObject currencyDataAccess = new CurrencyApiDataAccessObject();
        final ApiHandler api = new ApiHandler(userDataAccess, eventDataAccess, socialDataAccess);
        final AccountApiHandler account =
                new AccountApiHandler(userDataAccess, socialDataAccess, currencyDataAccess);
        final TripApiHandler trip =
                new TripApiHandler(eventDataAccess, userDataAccess, socialDataAccess);
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Longest matching context wins, so the account routes take precedence over /api/.
        server.createContext("/api/signup", account);
        server.createContext("/api/profile", account);
        server.createContext("/api/follows", account);
        server.createContext("/api/follow", account);
        server.createContext("/api/users/", account);
        server.createContext("/api/currencies", account);
        server.createContext("/api/account/", account);
        server.createContext("/api/trip/", trip);
        server.createContext("/api/", api);
        server.createContext("/", new StaticHandler());
        server.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
        server.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> server.stop(SHUTDOWN_DELAY_SECONDS)));

        System.out.println("PlanPal web is running on http://localhost:" + port);
    }

    private static int resolvePort() {
        final String configured = System.getenv(PORT_ENV);
        int port = DEFAULT_PORT;

        if (configured != null && !configured.isBlank()) {
            try {
                port = Integer.parseInt(configured.trim());
            }
            catch (final NumberFormatException exception) {
                port = DEFAULT_PORT;
            }
        }
        return port;
    }
}
