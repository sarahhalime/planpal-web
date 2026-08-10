package web;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import data_access.CurrencyApiDataAccessObject;
import data_access.SqliteSocialDataAccessObject;
import data_access.SqliteUserDataAccessObject;
import entity.CommonUserFactory;
import use_case.change_display_name.ChangeDisplayNameInputData;
import use_case.change_display_name.ChangeDisplayNameInteractor;
import use_case.change_display_name.ChangeDisplayNameOutputBoundary;
import use_case.change_display_name.ChangeDisplayNameOutputData;
import use_case.change_password.ChangePasswordInputData;
import use_case.change_password.ChangePasswordInteractor;
import use_case.change_password.ChangePasswordOutputBoundary;
import use_case.change_password.ChangePasswordOutputData;
import use_case.change_preferred_currency.ChangePreferredCurrencyInputData;
import use_case.change_preferred_currency.ChangePreferredCurrencyInteractor;
import use_case.change_preferred_currency.ChangePreferredCurrencyOutputBoundary;
import use_case.change_preferred_currency.ChangePreferredCurrencyOutputData;
import use_case.change_username.ChangeUsernameInputData;
import use_case.change_username.ChangeUsernameInteractor;
import use_case.change_username.ChangeUsernameOutputBoundary;
import use_case.change_username.ChangeUsernameOutputData;
import use_case.edit_home_address.EditHomeAddressInputData;
import use_case.edit_home_address.EditHomeAddressInteractor;
import use_case.edit_home_address.EditHomeAddressOutputBoundary;
import use_case.edit_home_address.EditHomeAddressOutputData;
import use_case.follow_user.FollowUserInputData;
import use_case.follow_user.FollowUserInteractor;
import use_case.follow_user.FollowUserOutputBoundary;
import use_case.follow_user.FollowUserOutputData;
import use_case.get_supported_currencies.CurrencyOptionData;
import use_case.get_supported_currencies.GetSupportedCurrenciesInteractor;
import use_case.get_supported_currencies.GetSupportedCurrenciesOutputBoundary;
import use_case.get_supported_currencies.GetSupportedCurrenciesOutputData;
import use_case.list_follows.ListFollowsInputData;
import use_case.list_follows.ListFollowsInteractor;
import use_case.list_follows.ListFollowsOutputBoundary;
import use_case.list_follows.ListFollowsOutputData;
import use_case.search_users.SearchUsersInputData;
import use_case.search_users.SearchUsersInteractor;
import use_case.search_users.SearchUsersOutputBoundary;
import use_case.search_users.SearchUsersOutputData;
import use_case.search_users.SearchedUser;
import use_case.signup.SignupInputData;
import use_case.signup.SignupInteractor;
import use_case.signup.SignupOutputBoundary;
import use_case.signup.SignupOutputData;
import use_case.update_profile.UpdateProfileInputData;
import use_case.update_profile.UpdateProfileInteractor;
import use_case.update_profile.UpdateProfileOutputBoundary;
import use_case.update_profile.UpdateProfileOutputData;
import use_case.view_profile.ViewProfileInputData;
import use_case.view_profile.ViewProfileInteractor;
import use_case.view_profile.ViewProfileOutputBoundary;
import use_case.view_profile.ViewProfileOutputData;

/**
 * Handles everything about the person using PlanPal rather than a particular trip:
 * signing up, the profile, the social graph, and account settings.
 */
final class AccountApiHandler implements HttpHandler {

    private final SqliteUserDataAccessObject userDataAccess;
    private final SqliteSocialDataAccessObject socialDataAccess;
    private final CurrencyApiDataAccessObject currencyDataAccess;
    private final CommonUserFactory userFactory = new CommonUserFactory();

    AccountApiHandler(SqliteUserDataAccessObject userDataAccess,
                      SqliteSocialDataAccessObject socialDataAccess,
                      CurrencyApiDataAccessObject currencyDataAccess) {
        this.userDataAccess = userDataAccess;
        this.socialDataAccess = socialDataAccess;
        this.currencyDataAccess = currencyDataAccess;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();

        try {
            this.route(path, exchange);
        }
        catch (final RuntimeException exception) {
            Json.fail(exchange, Json.SERVER_ERROR, String.valueOf(exception.getMessage()));
        }
    }

