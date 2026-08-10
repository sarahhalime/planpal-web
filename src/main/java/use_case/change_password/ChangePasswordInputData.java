package use_case.change_password;

public class ChangePasswordInputData {

    private final String username;
    private final char[] oldPassword;
    private final char[] newPassword;
    private final char[] confirmPassword;

    public ChangePasswordInputData(String username, char[] oldPassword, char[] newPassword, char[] confirmPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public char[] getOldPassword() {
        return oldPassword;
    }

    public char[] getNewPassword() {
        return newPassword;
    }

    public char[] getConfirmPassword() {
        return confirmPassword;
    }
}
