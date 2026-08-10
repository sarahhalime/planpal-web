package use_case.change_username;

import java.util.List;

import entity.User;
import entity.UserFactory;

public class ChangeUsernameInteractor implements ChangeUsernameInputBoundary {
    private final ChangeUsernameDataAccessInterface changeUsernameDataAccess;
    private final ChangeUsernameOutputBoundary changeUsernamePresenter;
    private final UserFactory userFactory;
    private final List<RenameUserDataAccessInterface> renameTargets;

    public ChangeUsernameInteractor(ChangeUsernameDataAccessInterface changeUsernameDataAccess,
                                    ChangeUsernameOutputBoundary changeUsernameOutputBoundary,
                                    UserFactory userFactory) {
        this(changeUsernameDataAccess, changeUsernameOutputBoundary, userFactory, List.of());
    }

    /**
     * Creates a change-username interactor that carries the rename into the other stores.
     *
     * @param changeUsernameDataAccess the user store
     * @param changeUsernameOutputBoundary the presenter
     * @param userFactory builds the renamed user
     * @param renameTargets the stores that also file data under the username
     */
    public ChangeUsernameInteractor(ChangeUsernameDataAccessInterface changeUsernameDataAccess,
                                    ChangeUsernameOutputBoundary changeUsernameOutputBoundary,
                                    UserFactory userFactory,
                                    List<RenameUserDataAccessInterface> renameTargets) {
        this.changeUsernameDataAccess = changeUsernameDataAccess;
        this.changeUsernamePresenter = changeUsernameOutputBoundary;
        this.userFactory = userFactory;
        this.renameTargets = List.copyOf(renameTargets);
    }

    @Override
    public void execute(ChangeUsernameInputData changeUsernameInputData) {
        if (changeUsernameInputData.getNewUsername() == null
                || changeUsernameInputData.getNewUsername().isEmpty()) {
            changeUsernamePresenter.prepareFailureView("Your new Username cannot be empty.");
        }
        else if (changeUsernameInputData.getNewUsername()
                .equals(changeUsernameInputData.getOldUsername())) {
            changeUsernamePresenter.prepareFailureView("Your new Username is the same as before.");
        }
        else if (changeUsernameDataAccess.existsByUsername(
                changeUsernameInputData.getNewUsername())) {
            changeUsernamePresenter.prepareFailureView("Username is already in use.");
        }
        else {
            final User currentUser = changeUsernameDataAccess.getUser(
                    changeUsernameInputData.getOldUsername());
            final User user = userFactory.create(changeUsernameInputData.getNewUsername(),
                    currentUser.getDisplayName(), currentUser.getEmail(),
                    currentUser.getPassword(), "", currentUser.getPreferredCurrency());
            changeUsernameDataAccess.changeUsername(changeUsernameInputData.getOldUsername(), user);
            this.carryRenameThroughStores(
                    changeUsernameInputData.getOldUsername(),
                    changeUsernameInputData.getNewUsername()
            );
            final ChangeUsernameOutputData changeUsernameOutputData =
                    new ChangeUsernameOutputData(changeUsernameInputData.getNewUsername(), false);
            changeUsernamePresenter.prepareSuccessView(changeUsernameOutputData);
        }
    }

    /**
     * Moves everything filed under the old username onto the new one.
     *
     * @param oldUsername the username being replaced
     * @param newUsername the replacement username
     */
    private void carryRenameThroughStores(String oldUsername, String newUsername) {
        for (final RenameUserDataAccessInterface target : this.renameTargets) {
            target.renameUser(oldUsername, newUsername);
        }
    }
}
