package com.example.caustudy;

import java.util.Calendar;

public class getDay {
    public static String getMonday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return formatter.format(c.getTime());
    }
    public static String getTuesday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        return formatter.format(c.getTime());
    }
    public static String getWednesday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        return formatter.format(c.getTime());
    }
    public static String getThursday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        return formatter.format(c.getTime());
    }
    public static String getFriday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        return formatter.format(c.getTime());
    }
    public static String getSaturday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        return formatter.format(c.getTime());
    }
    public static String getSunday(){
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy.MM.dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return formatter.format(c.getTime());
    }


}
