package com.app.payamchin.ui;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.payamchin.R;
import com.app.payamchin.helpers.AccessHelper;
import com.app.payamchin.helpers.Constants;
import com.app.payamchin.helpers.NetworkHelper;

import im.delight.android.webview.AdvancedWebView;

public class PaymentActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private Typeface sans_light, sans_regular, sans_bold;
    private AlertDialog exit_dialog;
    AccessHelper accessHelper;
    private AdvancedWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        accessHelper = new AccessHelper(this);

        // Create Fonts
        sans_light = Typeface.createFromAsset(getAssets(),"iransans_light.ttf");
        sans_regular = Typeface.createFromAsset(getAssets(),"iransans.ttf");
        sans_bold = Typeface.createFromAsset(getAssets(),"iransans_bold.ttf");

        // Webview
        webView = findViewById(R.id.webView);
        webView.setListener(this, this);
        webView.loadUrl(accessHelper.getGatewayIP() + "/?userid=" + accessHelper.getUserID(), true);
        webView.setCookiesEnabled(true);
        webView.setDesktopMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);


        applyFonts();


        // Create https certificate exit_dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Constants.ssl_certificate);
        builder.setPositiveButton("خُب", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog ssl_dialog = builder.create();
        ssl_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                TextView message = ssl_dialog.findViewById(android.R.id.message);
                message.setTypeface(sans_light);
                message.setTextSize(13);
                Button ok = ssl_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setTypeface(sans_regular);
            }
        });

        // Apply click listener for warning close icon
        findViewById(R.id.warning_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.warning_bar).setVisibility(View.GONE);
            }
        });

        // Apply click listener for https (icon and text)
        findViewById(R.id.lock_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssl_dialog.show();
            }
        });

        findViewById(R.id.https_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssl_dialog.show();
            }
        });

        // Create exit warning exit_dialog
        exit_dialog = prepareDialog();

        // Wait for warning bar to dismiss
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.warning_bar).setVisibility(View.GONE);
            }
        },2000);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit_dialog.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public AlertDialog prepareDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("مطمئنید میخواهید صفحه پرداخت را در حین عملیات ترک کنید؟");
        builder.setNegativeButton("خیر", null);
        builder.setPositiveButton("بله", null);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                Button yes = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button no = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                TextView message = dialog.findViewById(android.R.id.message);

                // Apply Fonts
                yes.setTypeface(sans_regular);
                no.setTypeface(sans_regular);
                yes.setTextColor(getResources().getColor(R.color.dark_green));
                no.setTextColor(getResources().getColor(R.color.dark_green));

                // Buttons layout (left aligned)
                LinearLayout.LayoutParams LL = (LinearLayout.LayoutParams) yes.getLayoutParams();
                LL.gravity = Gravity.LEFT;
                yes.setLayoutParams(LL);
                no.setLayoutParams(LL);

                message.setTypeface(sans_light);
                message.setTextSize(13F);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

            }
        });

        return dialog;
    }

    public void applyFonts(){
        // Apply Fonts
        ((TextView) findViewById(R.id.https_text)).setTypeface(sans_regular);
        ((TextView) findViewById(R.id.url_text)).setTypeface(sans_light);
        ((TextView) findViewById(R.id.warning_text)).setTypeface(sans_light);
        ((TextView) findViewById(R.id.domain_text)).setTypeface(sans_light);

    }

    public void loadErrorPage(WebView webview){
        if(webview!=null){
            String htmlData ="<html><body><div align=\"center\" >\"This is the description for the load fail : </div></body>";
            webview.loadUrl("about:blank");
            webview.loadDataWithBaseURL(null,htmlData, "text/html", "UTF-8",null);
            webview.invalidate();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {}

    @Override
    public void onPageFinished(String url) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        webView.loadHtml("<html></html>");
        if(String.valueOf(errorCode).equals("-10")){
            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
            intent.putExtra("result",failingUrl.split("/")[3]);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) { }
}
