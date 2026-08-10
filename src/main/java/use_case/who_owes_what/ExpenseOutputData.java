package use_case.who_owes_what;

public class ExpenseOutputData {

    private final String expenseName;
    private final double shareAmount;

    public ExpenseOutputData(String expenseName, double shareAmount) {

        this.expenseName = expenseName;
        this.shareAmount = shareAmount;

    }

    public String getExpenseName() {
        return expenseName;
    }

    public double getShareAmount() {
        return shareAmount;
    }

}
