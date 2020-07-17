package com.elkasaga.undegraduatethesisproject.utils;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConvert {

    public static String threeLettersMonth(int month){
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return months[month];
    }

    public static String getDisplayableTime(long delta)
    {
        long difference=0;
        Long mDate = java.lang.System.currentTimeMillis();


        if(mDate > delta)
        {
            difference= mDate - delta;

            final long seconds = difference/1000;
            final long minutes = seconds/60;
            final long hours = minutes/60;
            final long days = hours/24;
            final long months = days/31;
            final long years = days/365;

            if (seconds < 0)
            {
                return "not yet";
            }
            else if (seconds < 60)
            {
                return seconds == 1 ? "one second ago" : seconds + " seconds ago";
            }
            else if (seconds < 120)
            {
                return "a minute ago";
            }
            else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes ago";
            }
            else if (seconds < 5400) // 90 * 60
            {
                return "an hour ago";
            }
            else if (seconds < 86400) // 24 * 60 * 60
            {
                return hours + " hours ago";
            }
            else if (seconds < 172800) // 48 * 60 * 60
            {
                return "yesterday";
            }
            else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                return days + " days ago";
            }
            else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {

                return months <= 1 ? "one month ago" : days + " months ago";
            }
            else
            {

                return years <= 1 ? "one year ago" : years + " years ago";
            }
        } else if (mDate < delta) {

            difference = delta - mDate;

            final long seconds = difference/1000;
            final long minutes = seconds/60;
            final long hours = minutes/60;
            final long days = hours/24;
            final long months = days/31;
            final long years = days/365;

            if (seconds < 0)
            {
                return "not yet";
            }
            else if (seconds < 60)
            {
                return seconds == 1 ? "one second to go" : seconds + " seconds to go";
            }
            else if (seconds < 120)
            {
                return "a minute to go";
            }
            else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes to go";
            }
            else if (seconds < 5400) // 90 * 60
            {
                return "an hour to go";
            }
            else if (seconds < 86400) // 24 * 60 * 60
            {
                return hours + " hours to go";
            }
            else if (seconds < 172800) // 48 * 60 * 60
            {
                return "yesterday";
            }
            else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                return days + " days to go";
            }
            else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {

                return months <= 1 ? "one month to go" : days + " months to go";
            }
            else
            {

                return years <= 1 ? "one year to go" : years + " years to go";
            }
        } else{
            return "ongoing";
        }
    }

    public static Date convertStringToDate(String date){
        Date date1 = null;
        SimpleDateFormat df1;
       if (date.contains(":")){
           df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

       } else {
           df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
       }

        if (df1 != null){
            try {
                date1 = df1.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date1;
    }

    public static String timestammpToChatDate(Date date){
        Date currentDate = new Date();
        String sendtime = "";
        Log.d("", "DATE = "+date);
        Log.d("", "CURRENT DATE = "+currentDate);
        if (currentDate.getDate() == date.getDate()){
            sendtime = String.valueOf(date.getHours()) + ":" +String.valueOf(date.getMinutes());
        } else if (currentDate.getDay() != date.getDay() && currentDate.getYear() == date.getYear()){
            sendtime = String.valueOf(date.getDay()) + " " + threeLettersMonth(date.getMonth()) + " at " + String.valueOf(date.getHours()) + ":" +String.valueOf(date.getMinutes());
        } else if (currentDate.getDay() != date.getDay() && currentDate.getYear() != date.getYear()){
            sendtime = String.valueOf(date.getDay()) + " " + threeLettersMonth(date.getMonth()) + " " + String.valueOf(date.getYear()) + " at " + String.valueOf(date.getHours()) + ":" +String.valueOf(date.getMinutes());
        }

        return sendtime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long dateToMillis(Date date){
        long millis = date.toInstant().toEpochMilli();
        return millis;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCollapsedDateRange(Date startdate, Date enddate, Context context){

        long startmillis =  startdate.toInstant().toEpochMilli();
        long endmillis = enddate.toInstant().toEpochMilli();

        String range = DateUtils.formatDateRange(context, startmillis, endmillis, DateUtils.FORMAT_ABBREV_TIME);
        return range;
    }

    public static ArrayList<String> getDates(String dateString1, String dateString2)
    {
        ArrayList<String> dates = new ArrayList<>();
        String myFormat = "";

        if (dateString1.contains(":")){
            myFormat = "dd/MM/yyyy HH:mm";
        } else{
            myFormat = "dd/MM/yyyy";
        }

        SimpleDateFormat df1 = new SimpleDateFormat(myFormat, Locale.getDefault());

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(df1.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }
}
