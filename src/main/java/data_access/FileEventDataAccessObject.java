package data_access;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Activity;
import entity.ActivityFactory;
import entity.CommonActivityFactory;
import entity.Event;
import entity.EventDetails;
import entity.EventFactory;
import entity.EventSchedule;
import entity.Expense;
import entity.ExpenseFactory;
import use_case.change_username.RenameUserDataAccessInterface;
import use_case.event_photo.EventPhotoDataAccessException;
import use_case.who_owes_what.WhoOwesWhatDataAccessException;

public class FileEventDataAccessObject implements EventDataAccess, RenameUserDataAccessInterface {

    private static final String DEFAULT_CURRENCY = "CAD";
    private static final String EVENTS_KEY = "events";
    private static final String USERNAME_EVENTS_KEY = "usernameToEvents";

    private static final String EVENT_ID_KEY = "eventId";
    private static final String EVENT_NAME_KEY = "eventName";
    private static final String EVENT_DESCRIPTION_KEY = "eventDescription";
    private static final String EVENT_LOCATION_KEY = "eventLocation";
    private static final String EVENT_BUDGET_KEY = "eventBudget";
    private static final String EVENT_CURRENCY_KEY = "eventCurrency";
    private static final String START_DATE_KEY = "startDate";
    private static final String START_TIME_KEY = "startTime";
    private static final String END_DATE_KEY = "endDate";
    private static final String END_TIME_KEY = "endTime";
    private static final String ATTENDEES_KEY = "attendees";
    private static final String EXPENSES_KEY = "expenses";
    private static final String ACTIVITIES_KEY = "activities";
    private static final String EVENT_PHOTO_KEY = "eventPhoto";

    private static final String ACTIVITY_NAME_KEY = "activityName";
    private static final String ACTIVITY_DATE_KEY = "activityDate";
    private static final String ACTIVITY_TIME_KEY = "activityTime";
    private static final String ACTIVITY_LOCATION_KEY = "activityLocation";

    private static final String EXPENSE_ID_KEY = "expenseId";
    private static final String EXPENSE_NAME_KEY = "expenseName";
    private static final String EXPENSE_PAYER_USERNAME_KEY = "expensePayerUsername";
    private static final String EXPENSE_AMOUNT_KEY = "expenseAmount";
    private static final String EXPENSE_ORIGINAL_AMOUNT_KEY = "expenseOriginalAmount";
    private static final String EXPENSE_ORIGINAL_CURRENCY_KEY = "expenseOriginalCurrency";
    private static final String EXPENSE_DEBTORS_KEY = "expenseDebtors";
    private static final String EXPENSE_CUSTOM_SPLIT_KEY = "expenseCustomSplit";
    private static final String EXPENSE_EXPENSE_SPLITS_KEY = "expenseExpenseSplits";
    private static final String EXPENSE_ORIGINAL_SPLITS_KEY = "expenseOriginalSplits";
    private static final String EXPENSE_STATUS_KEY = "expenseStatus";
    private static final String PAID_STATUS = "PAID";

    private final EventFactory eventFactory;
    private final ExpenseFactory expenseFactory;
    private final ActivityFactory activityFactory;
    private final String filePath;

    // eventId -> event details.
    private final Map<Integer, Event> events = new HashMap<>();

    // eventId -> encoded event-photo bytes.
    private final Map<Integer, byte[]> eventPhotos = new HashMap<>();

    // username -> ids of the events that username attends. Always derived from `events`.
    private final Map<String, List<Integer>> usernameToEventIds = new HashMap<>();

    public FileEventDataAccessObject(String fileName, EventFactory eventFactory, ExpenseFactory expenseFactory) {
        this(fileName, eventFactory, expenseFactory, new CommonActivityFactory());
    }

    public FileEventDataAccessObject(
            String fileName,
            EventFactory eventFactory,
            ExpenseFactory expenseFactory,
            ActivityFactory activityFactory) {

        this.filePath = JsonFileUtil.resolvePath(fileName);
        this.eventFactory = eventFactory;
        this.expenseFactory = expenseFactory;
        this.activityFactory = activityFactory;
        loadFromFile();
    }

    @Override
    public void renameUser(String oldUsername, String newUsername) {
        final Map<Integer, Event> renamedEvents = new LinkedHashMap<>();

        for (final Map.Entry<Integer, Event> entry : this.events.entrySet()) {
            renamedEvents.put(
                    entry.getKey(),
                    this.renameWithinEvent(entry.getValue(), oldUsername, newUsername)
            );
        }

        this.events.clear();
        this.events.putAll(renamedEvents);
        saveToFile();
    }

