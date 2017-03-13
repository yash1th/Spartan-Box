package com.kirandama.spartanbox.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kiran on 10/16/15.
 */
public class DateFormat {

    public static String convertDate(String fromDate) {


        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date convertedDate = null;

        try {

            convertedDate = sdf.parse(fromDate);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            String convertedDateString = formatter.format(convertedDate);

            return convertedDateString;
        } catch(Exception ex){

            System.out.println("Exc "+ex);
            ex.printStackTrace();
            return null;
        }
    }
}