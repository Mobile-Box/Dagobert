package com.svp.svp.Objects.BalanceSheet;

/**
 * Created by Eric Schumacher on 30.01.2018.
 */

public class BalanceSheet_Account extends BalanceSheet {

    private int id;

    public BalanceSheet_Account(String type, String name, double amount, int id) {
        super(type, name, amount);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