    private Event renameWithinEvent(Event event, String oldUsername, String newUsername) {
        final List<String> attendees = new ArrayList<>();
        for (final String attendee : event.getAttendeeUsernames()) {
            attendees.add(renameMatch(attendee, oldUsername, newUsername));
        }

        final List<Expense> expenses = new ArrayList<>();
        for (final Expense expense : event.getExpenseList()) {
            expenses.add(this.renameWithinExpense(expense, oldUsername, newUsername));
        }

        final EventDetails details = new EventDetails(
                event.getEventName(),
                event.getEventDescription(),
                event.getEventLocation(),
                event.getEventBudget(),
                event.getEventCurrency()
        );

        return eventFactory.createEvent(event.getEventId(), details,
                event.getEventSchedule(), attendees, expenses, event.getActivityList());
    }

    private Expense renameWithinExpense(Expense expense, String oldUsername, String newUsername) {
        final Set<String> debtors = new LinkedHashSet<>();
        for (final String debtor : expense.getDebtors()) {
            debtors.add(renameMatch(debtor, oldUsername, newUsername));
        }

        final Expense renamed = expenseFactory.create(
                expense.getExpenseId(),
                expense.getExpenseName(),
                renameMatch(expense.getPayerUsername(), oldUsername, newUsername),
                expense.getTotalAmount(),
                expense.isCustomSplit(),
                debtors,
                renameKeys(expense.getExpenseSplits(), oldUsername, newUsername)
        );

        renamed.setOriginalValues(
                expense.getOriginalAmount(),
                expense.getOriginalCurrency(),
                renameKeys(expense.getOriginalExpenseSplits(), oldUsername, newUsername)
        );

        if (PAID_STATUS.equalsIgnoreCase(expense.getStatus())) {
            renamed.setStatusPaid();
        }
        else {
            renamed.setStatusUnpaid();
        }

        return renamed;
    }

    private static Map<String, Double> renameKeys(Map<String, Double> amounts,
                                                  String oldUsername, String newUsername) {
        final Map<String, Double> renamed = new LinkedHashMap<>();

        if (amounts != null) {
            for (final Map.Entry<String, Double> entry : amounts.entrySet()) {
                renamed.put(renameMatch(entry.getKey(), oldUsername, newUsername),
                        entry.getValue());
            }
        }

        return renamed;
    }

    private static String renameMatch(String username, String oldUsername, String newUsername) {
        final String result;

        if (username != null && username.equals(oldUsername)) {
            result = newUsername;
        }
        else {
            result = username;
        }

        return result;
    }

    private void loadFromFile() {
        final JSONObject root = JsonFileUtil.readObject(filePath);
        final JSONObject eventsJson = root.optJSONObject(EVENTS_KEY);

        if (eventsJson != null) {
            for (final String eventIdKey : eventsJson.keySet()) {
                final JSONObject json = eventsJson.getJSONObject(eventIdKey);
                final Event event = buildEvent(json);
                events.put(event.getEventId(), event);
                this.loadEventPhoto(json, event.getEventId());
            }
        }
        rebuildUsernameIndex();
    }

    private void loadEventPhoto(JSONObject json, int eventId) {
        final String encodedPhoto = json.optString(EVENT_PHOTO_KEY, "");

        if (!encodedPhoto.isBlank()) {
            try {
                this.eventPhotos.put(
                        eventId,
                        Base64.getDecoder().decode(encodedPhoto)
                );
            }
            catch (IllegalArgumentException exception) {
                this.eventPhotos.remove(eventId);
            }
        }
    }

    private void saveToFile() {
        rebuildUsernameIndex();

        final JSONObject eventsJson = new JSONObject();
        for (final Event event : events.values()) {
            eventsJson.put(String.valueOf(event.getEventId()), toJson(event));
        }

        final JSONObject usernameEventsJson = new JSONObject();
        for (final Map.Entry<String, List<Integer>> entry : usernameToEventIds.entrySet()) {
            usernameEventsJson.put(entry.getKey(), new JSONArray(entry.getValue()));
        }

        final JSONObject root = new JSONObject();
        root.put(EVENTS_KEY, eventsJson);
        root.put(USERNAME_EVENTS_KEY, usernameEventsJson);

        JsonFileUtil.writeObject(filePath, root);
    }

