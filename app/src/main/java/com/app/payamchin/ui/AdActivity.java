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
import com.app.payamchin.adapters.AdAdapter;
import com.app.payamchin.helpers.AccessHelper;
import com.app.payamchin.helpers.Constants;
import com.app.payamchin.helpers.DialogHelper;
import com.app.payamchin.helpers.NetworkHelper;
import com.app.payamchin.helpers.SMSHelper;
import com.app.payamchin.models.ad;

import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.telephony.Sms;
import me.everything.providers.android.telephony.TelephonyProvider;
import utils.Dialogs;

public class AdActivity extends AppCompatActivity implements View.OnClickListener {

    private SMSHelper smsHelper;
    private AccessHelper accessHelper;
    private Dialogs dialogs;

    ProgressDialog pDialog;
    RecyclerView rv_ad;
    Dialog permission_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        // Helpers
        smsHelper = new SMSHelper();
        accessHelper = new AccessHelper(this);
        dialogs = new Dialogs(this);

        // 5- Apply fonts
        applyFonts();

        // Recyclerview
        rv_ad = findViewById(R.id.rv_ad);
        rv_ad.setItemAnimator(new DefaultItemAnimator());
        rv_ad.setLayoutManager(new LinearLayoutManager(this));

        // Permission dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Constants.PERMISSION_MESSAGE);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(AdActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        Constants.REQUEST_CODE);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(AdActivity.this, Constants.PERMISSION_FAILED, Toast.LENGTH_SHORT).show();
            }
        });
        permission_dialog = builder.create();

        pDialog = dialogs.getProgressDialog(Constants.PLEASE_WAIT);

        // 7- Get screen dimensions
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;

        // 8-Set RV height
        ViewGroup.LayoutParams params= rv_ad.getLayoutParams();
        params.height= Math.round((float) (dpHeight * 0.7));
        rv_ad.setLayoutParams(params);

        // Activate click listener
        findViewById(R.id.button_activate).setOnClickListener(this);

        // Start scanning
        checkForPermission();

    }

    @Override
    public void onClick(View view) {
        new DialogHelper(this, Constants.PLEASE_ACTIVATE).show();
    }

    // Read AD SMS asynchronously
    public class AsyncAdReader extends AsyncTask<Void, Void, ad[]> {

        private int total = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected ad[] doInBackground(Void... voids) {

            // Receive sms list
            TelephonyProvider telephonyProvider = new TelephonyProvider(AdActivity.this);
            List<Sms> SmsList = telephonyProvider.getSms(TelephonyProvider.Filter.INBOX).getList();

            List<ad> ads = new ArrayList<>();
            List<String> address = new ArrayList<>();
            String keyword;

            for (Sms s:SmsList){
                keyword = smsHelper.isAd(s.body);
                if(!keyword.equals("")){
                    this.total ++;
                    if(!address.contains(s.address)){
                            ads.add(new ad(s.address, keyword, smsHelper.getTimes(s.address, SmsList)));
                            address.add(s.address);
                    }
                }

            }

            if(accessHelper.isActive())
                ads.clear();

            return ads.toArray(new ad[ads.size()]);

        }

        @Override
        protected void onPostExecute(final ad[] ads) {

            super.onPostExecute(ads);

            if(ads.length > 0){
                AdAdapter adapter = new AdAdapter(AdActivity.this, ads);
                rv_ad.setAdapter(adapter);
                ((TextView) findViewById(R.id.ad_warning)).setText(total + " پیامک تبلیغاتی شناسایی شد. ");
                findViewById(R.id.ad_result_section).setVisibility(View.VISIBLE);
            }
            else {
                ((TextView) findViewById(R.id.clean_text)).setText(Constants.AD_CLEAN_MESSAGE);
                findViewById(R.id.clean_section).setVisibility(View.VISIBLE);
            }

            // Display Ad section
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.dismiss();
                }
            },Constants.DIALOG_HIDE_DELAY);

        }
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
            else ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    Constants.REQUEST_CODE);

        } else {

            // Permission granted
            new AsyncAdReader().execute();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permission granted
                new AsyncAdReader().execute();

            } else {

                Toast.makeText(this, Constants.PERMISSION_FAILED, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Appy fonts
    public void applyFonts(){

        Typeface sans_bold = Typeface.createFromAsset(getAssets(),"iransans_bold.ttf");
        Typeface sans = Typeface.createFromAsset(getAssets(),"iransans.ttf");

        ((TextView) findViewById(R.id.ad_number_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.ad_keyword_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.ad_times_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.button_ad_clean_text)).setTypeface(sans);
        ((TextView) findViewById(R.id.ad_warning)).setTypeface(sans);
        ((TextView) findViewById(R.id.clean_text)).setTypeface(sans);



    }

}
