package entity;

/**
 * An implementation of the User interface.
 */
public class CommonUser implements User {

    private static final String DEFAULT_CURRENCY = "CAD";

    private final String username;
    private final String displayName;
    private final String email;
    private final String password;
    private final String address;
    private final String preferredCurrency;

    public CommonUser(String username, String displayName, String email, String password) {
        this(username, displayName, email, password, "", DEFAULT_CURRENCY);
    }

    public CommonUser(String username, String displayName, String email,
                      String password, String address) {
        this(username, displayName, email, password, address, DEFAULT_CURRENCY);
    }

    public CommonUser(String username, String displayName,
                      String email, String password, String address, String preferredCurrency) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.preferredCurrency = preferredCurrency;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String getPreferredCurrency() {
        return preferredCurrency;
    }
}
