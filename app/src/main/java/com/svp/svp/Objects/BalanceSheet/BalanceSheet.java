package com.svp.svp.Objects.BalanceSheet;

/**
 * Created by Eric Schumacher on 30.01.2018.
 */

public class BalanceSheet {

    private String type;
    private String name;
    private double amount;

    public BalanceSheet(String type, String name, double amount) {
        this.type = type;
        this.name = name;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
