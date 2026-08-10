package data_access;

import use_case.add_activity.AddActivityDataAccessInterface;
import use_case.add_expense.AddExpenseDataAccessInterface;
import use_case.add_guests.AddGuestDataAccessInterface;
import use_case.create_event.CreateEventDataAccessInterface;
import use_case.delete_event.DeleteEventDataAccessInterface;
import use_case.edit_activity.EditActivityDataAccessInterface;
import use_case.edit_event.EditEventDataAccessInterface;
import use_case.edit_expense.EditExpenseDataAccessInterface;
import use_case.event_photo.EventPhotoDataAccessInterface;
import use_case.login.LoginEventDataAccessInterface;
import use_case.pay_expense.PayExpenseDataAccessInterface;
import use_case.remove_activity.RemoveActivityDataAccessInterface;
import use_case.remove_expense.RemoveExpenseDataAccessInterface;
import use_case.remove_guest.RemoveGuestDataAccessInterface;
import use_case.select_event.SelectEventDataAccessInterface;
import use_case.who_owes_what.WhoOwesWhatDataAccessInterface;

/**
 * Every event operation the use cases need from a single stored collection of events.
 *
 * <p>The use cases each depend on their own narrow interface; this gathers those interfaces
 * so that a store which serves all of them can say so in one place.
 */
public interface EventDataAccess extends
        WhoOwesWhatDataAccessInterface,
        AddExpenseDataAccessInterface,
        EditExpenseDataAccessInterface,
        RemoveExpenseDataAccessInterface,
        PayExpenseDataAccessInterface,
        CreateEventDataAccessInterface,
        AddGuestDataAccessInterface,
        RemoveGuestDataAccessInterface,
        EditEventDataAccessInterface,
        DeleteEventDataAccessInterface,
        SelectEventDataAccessInterface,
        AddActivityDataAccessInterface,
        EditActivityDataAccessInterface,
        RemoveActivityDataAccessInterface,
        LoginEventDataAccessInterface,
        EventPhotoDataAccessInterface {

    /**
     * Returns the stored photo for an event.
     *
     * <p>Redeclared because the select-event port supplies a default and the event-photo port
     * declares it abstract, so this interface has to say which one wins.
     *
     * @param eventId the event identifier
     * @return the photo bytes, or null when the event has none
     */
    @Override
    byte[] getEventPhoto(int eventId);
}
