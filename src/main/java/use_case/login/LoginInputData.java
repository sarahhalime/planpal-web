package use_case.login;

public class LoginInputData {

    private final String username;
    private final char[] password;

    // LoginInputData Constructor using what UI collects
    public LoginInputData(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }
}
