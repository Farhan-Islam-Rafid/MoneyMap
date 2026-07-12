package src.model;

import java.sql.Date;

public class Transaction {
    private int id;
    private String type; // "Income" or "Expense"
    private double amount;
    private Date date;
    private String note;

    public Transaction(int id, String type, double amount, Date date, String note) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}