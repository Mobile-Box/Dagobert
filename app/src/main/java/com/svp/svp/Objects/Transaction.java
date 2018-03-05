package com.svp.svp.Objects;

import java.io.Serializable;

/**
 * Created by Eric Schumacher on 14.12.2017.
 */

public class Transaction implements Serializable {
    private int id;
    private String code;
    private String name;
    private String type;
    private String detailOne;
    private String detailTwo;
    private double amount;
    private String date;
    private int bankAccountId;
    private int svpSubAccountId;
    private int ustValue = 19;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUstValue() {
        return ustValue;
    }

    public void setUstValue(int ustValue) {
        this.ustValue = ustValue;
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

    public int getYear() {
        return Integer.parseInt(date.substring(0,4));
    }

    public int getMonth() {
        return Integer.parseInt(date.substring(4,6))-1;
    }

    public int getDay() {
        return Integer.parseInt(date.substring(6,8));
    }

    public void setMonth(int m) {
        String month = Integer.toString(m+1);
        StringBuffer sb = new StringBuffer(date);
        sb.replace(4,6, (month.length() < 2) ? "0"+month : month);
        date = sb.toString();
    }

    public void setDay(int d) {
        String day = Integer.toString(d);
        StringBuffer sb = new StringBuffer(date);
        sb.replace(6,8, (day.length() < 2) ? "0"+day : day);
        date = sb.toString();
    }

    public void setYear(int y) {
        String year = Integer.toString(y);
        StringBuffer sb = new StringBuffer(date);
        sb.replace(0,4, year);
        date = sb.toString();
    }
}
