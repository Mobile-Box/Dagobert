package com.svp.svp.Objects.Navigation;

/**
 * Created by Eric Schumacher on 04.01.2018.
 */

public class Navigation_Month extends Navigation_Year {

    int month;

    public Navigation_Month (int y, int m) {
        super(y);
        month = m;
    }

    public int getMonth() {
        return month;
    }

    public int getValue() {
        return month;
    }
}
