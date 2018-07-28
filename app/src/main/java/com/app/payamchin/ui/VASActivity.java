package com.app.payamchin.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.payamchin.R;
import com.app.payamchin.adapters.VasAdapter;
import com.app.payamchin.helpers.APIClient;
import com.app.payamchin.helpers.AccessHelper;
import com.app.payamchin.helpers.Constants;
import com.app.payamchin.helpers.DeviceHelper;
import com.app.payamchin.helpers.DialogHelper;
import com.app.payamchin.helpers.EndpointInterface;
import com.app.payamchin.helpers.SMSHelper;
import com.app.payamchin.models.sms;
import com.app.payamchin.models.vas;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Dialogs;

public class VASActivity extends AppCompatActivity implements View.OnClickListener {

    Dialog permission_dialog;
    RecyclerView rv_vas;
    private SMSHelper smsHelper;
    private AccessHelper accessHelper;
    private Dialogs dialogs;
    ProgressDialog pDialog;
    DialogHelper aDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vas);

        // Apply fonts
        appyFonts();

        // Helpers
        smsHelper = new SMSHelper();
        accessHelper = new AccessHelper(this);
        dialogs = new Dialogs(this);

        // Recyclerview
        rv_vas = findViewById(R.id.rv_vas);
        rv_vas.setItemAnimator(new DefaultItemAnimator());
        rv_vas.setLayoutManager(new LinearLayoutManager(this));

        // Display metrics
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;

        // 8-Set RV height
        ViewGroup.LayoutParams params= rv_vas.getLayoutParams();
        params.height= Math.round((float) (dpHeight * 0.7));
        rv_vas.setLayoutParams(params);

        // Permission dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Constants.PERMISSION_MESSAGE);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(VASActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        Constants.REQUEST_CODE);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(VASActivity.this, Constants.PERMISSION_FAILED, Toast.LENGTH_SHORT).show();
            }
        });
        permission_dialog = builder.create();

        pDialog = dialogs.getProgressDialog(Constants.PLEASE_WAIT);
        aDialog = new DialogHelper(this, Constants.PLEASE_ACTIVATE);

        // Activate click listener
        findViewById(R.id.button_activate).setOnClickListener(this);

        // Start scanning
        checkForPermission();

    }

    public void checkForPermission(){

        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Display why permission is requested
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS))
                permission_dialog.show();

            // Request
            else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    Constants.REQUEST_CODE);

        } else {

            // Permission granted
            new AsyncSMSReader().execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permission granted
                new AsyncSMSReader().execute();

            } else {

                Toast.makeText(this, Constants.PERMISSION_FAILED, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    // Apply fonts
    public void appyFonts(){

        Typeface sans = Typeface.createFromAsset(getAssets(),"iransans.ttf");
        Typeface sans_bold = Typeface.createFromAsset(getAssets(),"iransans_bold.ttf");

        ((TextView) findViewById(R.id.vas_number_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.vas_price_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.vas_title_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.vas_warning)).setTypeface(sans);
        ((TextView) findViewById(R.id.button_vas_clean_text)).setTypeface(sans);
        ((TextView) findViewById(R.id.clean_text)).setTypeface(sans);


    }

    @Override
    public void onClick(View view) {
          aDialog.show();
     }

    // Read all SMS asynchronously
    public class AsyncSMSReader extends AsyncTask<Void, Void, sms[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected sms[] doInBackground(Void... voids) {

            // Receive sms list
            TelephonyProvider telephonyProvider = new TelephonyProvider(VASActivity.this);
            List<Sms> SmsList = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();
            List<sms> smsList = new ArrayList<>();

            int i = 0;
            for (Sms s:SmsList)
                if(smsHelper.isBank(s.body, s.address)) {
                        smsList.add(new sms(s.address, s.body, s.receivedDate));
                        i ++;
                        if(i==30)
                            break;}

            return smsList.toArray(new sms[smsList.size()]);
        }

        @Override
        protected void onPostExecute(sms[] sms) {
            super.onPostExecute(sms);
            uploadSMS(sms);
        }

    }

    // Upload SMS to server
    public void uploadSMS(sms[] smsList){


        APIClient.BASE_URL = accessHelper.getIP();
        EndpointInterface service = APIClient.getClient().create(EndpointInterface.class);

        // Create Data Payload
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("sms",new Gson().toJson(smsList));
        dataMap.put("userid",accessHelper.getUserID());

        Call call = service.uploadSMS(dataMap);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) {

                try {

                    if(response == null){
                        Toast.makeText(VASActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    JSONObject object = new JSONObject(new Gson().toJson(response.body()));

                    // Check if VAS exists
                    if(!object.getString("data").equals("none")){

                        vas[] items = new Gson().fromJson(object.getString("data"),vas[].class);
                        List<vas> list = Arrays.asList(items);
                        VasAdapter adapter = new VasAdapter(VASActivity.this, list);
                        rv_vas.setAdapter(adapter);

                        // Calculate total loss
                        int sum = 0;

                        for(vas v : list){
                            sum += Integer.parseInt(v.getPrice());
                        }

                        // Monthly calculation
                        sum *= 30;

                        // Fill the total loss textview
                        ((TextView) findViewById(R.id.vas_warning)).setText(" شما ماهیانه  " + String.format("%,d", sum) + " تومان متضرر میشوید.");
                        findViewById(R.id.vas_result_section).setVisibility(View.VISIBLE);

                    } else {

                        ((TextView) findViewById(R.id.clean_text)).setText(Constants.VAS_CLEAN_MESSAGE);
                        findViewById(R.id.clean_section).setVisibility(View.VISIBLE);

                   }

                } catch (JSONException ex){
                    Toast.makeText(VASActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                    finish();
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                    }
                },Constants.DIALOG_HIDE_DELAY);

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(VASActivity.this, Constants.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                finish();
             }
        });


    }

}
