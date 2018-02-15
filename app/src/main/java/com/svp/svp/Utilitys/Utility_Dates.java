package com.svp.svp.Utilitys;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Eric Schumacher on 15.02.2018.
 */

public class Utility_Dates {

    public static String encodeDateForSQL(String date) {
        date = date.replace(".", "");
        date = date.substring(4, 8) + date.substring(2, 4) + date.substring(0, 2);
        return date;
    }

    public static String decodeDateFromSQL(String date) {
        Calendar d = Calendar.getInstance();
        d.set(Calendar.YEAR, Integer.parseInt(date.substring(0,4)));
        d.set(Calendar.MONTH, Integer.parseInt(date.substring(4,6)));
        d.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6,8)));
        SimpleDateFormat format = new SimpleDateFormat("d. MMM - y");
        return format.format(d.getTime());
    }


}
