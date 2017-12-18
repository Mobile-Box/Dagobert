package com.svp.svp.Objects;

import java.io.Serializable;

/**
 * Created by Eric Schumacher on 14.12.2017.
 */

public class Transaction implements Serializable {
    private String code;
    private String name;
    private String type;
    private String detailOne;
    private String detailTwo;
    private double amount;
    private String date;
    private int bankAccountId;

    public Transaction(String code, String name, String type, String detailOne, String detailTwo, double amount, String date, int bankAccountId) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.detailOne = detailOne;
        this.detailTwo = detailTwo;
        this.amount = amount;
        this.date = date;
        this.bankAccountId = bankAccountId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDetailOne() {
        return detailOne;
    }

    public String getDetailTwo() {
        return detailTwo;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public int getBankAccountId() {
        return bankAccountId;
    }
}
