package com.svp.svp.Objects.Navigation;

import java.io.Serializable;
import java.text.DateFormatSymbols;

/**
 * Created by Eric Schumacher on 04.01.2018.
 */

public abstract class Navigation_Date implements Serializable {

    public String getName(int dateValue) {
        if (dateValue > 12) {
            return Integer.toString(dateValue);
        } else {
            return  new DateFormatSymbols().getMonths()[dateValue-1];
        }
    }

    public abstract int getValue();
}
