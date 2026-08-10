package use_case.who_owes_what;

import java.util.List;

public class AttendeeBalanceOutputData {

    private final String name;
    private final double totalAmount;
    private final String balanceStatus;
    private final List<ExpenseOutputData> expenses;
    private final byte[] profilePicture;

    public AttendeeBalanceOutputData(
            String name,
            double totalAmount,
            String balanceStatus,
            List<ExpenseOutputData> expenses,
            byte[] profilePicture) {

        this.name = name;
        this.totalAmount = totalAmount;
        this.balanceStatus = balanceStatus;
        this.expenses = expenses;
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getBalanceStatus() {
        return balanceStatus;
    }

    public List<ExpenseOutputData> getExpenses() {
        return expenses;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

}
