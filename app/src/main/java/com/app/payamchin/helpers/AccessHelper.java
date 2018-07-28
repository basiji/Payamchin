package com.app.payamchin.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessHelper {

    private SharedPreferences prefs;

    public AccessHelper(Context context) {
        prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    }

    // User registration status
    public boolean isRegistered(){
        return !prefs.getString("userid","").equals("");
    }
    public void registerUser(String userid){
        prefs.edit().putString("userid",userid).apply();
    }
    public String getUserID(){
        return prefs.getString("userid","");
    }

    // Activate user after successfull payment
    public void activateUser() {
        prefs.edit().putBoolean("isActive", true).apply();
    }
    public boolean isActive(){
        return prefs.getBoolean("isActive",false);
    }

    // Server IP set and get
    public void updateIP(String ip) {
        prefs.edit().putString("ip", ip.split("#")[0]).apply();
        prefs.edit().putString("gateway_ip",ip.split("#")[1]).apply();
    }
    public String getIP() {
        return prefs.getString("ip", "");
    }
    public String getGatewayIP(){return prefs.getString("gateway_ip","");}

}
