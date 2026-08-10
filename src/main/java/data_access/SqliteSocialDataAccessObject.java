package data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import use_case.change_username.RenameUserDataAccessInterface;
import use_case.follow_user.FollowUserDataAccessInterface;
import use_case.list_follows.ListFollowsDataAccessInterface;
import use_case.update_profile.UpdateProfileDataAccessInterface;
import use_case.view_profile.ViewProfileDataAccessInterface;

/**
 * Stores the follow graph and profile details (bio, picture) in a local SQLite database.
 */
public class SqliteSocialDataAccessObject implements FollowUserDataAccessInterface,
        ViewProfileDataAccessInterface,
        UpdateProfileDataAccessInterface,
        ListFollowsDataAccessInterface,
        RenameUserDataAccessInterface,
        AutoCloseable {

    private final Connection connection;

    public SqliteSocialDataAccessObject(String fileName) {
        final String path = JsonFileUtil.resolvePath(fileName);
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            this.createTables();
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not open the social database.", exception);
        }
    }

    private void createTables() throws SQLException {
        final String followsSql = "CREATE TABLE IF NOT EXISTS follows ("
                + "follower TEXT NOT NULL, "
                + "followee TEXT NOT NULL, "
                + "PRIMARY KEY (follower, followee))";
        final String profilesSql = "CREATE TABLE IF NOT EXISTS profiles ("
                + "username TEXT PRIMARY KEY, "
                + "bio TEXT, "
                + "picture BLOB)";
        try (PreparedStatement follows = this.connection.prepareStatement(followsSql);
             PreparedStatement profiles = this.connection.prepareStatement(profilesSql)) {
            follows.execute();
            profiles.execute();
        }
    }

    @Override
    public void renameUser(String oldUsername, String newUsername) {
        final String profileSql =
                "UPDATE OR REPLACE profiles SET username = ? WHERE username = ?";
        final String followerSql =
                "UPDATE OR REPLACE follows SET follower = ? WHERE follower = ?";
        final String followeeSql =
                "UPDATE OR REPLACE follows SET followee = ? WHERE followee = ?";

        try (PreparedStatement profiles = this.connection.prepareStatement(profileSql);
             PreparedStatement followers = this.connection.prepareStatement(followerSql);
             PreparedStatement followees = this.connection.prepareStatement(followeeSql)) {

            for (final PreparedStatement statement
                    : List.of(profiles, followers, followees)) {
                statement.setString(1, newUsername);
                statement.setString(2, oldUsername);
                statement.executeUpdate();
            }
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException(
                    "Could not move the social data onto the new username.", exception);
        }
    }

    @Override
    public void follow(String follower, String followee) {
        final String sql = "INSERT OR IGNORE INTO follows (follower, followee) VALUES (?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, follower);
            statement.setString(2, followee);
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not follow the user.", exception);
        }
    }

    @Override
    public void unfollow(String follower, String followee) {
        final String sql = "DELETE FROM follows WHERE follower = ? AND followee = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, follower);
            statement.setString(2, followee);
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not unfollow the user.", exception);
        }
    }

    @Override
    public boolean isFollowing(String follower, String followee) {
        final String sql = "SELECT 1 FROM follows WHERE follower = ? AND followee = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, follower);
            statement.setString(2, followee);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not read the follow relationship.", exception);
        }
    }

    @Override
    public int countFollowers(String username) {
        return this.count("SELECT COUNT(*) FROM follows WHERE followee = ?", username);
    }

    @Override
    public int countFollowing(String username) {
        return this.count("SELECT COUNT(*) FROM follows WHERE follower = ?", username);
    }

    @Override
    public List<String> getFollowers(String username) {
        return this.usernames(
                "SELECT follower FROM follows WHERE followee = ? ORDER BY follower", username);
    }

    @Override
    public List<String> getFollowing(String username) {
        return this.usernames(
                "SELECT followee FROM follows WHERE follower = ? ORDER BY followee", username);
    }

    private List<String> usernames(String sql, String username) {
        final List<String> usernames = new ArrayList<>();
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    usernames.add(resultSet.getString(1));
                }
            }
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not read the follow list.", exception);
        }
        return usernames;
    }

    private int count(String sql, String username) {
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                int total = 0;
                if (resultSet.next()) {
                    total = resultSet.getInt(1);
                }
                return total;
            }
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not count the follow relationships.", exception);
        }
    }

    @Override
    public String getBio(String username) {
        final String sql = "SELECT bio FROM profiles WHERE username = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                String bio = "";
                if (resultSet.next() && resultSet.getString("bio") != null) {
                    bio = resultSet.getString("bio");
                }
                return bio;
            }
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not read the bio.", exception);
        }
    }

    @Override
    public byte[] getProfilePicture(String username) {
        final String sql = "SELECT picture FROM profiles WHERE username = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                byte[] picture = null;
                if (resultSet.next()) {
                    picture = resultSet.getBytes("picture");
                }
                return picture;
            }
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not read the profile picture.", exception);
        }
    }

    @Override
    public void setBio(String username, String bio) {
        final String sql = "INSERT INTO profiles (username, bio) VALUES (?, ?) "
                + "ON CONFLICT(username) DO UPDATE SET bio = excluded.bio";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, bio);
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not save the bio.", exception);
        }
    }

    @Override
    public void setProfilePicture(String username, byte[] profilePicture) {
        final String sql = "INSERT INTO profiles (username, picture) VALUES (?, ?) "
                + "ON CONFLICT(username) DO UPDATE SET picture = excluded.picture";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setBytes(2, profilePicture);
            statement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not save the profile picture.", exception);
        }
    }

    /**
     * Closes the underlying SQLite connection.
     */
    @Override
    public void close() {
        try {
            this.connection.close();
        }
        catch (SQLException exception) {
            throw new SocialDatabaseException("Could not close the social database.", exception);
        }
    }

    /**
     * Thrown when the SQLite social store cannot be accessed.
     */
    public static final class SocialDatabaseException extends RuntimeException {
        SocialDatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
