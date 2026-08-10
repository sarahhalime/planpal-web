package entity;

/**
 * To create CommonUser objects.
 */
public class CommonUserFactory implements UserFactory {

    @Override
    public User create(String username, String displayName, String password) {
        return new CommonUser(username, displayName, "", password);
    }

    @Override
    public User create(String username, String displayName, String email, String password) {
        return new CommonUser(username, displayName, email, password);
    }

    @Override
    public User create(String username, String displayName, String email,
                       String password, String address) {
        return new CommonUser(username, displayName, email, password, address);
    }

    @Override
    public User create(String username, String displayName, String email,
                       String password, String address, String preferredCurrency) {
        return new CommonUser(username, displayName, email, password, address, preferredCurrency);
    }
}
