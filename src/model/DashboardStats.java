package src.model;

public class DashboardStats {

    private double income;
    private double expense;


    public DashboardStats(double income, double expense) {

        this.income = income;
        this.expense = expense;

    }


    public double getIncome() {

        return income;

    }


    public double getExpense() {

        return expense;

    }


    public double getBalance() {

        return income - expense;

    }

}