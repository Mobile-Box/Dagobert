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
    private int svpSubAccountId;

    public Transaction(String code, String name, String type, String detailOne, String detailTwo, double amount, String date, int bankAccountId) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.detailOne = detailOne;
        this.detailTwo = detailTwo;
        this.amount = amount;
        this.date = date;
        this.bankAccountId = bankAccountId;
        svpSubAccountId = 0;
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

    public int getSvpSubAccountId() {
        return svpSubAccountId;
    }

    public void setSvpSubAccountId(int svpSubAccountId) {
        this.svpSubAccountId = svpSubAccountId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDetailOne(String detailOne) {
        this.detailOne = detailOne;
    }

    public void setDetailTwo(String detailTwo) {
        this.detailTwo = detailTwo;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBankAccountId(int bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
