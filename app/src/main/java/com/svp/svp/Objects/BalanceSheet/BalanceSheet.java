package com.svp.svp.Objects.BalanceSheet;

import com.svp.svp.Constants.Constants_Intern;
import com.svp.svp.Constants.Constants_Network;
import com.svp.svp.Objects.Navigation.Navigation_Date;

/**
 * Created by Eric Schumacher on 30.01.2018.
 */

public class BalanceSheet {

    private String type;
    private String name;
    private double amount;
    private Navigation_Date date;

    public BalanceSheet(String type, String name, double amount, Navigation_Date date) {
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.date = date;
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

    public Navigation_Date getDate() {
        return date;
    }

    public void setDate(Navigation_Date date) {
        this.date = date;
    }


    public static String getNextLevel(String type) {
        if (type.equals(Constants_Network.BS_TYPE_ACCOUNT)) {
            return Constants_Intern.BALANCESHEET_TYPE_SUBACCOUNTS;
        }
        if (type.equals(Constants_Network.BS_TYPE_SUBACCOUNT)) {
            return Constants_Intern.BALANCESHEET_TYPE_OPERATIONS;
        }
        return null;
    }

    public static String getBackLevel(String type) {
        if (type.equals(Constants_Network.BS_TYPE_SUBACCOUNT)) {
            return Constants_Intern.BALANCESHEET_TYPE_ACCOUNTS;
        }
        if (type.equals(Constants_Network.BS_TYPE_OPERATION)) {
            return Constants_Intern.BALANCESHEET_TYPE_SUBACCOUNTS;
        }
        return null;
    }
}
