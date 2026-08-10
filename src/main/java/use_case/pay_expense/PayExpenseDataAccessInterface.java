package use_case.pay_expense;

import entity.Event;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

/**
 * Data access required to toggle an expense's settlement status.
 */
public interface PayExpenseDataAccessInterface {

    /**
     * Gets the event.
     * @param eventId The event id.
     * @return The Event entity object.
     * @throws WhoOwesWhatDataAccessException exception.
     */
    Event getEvent(int eventId) throws WhoOwesWhatDataAccessException;

    /**
     * Saves the event.
     * @param event The event to save.
     */
    void saveEvent(Event event);
}
