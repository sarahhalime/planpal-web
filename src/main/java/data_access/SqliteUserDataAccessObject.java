package data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import use_case.search_users.SearchUsersDataAccessInterface;
import use_case.signup.SignupDataAccessInterface;

/**
 * Stores users in a local SQLite database.
 */
public class SqliteUserDataAccessObject implements SignupDataAccessInterface,
        LoginDataAccessInterface,
        LogoutDataAccessInterface,
        ChangeUsernameDataAccessInterface,
        ChangePasswordDataAccessInterface,
        ChangeDisplayNameDataAccessInterface,
        AddGuestUserDataAccessInterface,
        EditHomeAddressDataAccessInterface,
        SearchUsersDataAccessInterface,
        ChangePreferredCurrencyUserDataAccessInterface {

    private static final String SELECT_PREFIX = "SELECT ";
    private static final String USERNAME_COLUMN = "username";
    private static final String DISPLAY_NAME_COLUMN = "display_name";
    private static final String EMAIL_COLUMN = "email";
    private static final String PASSWORD_COLUMN = "password";

    private static final String PREFERRED_CURRENCY_COLUMN = "preferred_currency";
    private static final String DEFAULT_CURRENCY = "CAD";
    private static final String EQUALS_PLACEHOLDER = " = ?";
    private static final String COLUMN_SEPARATOR = ", ";
    private static final int EMAIL_PARAMETER_INDEX = 3;
    private static final int PASSWORD_PARAMETER_INDEX = 4;
    private static final int PREFERRED_CURRENCY_PARAMETER_INDEX = 5;
    private static final String TEXT_COLUMN_SUFFIX = " TEXT, ";

    private final UserFactory userFactory;
    private final Connection connection;
    private String currentUsername;

    public SqliteUserDataAccessObject(String fileName, UserFactory userFactory) {
        this.userFactory = userFactory;
        final String path = JsonFileUtil.resolvePath(fileName);
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            this.createTable();
            this.addPreferredCurrencyColumn();
        }
        catch (SQLException exception) {
            throw new UserDatabaseException("Could not open the user database.", exception);
        }
    }

    private void createTable() throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS users ("
                + USERNAME_COLUMN + " TEXT PRIMARY KEY, "
                + DISPLAY_NAME_COLUMN + TEXT_COLUMN_SUFFIX
                + EMAIL_COLUMN + TEXT_COLUMN_SUFFIX
                + PASSWORD_COLUMN + TEXT_COLUMN_SUFFIX
                + PREFERRED_CURRENCY_COLUMN + " TEXT DEFAULT '"
                + DEFAULT_CURRENCY + "')";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    private void addPreferredCurrencyColumn() throws SQLException {
        boolean columnExists = false;

        final String checkSql = "PRAGMA table_info(users)";
        try (PreparedStatement statement =
                     this.connection.prepareStatement(checkSql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                if (PREFERRED_CURRENCY_COLUMN.equals(
                        resultSet.getString("name"))) {
                    columnExists = true;
                }
            }
        }

        if (!columnExists) {
            final String alterSql = "ALTER TABLE users ADD COLUMN "
                    + PREFERRED_CURRENCY_COLUMN
                    + " TEXT DEFAULT 'CAD'";
            try (PreparedStatement statement =
                         this.connection.prepareStatement(alterSql)) {
                statement.execute();
            }
        }
    }

    @Override
    public boolean existsByName(String identifier) {
        return this.existsByUsername(identifier);
    }

    @Override
    public boolean existsByUsername(String username) {
        final String sql = "SELECT 1 FROM users WHERE " + USERNAME_COLUMN + EQUALS_PLACEHOLDER;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
        catch (SQLException exception) {
            throw new UserDatabaseException("Could not look up the user.", exception);
        }
    }

    @Override
    public void save(User user) {
        this.upsert(user);
    }

    @Override
    public List<String> getAllUsernames() {
        final String sql = SELECT_PREFIX + USERNAME_COLUMN + " FROM users";
        final List<String> usernames = new ArrayList<>();
        try (PreparedStatement statement = this.connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                usernames.add(resultSet.getString(USERNAME_COLUMN));
            }
        }
        catch (SQLException exception) {
            throw new UserDatabaseException("Could not list the users.", exception);
        }
        return usernames;
    }

    @Override
    public String findUsernameByEmail(String email) {
        final String sql = SELECT_PREFIX + USERNAME_COLUMN + " FROM users WHERE "
                + EMAIL_COLUMN + EQUALS_PLACEHOLDER + " COLLATE NOCASE";
        String match = null;

        if (email != null && !email.isBlank()) {
            try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
                statement.setString(1, email);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        match = resultSet.getString(USERNAME_COLUMN);
                    }
                }
            }
            catch (SQLException exception) {
                throw new UserDatabaseException("Could not look up the email.", exception);
            }
        }

        return match;
    }

    @Override
    public User getUser(String username) {
        final String sql = "SELECT * FROM users WHERE " + USERNAME_COLUMN + EQUALS_PLACEHOLDER;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                User user = null;
                if (resultSet.next()) {
                    user = this.userFactory.create(
                            resultSet.getString(USERNAME_COLUMN),
                            resultSet.getString(DISPLAY_NAME_COLUMN),
                            resultSet.getString(EMAIL_COLUMN),
                            resultSet.getString(PASSWORD_COLUMN),
                            "",
                            resultSet.getString(PREFERRED_CURRENCY_COLUMN)
                    );
                }
                return user;
            }
        }
        catch (SQLException exception) {
            throw new UserDatabaseException("Could not load the user.", exception);
        }
    }

    @Override
    public void changeHomeAddress(User user) {
        this.upsert(user);
    }

    @Override
    public void clearCurrentUser() {
        this.currentUsername = null;
    }

    @Override
    public void changeUsername(String oldUsername, User updatedUser) {
        final String sql = "DELETE FROM users WHERE " + USERNAME_COLUMN + EQUALS_PLACEHOLDER;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, oldUsername);
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new UserDatabaseException("Could not change the username.", exception);
        }
        this.upsert(updatedUser);
    }

    @Override
    public void changePassword(User user) {
        this.upsert(user);
    }

    @Override
    public String getPassword(String username) {
        return this.readColumn(username, PASSWORD_COLUMN);
    }

    @Override
    public void changeDisplayName(User user) {
        this.upsert(user);
    }

    @Override
    public void changePreferredCurrency(
            String username,
            String preferredCurrencyCode) {
        final String sql = "UPDATE users SET "
                + PREFERRED_CURRENCY_COLUMN
                + EQUALS_PLACEHOLDER
                + " WHERE "
                + USERNAME_COLUMN
                + EQUALS_PLACEHOLDER;

        try (PreparedStatement statement =
                     this.connection.prepareStatement(sql)) {
            statement.setString(1, preferredCurrencyCode);
            statement.setString(2, username);
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new UserDatabaseException(
                    "Could not change the preferred currency.",
                    exception);
        }
    }

    @Override
    public String getDisplayName(String username) {
        return this.readColumn(username, DISPLAY_NAME_COLUMN);
    }

    private void upsert(User user) {
       
        final String sql = "INSERT OR REPLACE INTO users ("
                + USERNAME_COLUMN + COLUMN_SEPARATOR
                + DISPLAY_NAME_COLUMN + COLUMN_SEPARATOR
                + EMAIL_COLUMN + COLUMN_SEPARATOR
                + PASSWORD_COLUMN + COLUMN_SEPARATOR
                + PREFERRED_CURRENCY_COLUMN
                + ") VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getDisplayName());
            statement.setString(EMAIL_PARAMETER_INDEX, user.getEmail());
            statement.setString(PASSWORD_PARAMETER_INDEX, user.getPassword());
            statement.setString(PREFERRED_CURRENCY_PARAMETER_INDEX,
                    user.getPreferredCurrency());

            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new UserDatabaseException(
                    "Could not save the user.",
                    exception);
        }
    }

    private String readColumn(String username, String column) {
        final String sql = "SELECT "
                + column
                + " FROM users WHERE "
                + USERNAME_COLUMN + EQUALS_PLACEHOLDER;
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                String value = null;
                if (resultSet.next()) {
                    value = resultSet.getString(column);
                }
                return value;
            }
        }
        catch (SQLException exception) {
            throw new UserDatabaseException("Could not read the user record.", exception);
        }
    }

    /**
     * Thrown when the SQLite user store cannot be accessed.
     */
    public static final class UserDatabaseException extends RuntimeException {
        UserDatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
