package com.svp.svp.Objects.BalanceSheet;

import com.svp.svp.Objects.Navigation.Navigation_Date;

/**
 * Created by Eric Schumacher on 30.01.2018.
 */

public class BalanceSheet_Account extends BalanceSheet {

    private int id;

    public BalanceSheet_Account(String type, String name, double amount, int id, Navigation_Date date) {
        super(type, name, amount, date);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
