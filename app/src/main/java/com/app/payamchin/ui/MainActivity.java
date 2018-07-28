package com.app.payamchin.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.payamchin.R;
import com.app.payamchin.helpers.APIClient;
import com.app.payamchin.helpers.AccessHelper;
import com.app.payamchin.helpers.Constants;
import com.app.payamchin.helpers.DeviceHelper;
import com.app.payamchin.helpers.EndpointInterface;
import com.app.payamchin.helpers.NetworkHelper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Dialogs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Global helpers
    private AccessHelper accessHelper;
    private NetworkHelper networkHelper;
    private Dialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1-Initiate AccessHelper
        accessHelper = new AccessHelper(this);
        this.networkHelper = new NetworkHelper(this);
        dialogs = new Dialogs(this);

        // Fetch IP
        new fetchIP().execute();

        // 3- Process incoming from gateway
        processPayment(getIntent());

        // 4- Apply Click Listeners
        applyClickListeners();

        // 5- Apply fonts
        applyFonts();

    }

    // Process payment (returning from gateway)
    public void processPayment(Intent intent){
        if(intent.hasExtra("result")) {
            String result = intent.getStringExtra("result"); // "status"
            if(result.equals("success")){

                // Payment successful
                accessHelper.activateUser();
                Toast.makeText(this, Constants.PAYMENT_SUCCESSFULL, Toast.LENGTH_SHORT).show();


            } else {

                // Payment failed
                Toast.makeText(this, Constants.PAYMENT_FAILED, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Fetch server IP from static web
    private class fetchIP extends AsyncTask<String, String, String>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = dialogs.getProgressDialog(Constants.PLEASE_WAIT);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        private String ip;
        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(Constants.IP_SOURCE_URL);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                accessHelper.updateIP(in.readLine());
                in.close();
                publishProgress("success");
            } catch (IOException e) { // error handling}
                publishProgress("failed");
            }
            return "Done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            // If IP fetched successfully
            if(!values[0].equals("failed")){

                // Ip fetched successfully -> now register user
                if(!accessHelper.isRegistered())
                    registerUser();
                else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, Constants.WELCOME_MESSAGE, Toast.LENGTH_SHORT).show();
                }
            }
        }

        // registerUser user -> save userid
        public void registerUser(){

            APIClient.BASE_URL = accessHelper.getIP();
            EndpointInterface service = APIClient.getClient().create(EndpointInterface.class);

            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("model",new DeviceHelper().getDeviceName());
            Call call = service.registerUser(dataMap);

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {

                    if(response == null) {
                        Toast.makeText(MainActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Parse json result
                    try {
                        JSONObject object = new JSONObject(new Gson().toJson(response.body()));
                        accessHelper.registerUser(object.getString("userid"));
                    } catch (JSONException ex){
                        Toast.makeText(MainActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(MainActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                }

            });

            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, Constants.WELCOME_MESSAGE, Toast.LENGTH_SHORT).show();

        }
    }

    // Apply click listeners and fonts
    private void applyClickListeners(){

        // VAS scan button
        findViewById(R.id.button_adblock).setOnClickListener(this);
        findViewById(R.id.button_antivirus).setOnClickListener(this);
        findViewById(R.id.button_scan).setOnClickListener(this);

    }

    // Apply fonts
    public void applyFonts(){

        // Custom fonts
        Typeface sans = Typeface.createFromAsset(getAssets(),"iransans.ttf");
        ((TextView) findViewById(R.id.button_1_text)).setTypeface(sans);
        ((TextView) findViewById(R.id.button_2_text)).setTypeface(sans);
        ((TextView) findViewById(R.id.button_3_text)).setTypeface(sans);
        ((TextView) findViewById(R.id.splash_text)).setTypeface(sans);

    }

    // Click listener
    @Override
    public void onClick(View view) {

        final Class destination;

        switch (view.getId()){

            case R.id.button_scan:
                destination = VASActivity.class;
            break;

            case R.id.button_adblock:
                destination = AdActivity.class;
            break;

            case R.id.button_antivirus:
                destination = VirusActivity.class;
            break;

            default:
                destination = MainActivity.class;


        }
        if(networkHelper.hasNetworkInterface())
                startActivity(new Intent(MainActivity.this, destination));
        else
            Toast.makeText(this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
    }

}
