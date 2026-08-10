package data_access;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import entity.CommonUserFactory;
import entity.User;
import entity.UserFactory;
import use_case.add_guests.AddGuestUserDataAccessInterface;
import use_case.change_display_name.ChangeDisplayNameDataAccessInterface;
import use_case.change_password.ChangePasswordDataAccessInterface;
import use_case.change_preferred_currency.ChangePreferredCurrencyUserDataAccessInterface;
import use_case.change_username.ChangeUsernameDataAccessInterface;
import use_case.login.LoginDataAccessInterface;
import use_case.logout.LogoutDataAccessInterface;
import use_case.signup.SignupDataAccessInterface;

/**
 * In-memory implementation of the DAO for storing user data. Shared by the
 * signup, login, logout, and user-setting use cases.
 */
public class InMemoryUserDataAccessObject implements SignupDataAccessInterface,
        LoginDataAccessInterface,
        LogoutDataAccessInterface,
        ChangeUsernameDataAccessInterface,
        ChangePasswordDataAccessInterface,
        ChangeDisplayNameDataAccessInterface,
        AddGuestUserDataAccessInterface,
        ChangePreferredCurrencyUserDataAccessInterface {

    private final UserFactory userFactory = new CommonUserFactory();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private String currentUsername;

    @Override
    public boolean existsByName(String identifier) {
        return this.users.containsKey(identifier);
    }

    @Override
    public void save(User user) {
        this.users.put(user.getUsername(), user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.users.containsKey(username);
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>(this.users.keySet());
    }

    @Override
    public User getUser(String username) {
        return this.users.get(username);
    }

    @Override
    public String findUsernameByEmail(String email) {
        String match = null;

        if (email != null && !email.isBlank()) {
            for (final User candidate : this.users.values()) {
                if (email.equalsIgnoreCase(candidate.getEmail())) {
                    match = candidate.getUsername();
                    break;
                }
            }
        }

        return match;
    }

    @Override
    public void clearCurrentUser() {
        this.currentUsername = null;
    }

    @Override
    public void changeUsername(String oldUsername, User updatedUser) {
        this.users.remove(oldUsername);
        this.users.put(updatedUser.getUsername(), updatedUser);
    }

    @Override
    public void changePassword(User user) {
        this.users.put(user.getUsername(), user);
    }

    @Override
    public String getPassword(String username) {
        final User user = this.users.get(username);
        String password = null;
        if (user != null) {
            password = user.getPassword();
        }
        return password;
    }

    @Override
    public void changeDisplayName(User user) {
        this.users.put(user.getUsername(), user);
    }

    @Override
    public String getDisplayName(String username) {
        final User user = this.users.get(username);
        String displayName = null;
        if (user != null) {
            displayName = user.getDisplayName();
        }
        return displayName;
    }

    @Override
    public void changePreferredCurrency(String username,
                                        String preferredCurrencyCode) {
        final User user = this.users.get(username);
        final User updatedUser = this.userFactory.create(
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPassword(),
                "",
                preferredCurrencyCode
        );
        this.users.put(username, updatedUser);
    }
}
