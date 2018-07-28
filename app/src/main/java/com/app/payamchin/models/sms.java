package com.app.payamchin.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class sms {

    private String address, body, date, time;

    public sms(String address, String body, long date){
        this.address = address;
        this.body = body;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
        this.date = sdf.format(new Date(date));

    }


}
