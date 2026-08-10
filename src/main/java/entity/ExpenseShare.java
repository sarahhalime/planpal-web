package entity;

public class ExpenseShare {

    // attendeeId: username of the user responsible for this share.
    // shareAmount: Amount that attendee owes toward the expense.

    private final String username;
    private final double shareAmount;

    public ExpenseShare(String username, double shareAmount) {

        this.username = username;
        this.shareAmount = shareAmount;

    }

    public String getAttendeeId() {
        return username;
    }

    public double getShareAmount() {
        return shareAmount;
    }

}
