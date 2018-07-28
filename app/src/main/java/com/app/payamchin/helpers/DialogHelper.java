package com.app.payamchin.helpers;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.payamchin.R;
import com.app.payamchin.ui.PaymentActivity;

import utils.CustomTypefaceSpan;
import utils.Dialogs;

public class DialogHelper extends Dialog {

    private Activity activity;
    private LinearLayout yes;
    public ImageView no;
    private String message;
    private NetworkHelper networkHelper;
    private Dialogs dialogs;

    public DialogHelper(Activity a, String message) {
        super(a);
        this.activity = a;
        this.message = message;
        this.networkHelper = new NetworkHelper(activity);
        this.dialogs = new Dialogs(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.update_dialog);

        // Bazaar progress dialog
        final ProgressDialog pDialog = dialogs.getProgressDialog((String) Constants.CONNECTING_BAZAAR);

        // Custom font
        Typeface sans  = Typeface.createFromAsset(activity.getAssets(),"iransans.ttf");

        // Set message text
        ((TextView) findViewById(R.id.dialog_message)).setText(message);

        // Apply fonts
        ((TextView) findViewById(R.id.dialog_message)).setTypeface(sans);
        ((TextView) findViewById(R.id.dialog_download_text)).setTypeface(sans);

        yes = findViewById(R.id.btn_yes);
        no =  findViewById(R.id.btn_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Dismiss current dialog
                dismiss();

                // Open progress dialog
                pDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog.dismiss();
                        if(networkHelper.hasNetworkInterface())
                            activity.startActivity(new Intent(activity, PaymentActivity.class));
                        else
                            Toast.makeText(activity, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}

