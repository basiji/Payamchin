package com.app.payamchin.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {

    private Context context;

    public NetworkHelper(Context context)
    {
        this.context = context;
    }

    // Check network interface (cellular and wifi)
    public boolean hasNetworkInterface(){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
}
