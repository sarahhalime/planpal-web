package use_case.view_profile;

/**
 * The output data for the view profile use case.
 */
public class ViewProfileOutputData {

    private final String username;
    private final String bio;
    private final int followerCount;
    private final int followingCount;
    private final boolean following;
    private final boolean ownProfile;
    private final byte[] profilePicture;

    public ViewProfileOutputData(String username, String bio, int followerCount, int followingCount,
                                 boolean following, boolean ownProfile, byte[] profilePicture) {
        this.username = username;
        this.bio = bio;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.following = following;
        this.ownProfile = ownProfile;
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public boolean isOwnProfile() {
        return ownProfile;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }
}
