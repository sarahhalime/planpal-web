package data_access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.User;
import entity.UserFactory;
import use_case.add_guests.AddGuestUserDataAccessInterface;
import use_case.change_display_name.ChangeDisplayNameDataAccessInterface;
import use_case.change_password.ChangePasswordDataAccessInterface;
import use_case.change_preferred_currency.ChangePreferredCurrencyUserDataAccessInterface;
import use_case.change_username.ChangeUsernameDataAccessInterface;
import use_case.edit_home_address.EditHomeAddressDataAccessInterface;
import use_case.login.LoginDataAccessInterface;
import use_case.logout.LogoutDataAccessInterface;
import use_case.signup.SignupDataAccessInterface;

public class FileUserDataAccessObject implements SignupDataAccessInterface,
        LoginDataAccessInterface,
        LogoutDataAccessInterface,
        ChangeUsernameDataAccessInterface,
        ChangePasswordDataAccessInterface,
        ChangeDisplayNameDataAccessInterface,
        AddGuestUserDataAccessInterface,
        EditHomeAddressDataAccessInterface,
        ChangePreferredCurrencyUserDataAccessInterface {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String DISPLAY_NAME_KEY = "displayName";
    private static final String PREFERRED_CURRENCY_KEY = "preferredCurrency";

    private final UserFactory userFactory;
    private final String filePath;
    private final Map<String, User> users = new HashMap<>();
    private String currentUsername;

    public FileUserDataAccessObject(String fileName, UserFactory userFactory) {
        this.filePath = JsonFileUtil.resolvePath(fileName);
        this.userFactory = userFactory;
        loadFromFile();
    }

    private void loadFromFile() {
        final JSONArray array = JsonFileUtil.readArray(filePath);
        for (int i = 0; i < array.length(); i++) {
            final JSONObject json = array.getJSONObject(i);
            final User user = buildUser(json);
            users.put(user.getUsername(), user);
        }
    }

    private void saveToFile() {
        final JSONArray array = new JSONArray();
        for (User user : users.values()) {
            array.put(toJson(user));
        }
        JsonFileUtil.writeArray(filePath, array);
    }

    /**
     * Reconstructs a User entity from its JSON representation.
     * @param json the JSON object that stores the user data
     * @return the user object reconstructed from the JSON
     */
    private User buildUser(JSONObject json) {
        return userFactory.create(
                json.getString(USERNAME_KEY),
                json.getString(DISPLAY_NAME_KEY),
                json.getString(EMAIL_KEY),
                json.getString(PASSWORD_KEY),
                "",
                json.optString(PREFERRED_CURRENCY_KEY, "CAD")
        );
    }

    /**
     * Serializes a User entity into its JSON representation.
     * @param user a user object corresponding to an user of the app
     * @return a JSON object with the user data
     */
    private JSONObject toJson(User user) {
        final JSONObject json = new JSONObject();
        json.put(USERNAME_KEY, user.getUsername());
        json.put(PASSWORD_KEY, user.getPassword());
        json.put(DISPLAY_NAME_KEY, user.getDisplayName());
        json.put(EMAIL_KEY, user.getEmail());
        json.put(PREFERRED_CURRENCY_KEY, user.getPreferredCurrency());
        return json;
    }

    @Override
    public boolean existsByName(String identifier) {
        return users.containsKey(identifier);
    }

    @Override
    public void save(User user) {
        users.put(user.getUsername(), user);
        saveToFile();
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }

    @Override
    public String findUsernameByEmail(String email) {
        String match = null;

        if (email != null && !email.isBlank()) {
            for (final User candidate : users.values()) {
                if (email.equalsIgnoreCase(candidate.getEmail())) {
                    match = candidate.getUsername();
                    break;
                }
            }
        }

        return match;
    }

    @Override
    public void changeHomeAddress(User user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public void clearCurrentUser() {
        this.currentUsername = null;
    }

    @Override
    public void changeUsername(String oldUsername, User updatedUser) {
        users.remove(oldUsername);
        users.put(updatedUser.getUsername(), updatedUser);
        saveToFile();
    }

    @Override
    public void changePassword(User user) {
        users.put(user.getUsername(), user);
        saveToFile();
    }

    @Override
    public String getPassword(String username) {
        final User user = users.get(username);
        return user.getPassword();
    }

    @Override
    public void changeDisplayName(User user) {
        users.put(user.getUsername(), user);
        saveToFile();
    }

    @Override
    public String getDisplayName(String username) {
        final User user = users.get(username);
        return user.getDisplayName();
    }

    @Override
    public void changePreferredCurrency(String username,
                                        String preferredCurrencyCode) {
        final User user = users.get(username);
        final User updatedUser = userFactory.create(
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPassword(),
                "",
                preferredCurrencyCode
        );
        users.put(username, updatedUser);
        saveToFile();
    }
}