    private void rebuildUsernameIndex() {
        usernameToEventIds.clear();

        for (final Event event : events.values()) {
            for (final String username : event.getAttendeeUsernames()) {
                usernameToEventIds
                        .computeIfAbsent(username, key -> new ArrayList<>())
                        .add(event.getEventId());
            }
        }
    }

    /**
     * Returns the ids of the events the given username attends, in no particular order.
     * Backed entirely by the in-memory index derived from the stored events.
     *
     * @param username the attendee's username
     * @return the ids of events they attend, or an empty list if they attend none
     */
    public List<Integer> getEventIdsForUsername(String username) {
        return Collections.unmodifiableList(
                usernameToEventIds.getOrDefault(username, new ArrayList<>()));
    }

    /**
     * Reconstructs an Event entity from its JSON representation.
     * @param json the JSON object that stores the event data
     * @return an event object constructed from JSON data
     */
    private Event buildEvent(JSONObject json) {
        final int eventId = json.getInt(EVENT_ID_KEY);
        final String eventName = json.optString(EVENT_NAME_KEY, "");
        final String eventDescription = json.optString(EVENT_DESCRIPTION_KEY, "");
        final String eventLocation = json.optString(EVENT_LOCATION_KEY, "");
        final Double eventBudget = readNullableDouble(json, EVENT_BUDGET_KEY);
        final String eventCurrency = readEventCurrency(json);
        final LocalDate startDate = LocalDate.parse(json.getString(START_DATE_KEY));
        final LocalTime startTime = LocalTime.parse(json.getString(START_TIME_KEY));
        final String endDateText;
        final String endTimeText;
        if (json.has(END_DATE_KEY) && !json.isNull(END_DATE_KEY)) {
            endDateText = json.getString(END_DATE_KEY);
        }
        else {
            endDateText = "";
        }
        if (json.has(END_TIME_KEY) && !json.isNull(END_TIME_KEY)) {
            endTimeText = json.getString(END_TIME_KEY);
        }
        else {
            endTimeText = "";
        }
        LocalDate endDate = null;
        LocalTime endTime = null;
        if (!endDateText.isBlank()) {
            endDate = LocalDate.parse(endDateText);
        }
        if (!endTimeText.isBlank()) {
            endTime = LocalTime.parse(endTimeText);
        }
        final EventSchedule eventSchedule = new EventSchedule(startDate, startTime, endDate, endTime);
        final List<String> attendeeUsernames = buildUsernameList(json.optJSONArray(ATTENDEES_KEY));
        final List<Expense> expenseList = buildExpenseList(json.optJSONArray(EXPENSES_KEY), eventCurrency);
        final List<Activity> activityList = buildActivityList(json.optJSONArray(ACTIVITIES_KEY));

        final EventDetails details = new EventDetails(
                                             eventName,
                                             eventDescription,
                                             eventLocation,
                                             eventBudget,
                                             eventCurrency);

        return eventFactory.createEvent(eventId, details, eventSchedule, attendeeUsernames,
                expenseList, activityList);
    }

    /**
     * Serializes an Event entity into its JSON representation.
     * @param event an event object holding event related data
     * @return the JSON object with the event data
     */
    private JSONObject toJson(Event event) {
        final JSONObject json = new JSONObject();
        json.put(EVENT_ID_KEY, event.getEventId());
        json.put(EVENT_NAME_KEY, event.getEventName());
        json.put(EVENT_DESCRIPTION_KEY, event.getEventDescription());
        json.put(EVENT_LOCATION_KEY, event.getEventLocation());
        json.put(EVENT_BUDGET_KEY, writeNullableDouble(event.getEventBudget()));
        json.put(EVENT_CURRENCY_KEY, event.getEventCurrency());
        final EventSchedule eventSchedule = event.getEventSchedule();
        json.put(START_DATE_KEY, eventSchedule.getStartDate().toString());
        json.put(START_TIME_KEY, eventSchedule.getStartTime().toString());

        if (eventSchedule.getEndDate() == null) {
            json.put(END_DATE_KEY, JSONObject.NULL);
        }
        else {
            json.put(END_DATE_KEY, eventSchedule.getEndDate().toString());
        }

        if (eventSchedule.getEndTime() == null) {
            json.put(END_TIME_KEY, JSONObject.NULL);
        }
        else {
            json.put(END_TIME_KEY, eventSchedule.getEndTime().toString());
        }
        json.put(ATTENDEES_KEY, new JSONArray(event.getAttendeeUsernames()));
        json.put(EXPENSES_KEY, expensesToJson(event.getExpenseList()));
        json.put(ACTIVITIES_KEY, activitiesToJson(event.getActivityList()));
        final byte[] eventPhoto = this.eventPhotos.get(event.getEventId());
        if (eventPhoto != null && eventPhoto.length > 0) {
            json.put(
                    EVENT_PHOTO_KEY,
                    Base64.getEncoder().encodeToString(eventPhoto)
            );
        }
        return json;
    }