    private void route(String path, HttpExchange exchange) throws IOException {
        switch (path) {
            case "/api/signup" -> this.signup(exchange);
            case "/api/profile" -> this.profile(exchange);
            case "/api/profile/update" -> this.updateProfile(exchange);
            case "/api/follows" -> this.follows(exchange);
            case "/api/follow" -> this.follow(exchange);
            case "/api/users/search" -> this.searchUsers(exchange);
            case "/api/currencies" -> this.currencies(exchange);
            case "/api/account/currency" -> this.preferredCurrency(exchange);
            case "/api/account/password" -> this.changePassword(exchange);
            case "/api/account/username" -> this.changeUsername(exchange);
            case "/api/account/displayname" -> this.changeDisplayName(exchange);
            case "/api/account/address" -> this.changeHomeAddress(exchange);
            default -> Json.fail(exchange, Json.NOT_FOUND, "Unknown endpoint");
        }
    }

    private void signup(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<SignupOutputData> captured = new Result<>();

        new SignupInteractor(this.userDataAccess, new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }

            @Override
            public void switchToLoginView() {
                // The web client decides where to go next.
            }
        }, this.userFactory).execute(new SignupInputData(
                body.optString("username", "").trim(),
                body.optString("displayName", "").trim(),
                body.optString("email", "").trim(),
                body.optString("password", "").toCharArray()));

        if (captured.error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, captured.error);
        }
        else {
            Json.ok(exchange, new JSONObject().put("username", captured.value.getUsername()));
        }
    }

    private void profile(HttpExchange exchange) throws IOException {
        final String target = Json.query(exchange, "username");
        final String viewer = Json.query(exchange, "viewer");
        final Result<ViewProfileOutputData> captured = new Result<>();

        new ViewProfileInteractor(this.socialDataAccess, this.socialDataAccess,
                new ViewProfileOutputBoundary() {
                    @Override
                    public void prepareSuccessView(ViewProfileOutputData outputData) {
                        captured.value = outputData;
                    }

                }).execute(new ViewProfileInputData(target, viewer.isEmpty() ? target : viewer));

        if (captured.error != null) {
            Json.fail(exchange, Json.NOT_FOUND, captured.error);
            return;
        }

        final ViewProfileOutputData data = captured.value;
        Json.ok(exchange, new JSONObject()
                .put("username", data.getUsername())
                .put("bio", data.getBio() == null ? "" : data.getBio())
                .put("followers", data.getFollowerCount())
                .put("following", data.getFollowingCount())
                .put("isFollowing", data.isFollowing())
                .put("picture", encodePicture(data.getProfilePicture())));
    }

    private void updateProfile(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<UpdateProfileOutputData> captured = new Result<>();
        final String picture = body.optString("picture", "");

        new UpdateProfileInteractor(this.socialDataAccess, new UpdateProfileOutputBoundary() {
            @Override
            public void prepareSuccessView(UpdateProfileOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }).execute(new UpdateProfileInputData(
                body.optString("username", ""),
                body.optString("bio", ""),
                picture.isEmpty() ? null : Base64.getDecoder().decode(picture)));

        Json.result(exchange, captured.error, new JSONObject().put("ok", true));
    }

    private void follows(HttpExchange exchange) throws IOException {
        final String username = Json.query(exchange, "username");
        final boolean followers = "true".equals(Json.query(exchange, "followers"));
        final Result<ListFollowsOutputData> captured = new Result<>();

        new ListFollowsInteractor(this.socialDataAccess, new ListFollowsOutputBoundary() {
            @Override
            public void prepareSuccessView(ListFollowsOutputData outputData) {
                captured.value = outputData;
            }

        }).execute(new ListFollowsInputData(username, followers));

        if (captured.error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, captured.error);
            return;
        }

        final JSONArray people = new JSONArray();
        for (final String name : captured.value.getUsernames()) {
            people.put(new JSONObject()
                    .put("username", name)
                    .put("picture", encodePicture(this.socialDataAccess.getProfilePicture(name))));
        }
        Json.ok(exchange, new JSONObject().put("people", people));
    }

    private void follow(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<FollowUserOutputData> captured = new Result<>();

        new FollowUserInteractor(this.socialDataAccess, new FollowUserOutputBoundary() {
            @Override
            public void prepareSuccessView(FollowUserOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                captured.error = errorMessage;
            }
        }).execute(new FollowUserInputData(
                body.optString("currentUsername", ""),
                body.optString("targetUsername", ""),
                body.optBoolean("follow", true)));

        Json.result(exchange, captured.error, new JSONObject().put("ok", true));
    }

    private void searchUsers(HttpExchange exchange) throws IOException {
        final Result<SearchUsersOutputData> captured = new Result<>();

        new SearchUsersInteractor(this.userDataAccess, this.socialDataAccess,
                new SearchUsersOutputBoundary() {
                    @Override
                    public void prepareSuccessView(SearchUsersOutputData outputData) {
                        captured.value = outputData;
                    }

                }).execute(new SearchUsersInputData(
                        Json.query(exchange, "q"), Json.query(exchange, "username")));

        if (captured.error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, captured.error);
            return;
        }

        final JSONArray results = new JSONArray();
        for (final SearchedUser user : captured.value.getResults()) {
            results.put(new JSONObject()
                    .put("username", user.getUsername())
                    .put("isFollowing", user.isFollowing()));
        }
        Json.ok(exchange, new JSONObject().put("users", results));
    }

    private void currencies(HttpExchange exchange) throws IOException {
        final Result<GetSupportedCurrenciesOutputData> captured = new Result<>();

        new GetSupportedCurrenciesInteractor(this.currencyDataAccess,
                new GetSupportedCurrenciesOutputBoundary() {
                    @Override
                    public void prepareSuccessView(GetSupportedCurrenciesOutputData outputData) {
                        captured.value = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }
                }).execute();

        if (captured.error != null) {
            Json.fail(exchange, Json.BAD_REQUEST, captured.error);
            return;
        }

        final JSONArray options = new JSONArray();
        final List<CurrencyOptionData> currencies = captured.value.getCurrencies();
        for (final CurrencyOptionData option : currencies) {
            options.put(new JSONObject()
                    .put("code", option.getCode())
                    .put("name", option.getName()));
        }
        Json.ok(exchange, new JSONObject().put("currencies", options));
    }

    private void preferredCurrency(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<ChangePreferredCurrencyOutputData> captured = new Result<>();

        new ChangePreferredCurrencyInteractor(this.userDataAccess, this.currencyDataAccess,
                new ChangePreferredCurrencyOutputBoundary() {
                    @Override
                    public void prepareSuccessView(ChangePreferredCurrencyOutputData outputData) {
                        captured.value = outputData;
                    }

                    @Override
                    public void prepareFailView(String errorMessage) {
                        captured.error = errorMessage;
                    }
                }).execute(new ChangePreferredCurrencyInputData(
                        body.optString("username", ""), body.optString("currency", "")));

        Json.result(exchange, captured.error, new JSONObject()
                .put("currency", captured.value == null ? "" : captured.value.getPreferredCurrencyCode()));
    }

    private void changePassword(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<ChangePasswordOutputData> captured = new Result<>();

        new ChangePasswordInteractor(this.userDataAccess, new ChangePasswordOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangePasswordOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailureView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.userFactory).execute(new ChangePasswordInputData(
                body.optString("username", ""),
                body.optString("oldPassword", "").toCharArray(),
                body.optString("newPassword", "").toCharArray(),
                body.optString("confirmPassword", "").toCharArray()));

        Json.result(exchange, captured.error, new JSONObject().put("ok", true));
    }

    private void changeUsername(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<ChangeUsernameOutputData> captured = new Result<>();

        new ChangeUsernameInteractor(this.userDataAccess, new ChangeUsernameOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangeUsernameOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailureView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.userFactory).execute(new ChangeUsernameInputData(
                body.optString("oldUsername", ""), body.optString("newUsername", "")));

        Json.result(exchange, captured.error, new JSONObject()
                .put("username", body.optString("newUsername", "")));
    }

    private void changeDisplayName(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<ChangeDisplayNameOutputData> captured = new Result<>();

        new ChangeDisplayNameInteractor(this.userDataAccess, new ChangeDisplayNameOutputBoundary() {
            @Override
            public void prepareSuccessView(ChangeDisplayNameOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailureView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.userFactory).execute(new ChangeDisplayNameInputData(
                body.optString("username", ""), body.optString("displayName", "")));

        Json.result(exchange, captured.error, new JSONObject()
                .put("displayName", body.optString("displayName", "")));
    }

    private void changeHomeAddress(HttpExchange exchange) throws IOException {
        final JSONObject body = Json.read(exchange);
        final Result<EditHomeAddressOutputData> captured = new Result<>();

        new EditHomeAddressInteractor(this.userDataAccess, new EditHomeAddressOutputBoundary() {
            @Override
            public void prepareSuccessView(EditHomeAddressOutputData outputData) {
                captured.value = outputData;
            }

            @Override
            public void prepareFailureView(String errorMessage) {
                captured.error = errorMessage;
            }
        }, this.userFactory).execute(new EditHomeAddressInputData(
                body.optString("username", ""), body.optString("address", "")));

        Json.result(exchange, captured.error, new JSONObject()
                .put("address", body.optString("address", "")));
    }

    private static String encodePicture(byte[] picture) {
        final String encoded;

        if (picture == null || picture.length == 0) {
            encoded = "";
        }
        else {
            encoded = Base64.getEncoder().encodeToString(picture);
        }
        return encoded;
    }

    /** Captures whatever an interactor reports. */
    private static final class Result<T> {
        private T value;
        private String error;
    }
}
