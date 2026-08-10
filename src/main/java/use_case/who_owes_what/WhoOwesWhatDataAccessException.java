package use_case.who_owes_what;

public class WhoOwesWhatDataAccessException extends Exception {

    public WhoOwesWhatDataAccessException(String message) {

        super(message);

    }

    public WhoOwesWhatDataAccessException(String message, Throwable cause) {

        super(message, cause);

    }

}
