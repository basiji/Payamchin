package com.app.payamchin.ui;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Handler;
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
import com.app.payamchin.adapters.VirusAdapter;
import com.app.payamchin.helpers.APIClient;
import com.app.payamchin.helpers.AccessHelper;
import com.app.payamchin.helpers.Constants;
import com.app.payamchin.helpers.DeviceHelper;
import com.app.payamchin.helpers.DialogHelper;
import com.app.payamchin.helpers.EndpointInterface;
import com.app.payamchin.models.virus;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.Dialogs;

public class VirusActivity extends AppCompatActivity implements View.OnClickListener {

    // Global UI elements
    ProgressDialog pDialog;
    RecyclerView rv_virus;

    // Global helpers
    private AccessHelper accessHelper;
    private Dialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virus);

        // Helpers
        accessHelper = new AccessHelper(this);
        dialogs = new Dialogs(this);

        // Apply fonts
        applyFonts();

        rv_virus = findViewById(R.id.rv_virus);
        rv_virus.setItemAnimator(new DefaultItemAnimator());
        rv_virus.setLayoutManager(new LinearLayoutManager(this));

        // 7- Get screen dimensions
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;

        // 8-Set RV height
        ViewGroup.LayoutParams params= rv_virus.getLayoutParams();
        params.height= Math.round((float) (dpHeight * 0.7));
        rv_virus.setLayoutParams(params);

        pDialog = dialogs.getProgressDialog(Constants.PLEASE_WAIT);

        // Activate click listener
        findViewById(R.id.button_activate).setOnClickListener(this);

        // Start scanning
        virusScanner();

    }


    public void applyFonts(){

        Typeface sans_bold = Typeface.createFromAsset(getAssets(),"iransans_bold.ttf");
        Typeface sans = Typeface.createFromAsset(getAssets(),"iransans.ttf");

        ((TextView) findViewById(R.id.virus_path_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.virus_type_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.virus_danger_header)).setTypeface(sans_bold);
        ((TextView) findViewById(R.id.button_virus_clean_text)).setTypeface(sans);
        ((TextView) findViewById(R.id.virus_warning)).setTypeface(sans);
        ((TextView) findViewById(R.id.clean_text)).setTypeface(sans);


    }

    // Scan device viruses
    public void virusScanner(){

        // Show progress dialog
        pDialog.show();

        // API Payload
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("userid",accessHelper.getUserID());

        APIClient.BASE_URL = accessHelper.getIP();
        EndpointInterface service = APIClient.getClient().create(EndpointInterface.class);

        Call call = service.getVirus(dataMap);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if(response == null){
                    Toast.makeText(VirusActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                    finish();
                }

                try {

                    JSONObject object = new JSONObject(new Gson().toJson(response.body()));

                    // If virus detected
                    if(!object.getString("data").equals("none")){


                        virus[] items = new Gson().fromJson(object.getString("data"), virus[].class);
                        List<virus> list = Arrays.asList(items);

                        // Set adapter
                        VirusAdapter adapter = new VirusAdapter(VirusActivity.this, list);
                        rv_virus.setAdapter(adapter);

                        // Display result section
                        findViewById(R.id.virus_result_section).setVisibility(View.VISIBLE);

                    } else {
                        ((TextView) findViewById(R.id.clean_text)).setText(Constants.VIRUS_CLEAN_MESSAGE);
                        findViewById(R.id.clean_section).setVisibility(View.VISIBLE);
                    }


                    // Hide progress dialog
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismiss();
                        }
                    },Constants.DIALOG_HIDE_DELAY);

                } catch (JSONException ex) {
                    Toast.makeText(VirusActivity.this, Constants.UNKNOWN_ERROR, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(VirusActivity.this, Constants.SERVER_ERROR, Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }

    @Override
    public void onClick(View view) {
        new DialogHelper(this, Constants.PLEASE_ACTIVATE).show();
    }
}
