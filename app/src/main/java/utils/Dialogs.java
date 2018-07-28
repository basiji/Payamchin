package utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import com.app.payamchin.R;
import com.app.payamchin.helpers.Constants;

public class Dialogs {

    private Context context;
    private Typeface sans;

    public Dialogs(Context context){
        this.context = context;
        sans = Typeface.createFromAsset(context.getAssets(),"iransans.ttf");
    }

    public ProgressDialog getProgressDialog(String message){
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        SpannableString ss=  new SpannableString(message);
        ss.setSpan(new RelativeSizeSpan(0.9f), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new CustomTypefaceSpan("", sans), 0, ss.length(), 0);
        pDialog.setMessage(ss);
        return pDialog;
    }

}
