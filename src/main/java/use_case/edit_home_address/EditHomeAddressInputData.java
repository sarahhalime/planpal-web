package use_case.edit_home_address;

public class EditHomeAddressInputData {
    private final String username;
    private final String homeAddress;

    public EditHomeAddressInputData(String username, String homeAddress) {
        this.username = username;
        this.homeAddress = homeAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getHomeAddress() {
        return homeAddress;
    }
}
