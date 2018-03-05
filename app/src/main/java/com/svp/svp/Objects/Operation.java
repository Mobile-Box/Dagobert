package com.svp.svp.Objects;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by Eric Schumacher on 24.02.2018.
 */

public class Operation implements Serializable {
    private int id;
    private String code;
    private String name;
    private String date;
    private double amount_gross;
    private double amount_net;
    private int ust_value;
    private int id_svp_subaccount;

    public Operation(int id, String code, String name, String date, double amount_gross, double amount_net, int id_svp_subaccount,  int ust_value) {
        this.id = id;
        this.ust_value = ust_value;
        this.code = code;
        this.name = name;
        this.date = date.replace("-", "");
        this.amount_gross = amount_gross;
        this.amount_net = amount_net;
        this.id_svp_subaccount = id_svp_subaccount;
    }

    public int getId() {
        return id;
    }

    public int getUst_value() {
        return ust_value;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public double getAmount_gross() {
        return amount_gross;
    }

    public double getAmount_net() {
        return amount_net;
    }

    public int getId_svp_subaccount() {
        return id_svp_subaccount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUst_value(int ust_value) {
        this.ust_value = ust_value;
    }

    public void setId_svp_subaccount(int id_svp_subaccount) {
        this.id_svp_subaccount = id_svp_subaccount;
    }

    public int getYear() {
        return Integer.parseInt(date.substring(0,4));
    }

    public int getMonth() {
        return Integer.parseInt(date.substring(4,6))-1;
    }

    public int getDay() {
        Log.i("Ichmachschon", date);
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
