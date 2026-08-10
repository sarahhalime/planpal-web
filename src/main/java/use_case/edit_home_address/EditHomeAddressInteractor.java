package use_case.edit_home_address;

import entity.User;
import entity.UserFactory;

public class EditHomeAddressInteractor implements EditHomeAddressInputBoundary {

    private static final String BLANK_ADDRESS_MESSAGE = "Home address cannot be empty.";
    private static final String USER_NOT_FOUND_MESSAGE = "No account was found for that user.";

    private final EditHomeAddressDataAccessInterface editHomeAddressDataAccess;
    private final EditHomeAddressOutputBoundary editHomeAddressPresenter;
    private final UserFactory userFactory;

    public EditHomeAddressInteractor(EditHomeAddressDataAccessInterface editHomeAddressDataAccess,
                                     EditHomeAddressOutputBoundary presenter,
                                     UserFactory userFactory) {
        this.editHomeAddressDataAccess = editHomeAddressDataAccess;
        this.editHomeAddressPresenter = presenter;
        this.userFactory = userFactory;
    }

    @Override
    public void execute(EditHomeAddressInputData editHomeAddressInputData) {
        final String homeAddress = editHomeAddressInputData.getHomeAddress();

        if (homeAddress == null || homeAddress.isBlank()) {
            this.editHomeAddressPresenter.prepareFailureView(BLANK_ADDRESS_MESSAGE);
        }
        else {
            final User existingUser = this.editHomeAddressDataAccess.getUser(
                    editHomeAddressInputData.getUsername());

            if (existingUser == null) {
                this.editHomeAddressPresenter.prepareFailureView(USER_NOT_FOUND_MESSAGE);
            }
            else {
                this.updateHomeAddress(existingUser, homeAddress.trim());
            }
        }
    }

    private void updateHomeAddress(User existingUser, String trimmedHomeAddress) {
        final User updatedUser = this.userFactory.create(
                existingUser.getUsername(),
                existingUser.getDisplayName(),
                existingUser.getEmail(),
                existingUser.getPassword(),
                trimmedHomeAddress,
                existingUser.getPreferredCurrency()
        );

        this.editHomeAddressDataAccess.changeHomeAddress(updatedUser);
        this.editHomeAddressPresenter.prepareSuccessView(
                new EditHomeAddressOutputData(existingUser.getUsername(), trimmedHomeAddress, false));
    }
}
