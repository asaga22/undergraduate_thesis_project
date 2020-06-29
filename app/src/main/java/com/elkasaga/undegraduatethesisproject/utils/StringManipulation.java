package com.elkasaga.undegraduatethesisproject.utils;

import android.util.Log;

import java.util.Random;

public class StringManipulation {

    private static final String TAG = "StringManipulation";

    public static String expandUsername(String username){
        if (username.contains(".")){
            username =  username.replace(".", " ");
        }
        return username;
    }

    public static String condenseUsername(String username){
        if (username.contains(" ")){
            username = username.replace(" ", ".");
        }
        return username.toLowerCase();
    }

    public static String getSixRandomNumber() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public boolean isInputNull(String input){
        Log.d(TAG, "checkInput: checking inputs for null values");

        if (input.isEmpty()){
            return true;
        }
        return false;
    }
}
