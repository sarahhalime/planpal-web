package use_case.change_password;

public class ChangePasswordOutputData {

    private final String username;
    private final boolean isUseCaseFailed;

    public ChangePasswordOutputData(String username, boolean useCaseFailed) {
        this.username = username;
        this.isUseCaseFailed = useCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public boolean isUseCaseFailed() {
        return isUseCaseFailed;
    }
}
