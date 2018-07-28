package com.app.payamchin.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.payamchin.R;
import com.app.payamchin.models.ad;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.MyViewHolder>{

    private ad[] ads;
    private Context context;

    public AdAdapter(Context context, ad[] ads){
        this.context = context;
        this.ads = ads;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ad_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(ads[position]);
    }

    @Override
    public int getItemCount() {
        return ads.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView number, keyword, times;
        private Typeface sans = Typeface.createFromAsset(context.getAssets(),"iransans.ttf");

        public MyViewHolder(View itemView) {
            super(itemView);

            // Get UI elements
            number = itemView.findViewById(R.id.ad_number);
            keyword = itemView.findViewById(R.id.ad_keyword);
            times = itemView.findViewById(R.id.ad_times);

            // Apply fonts
            number.setTypeface(sans);
            keyword.setTypeface(sans);
            times.setTypeface(sans);

        }

        public void bind(ad ad){

            number.setText(ad.getNumber());
            keyword.setText(ad.getKeyword());
            times.setText(String.valueOf(ad.getTimes()));

        }
    }
}