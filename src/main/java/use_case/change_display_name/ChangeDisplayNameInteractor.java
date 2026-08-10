package use_case.change_display_name;

import entity.User;
import entity.UserFactory;

public class ChangeDisplayNameInteractor implements ChangeDisplayNameInputBoundary {
    private final ChangeDisplayNameDataAccessInterface changeDisplayNameDataAccess;
    private final ChangeDisplayNameOutputBoundary changeDisplayNamePresenter;
    private final UserFactory userFactory;

    public ChangeDisplayNameInteractor(ChangeDisplayNameDataAccessInterface changeDisplayNameDataAccess,
                                       ChangeDisplayNameOutputBoundary changeDisplayNameOutputBoundary,
                                       UserFactory userFactory) {
        this.changeDisplayNameDataAccess = changeDisplayNameDataAccess;
        this.changeDisplayNamePresenter = changeDisplayNameOutputBoundary;
        this.userFactory = userFactory;
    }

    @Override
    public void execute(ChangeDisplayNameInputData changeDisplayNameInputData) {
        if (changeDisplayNameInputData.getDisplayName() == null
                || changeDisplayNameInputData.getDisplayName().isEmpty()) {
            changeDisplayNamePresenter.prepareFailureView("Your display name cannot be empty.");
        }
        else if (changeDisplayNameInputData.getDisplayName()
                .equals(changeDisplayNameDataAccess.getDisplayName(changeDisplayNameInputData.getUsername()))) {
            changeDisplayNamePresenter.prepareFailureView("Your display name is the same as before.");
        }
        else {
            final User currentUser = changeDisplayNameDataAccess.getUser(
                    changeDisplayNameInputData.getUsername());

            final User user = userFactory.create(
                    currentUser.getUsername(),
                    changeDisplayNameInputData.getDisplayName(),
                    currentUser.getEmail(),
                    currentUser.getPassword(),
                    "",
                    currentUser.getPreferredCurrency());
            changeDisplayNameDataAccess.changeDisplayName(user);
            final ChangeDisplayNameOutputData changeDisplayNameOutputData =
                    new ChangeDisplayNameOutputData(changeDisplayNameInputData.getUsername(),
                            changeDisplayNameInputData.getDisplayName(), false);
            changeDisplayNamePresenter.prepareSuccessView(changeDisplayNameOutputData);
        }
    }
}
