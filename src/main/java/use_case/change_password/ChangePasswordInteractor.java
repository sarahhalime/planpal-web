package use_case.change_password;

import java.util.Arrays;

import data_access.PasswordHasher;
import entity.User;
import entity.UserFactory;
import use_case.signup.PasswordPolicy;

public class ChangePasswordInteractor implements ChangePasswordInputBoundary {
    private final ChangePasswordDataAccessInterface changePasswordDataAccess;
    private final ChangePasswordOutputBoundary changePasswordPresenter;
    private final UserFactory userFactory;

    public ChangePasswordInteractor(ChangePasswordDataAccessInterface changePasswordDataAccess,
                                    ChangePasswordOutputBoundary changePasswordOutputBoundary,
                                    UserFactory userFactory) {
        this.changePasswordDataAccess = changePasswordDataAccess;
        this.changePasswordPresenter = changePasswordOutputBoundary;
        this.userFactory = userFactory;
    }

    @Override
    public void execute(ChangePasswordInputData changePasswordInputData) {
        if (changePasswordInputData.getNewPassword() == null || changePasswordInputData.getNewPassword().length == 0) {
            changePasswordPresenter.prepareFailureView("Your new password cannot be empty.");
        }
        else if (PasswordPolicy.getValidationError(changePasswordInputData.getNewPassword()) != null) {
            changePasswordPresenter.prepareFailureView(
                    PasswordPolicy.getValidationError(changePasswordInputData.getNewPassword())
            );
        }
        else if (Arrays.equals(changePasswordInputData.getOldPassword(), changePasswordInputData.getNewPassword())) {
            changePasswordPresenter.prepareFailureView("Your new password is the same as your old password.");
        }
        else if (!PasswordHasher.matches(changePasswordInputData.getOldPassword(),
                changePasswordDataAccess.getPassword(changePasswordInputData.getUsername()))) {
            changePasswordPresenter.prepareFailureView("Incorrect old password.");
        }
        else if (!Arrays.equals(changePasswordInputData.getNewPassword(),
                changePasswordInputData.getConfirmPassword())) {
            changePasswordPresenter.prepareFailureView("Passwords do not match.");
        }
        else {
            final User currentUser = changePasswordDataAccess.getUser(
                    changePasswordInputData.getUsername());

            final User user = userFactory.create(
                    currentUser.getUsername(),
                    currentUser.getDisplayName(),
                    currentUser.getEmail(),
                    PasswordHasher.hash(changePasswordInputData.getNewPassword()),
                    "",
                    currentUser.getPreferredCurrency()
            );

            PasswordHasher.hash(changePasswordInputData.getConfirmPassword());
            changePasswordDataAccess.changePassword(user);
            final ChangePasswordOutputData changePasswordOutputData =
                    new ChangePasswordOutputData(changePasswordInputData.getUsername(), false);
            changePasswordPresenter.prepareSuccessView(changePasswordOutputData);
        }
    }
}
