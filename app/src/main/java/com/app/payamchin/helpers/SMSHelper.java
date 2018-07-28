package com.app.payamchin.helpers;

import java.util.List;
import me.everything.providers.android.telephony.Sms;

public class SMSHelper {

    // Filter irrelevant SMS
    public boolean isBank(String body, String address){

        // Check for banned address
        for (String s: Constants.banned_address) {
            if(address.contains(s)){
                return false;
            }
        }

        for (String s:Constants.selected_keywords){
            if(body.contains(s))
                return true;
        }

        return false;
    }

    // Filter irrelevant SMS
    public String isAd(String body){

        // Check for banned address
        for (String s: Constants.ad_keywords) {
            if(body.contains(s)){
                return s;
            }
        }
        return "";
    }

    // Get number of AD address occurrences
    public int getTimes(String address, List<Sms> list ){
        int count = 0;
        for (Sms s:list){
            if(s.address.equals(address))
                count++;
        }
        return count;
    }

}
