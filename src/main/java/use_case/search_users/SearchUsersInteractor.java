package use_case.search_users;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import use_case.follow_user.FollowUserDataAccessInterface;

/**
 * The interactor for the search users use case.
 */
public class SearchUsersInteractor implements SearchUsersInputBoundary {

    private final SearchUsersDataAccessInterface userDataAccess;
    private final FollowUserDataAccessInterface followDataAccess;
    private final SearchUsersOutputBoundary presenter;

    public SearchUsersInteractor(SearchUsersDataAccessInterface userDataAccess,
                                 FollowUserDataAccessInterface followDataAccess,
                                 SearchUsersOutputBoundary presenter) {
        this.userDataAccess = userDataAccess;
        this.followDataAccess = followDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SearchUsersInputData searchUsersInputData) {
        final String query = searchUsersInputData.getQuery();
        final String currentUsername = searchUsersInputData.getCurrentUsername();
        final List<SearchedUser> results = new ArrayList<>();

        final boolean browseAll = query == null || query.isBlank();
        final String needle;
        if (browseAll) {
            needle = "";
        }
        else {
            needle = query.trim().toLowerCase(Locale.ROOT);
        }
        for (final String username : userDataAccess.getAllUsernames()) {
            if (!username.equals(currentUsername)
                    && (browseAll || username.toLowerCase(Locale.ROOT).contains(needle))) {
                results.add(new SearchedUser(username,
                        followDataAccess.isFollowing(currentUsername, username)));
            }
        }

        presenter.prepareSuccessView(new SearchUsersOutputData(query, results));
    }
}