    private String readEventCurrency(final JSONObject json) {
        final String currencyCode;

        if (json.has(EVENT_CURRENCY_KEY) && !json.isNull(EVENT_CURRENCY_KEY)
                && !json.optString(EVENT_CURRENCY_KEY).isBlank()) {
            currencyCode = json.optString(EVENT_CURRENCY_KEY);
        }
        else {
            currencyCode = DEFAULT_CURRENCY;
        }

        return currencyCode;
    }

    private Double readNullableDouble(JSONObject json, String key) {
        final Double value;
        if (json.has(key) && !json.isNull(key)) {
            value = json.getDouble(key);
        }
        else {
            value = null;
        }
        return value;
    }

    private Object writeNullableDouble(Double value) {
        final Object jsonValue;
        if (value == null) {
            jsonValue = JSONObject.NULL;
        }
        else {
            jsonValue = value;
        }
        return jsonValue;
    }

    private List<String> buildUsernameList(JSONArray array) {
        final List<String> usernames = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                usernames.add(array.getString(i));
            }
        }
        return usernames;
    }

    private List<Expense> buildExpenseList(JSONArray array, String eventCurrency) {
        final List<Expense> expenses = new ArrayList<>();

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject json = array.getJSONObject(i);
                final int expenseId = json.getInt(EXPENSE_ID_KEY);
                final String expenseName = json.getString(EXPENSE_NAME_KEY);
                final String payerUsername = json.getString(EXPENSE_PAYER_USERNAME_KEY);
                final double totalAmount = json.getDouble(EXPENSE_AMOUNT_KEY);
                final double originalAmount = json.optDouble(EXPENSE_ORIGINAL_AMOUNT_KEY, totalAmount);
                final String originalCurrency = json.optString(EXPENSE_ORIGINAL_CURRENCY_KEY, eventCurrency);
                final boolean isCustomSplit = json.optBoolean(EXPENSE_CUSTOM_SPLIT_KEY, false);
                final Set<String> debtors = buildDebtorSet(json.optJSONArray(EXPENSE_DEBTORS_KEY));
                final Map<String, Double> expenseSplits =
                        buildExpenseSplits(json.optJSONObject(EXPENSE_EXPENSE_SPLITS_KEY));
                final Map<String, Double> originalExpenseSplits =
                        buildExpenseSplits(json.optJSONObject(EXPENSE_ORIGINAL_SPLITS_KEY));
                final Map<String, Double> resolvedOriginalExpenseSplits;

                if (originalExpenseSplits.isEmpty()) {
                    resolvedOriginalExpenseSplits = expenseSplits;
                }
                else {
                    resolvedOriginalExpenseSplits = originalExpenseSplits;
                }

                final Expense expense = expenseFactory.create(
                        expenseId,
                        expenseName,
                        payerUsername,
                        totalAmount,
                        isCustomSplit,
                        debtors,
                        expenseSplits
                );
                expense.setOriginalValues(
                        originalAmount,
                        originalCurrency,
                        resolvedOriginalExpenseSplits
                );

                if ("PAID".equalsIgnoreCase(json.optString(EXPENSE_STATUS_KEY, "UNPAID"))) {
                    expense.setStatusPaid();
                }

                expenses.add(expense);
            }
        }

        return expenses;
    }

    private JSONArray expensesToJson(List<Expense> expenseList) {
        final JSONArray array = new JSONArray();

        for (final Expense expense : expenseList) {
            final JSONObject json = new JSONObject();
            json.put(EXPENSE_ID_KEY, expense.getExpenseId());
            json.put(EXPENSE_NAME_KEY, expense.getExpenseName());
            json.put(EXPENSE_PAYER_USERNAME_KEY, expense.getPayerUsername());
            json.put(EXPENSE_AMOUNT_KEY, expense.getTotalAmount());
            json.put(EXPENSE_ORIGINAL_AMOUNT_KEY, expense.getOriginalAmount());
            json.put(EXPENSE_ORIGINAL_CURRENCY_KEY, expense.getOriginalCurrency());
            json.put(EXPENSE_CUSTOM_SPLIT_KEY, expense.isCustomSplit());
            json.put(EXPENSE_DEBTORS_KEY, new JSONArray(expense.getDebtors()));
            json.put(EXPENSE_EXPENSE_SPLITS_KEY, new JSONObject(expense.getExpenseSplits()));
            json.put(EXPENSE_ORIGINAL_SPLITS_KEY, new JSONObject(expense.getOriginalExpenseSplits()));
            json.put(EXPENSE_STATUS_KEY, expense.getStatus());
            array.put(json);
        }

        return array;
    }

    private List<Activity> buildActivityList(JSONArray array) {
        final List<Activity> activities = new ArrayList<>();

        if (array != null) {
            for (int index = 0; index < array.length(); index++) {
                final JSONObject json = array.getJSONObject(index);

                activities.add(
                        this.activityFactory.create(
                                json.optString(ACTIVITY_NAME_KEY, ""),
                                json.optString(ACTIVITY_DATE_KEY, ""),
                                json.optString(ACTIVITY_TIME_KEY, ""),
                                json.optString(ACTIVITY_LOCATION_KEY, "")
                        )
                );
            }
        }

        return activities;
    }

    private JSONArray activitiesToJson(List<Activity> activityList) {
        final JSONArray array = new JSONArray();

        for (final Activity activity : activityList) {
            final JSONObject json = new JSONObject();

            json.put(ACTIVITY_NAME_KEY, activity.getActivityName());
            json.put(ACTIVITY_DATE_KEY, activity.getDate());
            json.put(ACTIVITY_TIME_KEY, activity.getTime());
            json.put(ACTIVITY_LOCATION_KEY, activity.getLocation());
            array.put(json);
        }

        return array;
    }

    private Set<String> buildDebtorSet(JSONArray array) {
        final Set<String> debtors = new LinkedHashSet<>();

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                debtors.add(array.getString(i));
            }
        }

        return debtors;
    }

    private Map<String, Double> buildExpenseSplits(JSONObject json) {
        final Map<String, Double> splits = new HashMap<>();

        if (json != null) {
            for (final String username : json.keySet()) {
                splits.put(username, json.getDouble(username));
            }
        }

        return splits;
    }

    // --- Interface methods ---

    @Override
    public int getNextEventId() {
        int highestEventId = 0;
        for (final int eventId : events.keySet()) {
            if (eventId > highestEventId) {
                highestEventId = eventId;
            }
        }
        return highestEventId + 1;
    }

    @Override
    public void saveEvent(Event event) {
        events.put(event.getEventId(), event);
        saveToFile();
    }

    @Override
    public void deleteEvent(int eventId) {
        events.remove(eventId);
        eventPhotos.remove(eventId);
        // saveToFile() rebuilds the username index from the remaining events, so the deleted
        // event drops out of every attendee's list automatically.
        saveToFile();
    }

    @Override
    public boolean isAttendingEvent(int eventId, String username) {
        final boolean result;
        if (usernameToEventIds.containsKey(username)) {
            result = usernameToEventIds.get(username).contains(eventId);
        }
        else {
            result = false;
        }
        return result;
    }

    @Override
    public Event getEvent(int eventId) throws WhoOwesWhatDataAccessException {
        final Event event = events.get(eventId);
        if (event == null) {
            throw new WhoOwesWhatDataAccessException("No event was found with ID " + eventId + ".");
        }
        return event;
    }

    @Override
    public List<Integer> getEventIds(String username) {
        return usernameToEventIds.get(username);
    }

    @Override
    public byte[] getEventPhoto(int eventId) {
        final byte[] storedPhoto = this.eventPhotos.get(eventId);
        final byte[] result;

        if (storedPhoto == null) {
            result = null;
        }
        else {
            result = storedPhoto.clone();
        }

        return result;
    }

    @Override
    public void saveEventPhoto(int eventId, byte[] photoBytes)
            throws EventPhotoDataAccessException {

        if (!this.events.containsKey(eventId)) {
            throw new EventPhotoDataAccessException(
                    "The selected event could not be found."
            );
        }

        this.eventPhotos.put(eventId, photoBytes.clone());
        this.saveToFile();
    }
}
