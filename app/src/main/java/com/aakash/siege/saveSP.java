package com.aakash.siege;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.List;
import java.util.Set;

/**
 * Created by aakash on 2/2/18.
 */

public class saveSP extends apollo_client {
    static final String PREF_USER_NAME= "username";
    static final String PREF_USER_LAT= "Lat";
    static final String PREF_USER_LON= "lon";
    static final String OFF_LOCATION= "loc";
    static Set<String> nset;

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLocation(Context ctx,Double userLat,Double userLon){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_LAT, userLat.toString());
        editor.putString(PREF_USER_LON, userLon.toString());
        editor.apply();
    }
    public static String getUserLat(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_LAT,"");
    }
    public static String getUserLon(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_LON,"");
    }
    public static void setoffLocation(Context ctx, List<String> offLocation){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        Gson gson = new Gson();
        String json = gson.toJson(offLocation);
        editor.putString(OFF_LOCATION,json);
        editor.apply();
    }
    public  static String getoffLocation(Context ctx){
        return getSharedPreferences(ctx).getString(OFF_LOCATION,"");
    }
    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.apply();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
}
