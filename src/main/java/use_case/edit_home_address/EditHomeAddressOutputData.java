package use_case.edit_home_address;

public class EditHomeAddressOutputData {
    private final String username;
    private final String homeAddress;
    private final boolean useCaseFailed;

    public EditHomeAddressOutputData(String username, String homeAddress, boolean useCaseFailed) {
        this.username = username;
        this.homeAddress = homeAddress;
        this.useCaseFailed = useCaseFailed;
    }

    public String getUsername() {
        return username;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
