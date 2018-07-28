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
import com.app.payamchin.models.vas;

import java.lang.reflect.Type;
import java.util.List;

public class VasAdapter extends RecyclerView.Adapter<VasAdapter.MyViewHolder> {

    private Context context;
    private List<vas> items;

    public VasAdapter(Context context, List<vas> items){
        this.context = context;
        this.items = items;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.vas_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView title, number, price;
        private Typeface sans = Typeface.createFromAsset(context.getAssets(),"iransans.ttf");

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.vas_title);
            number = itemView.findViewById(R.id.vas_number);
            price = itemView.findViewById(R.id.vas_price);

            // Apply Fonts
            title.setTypeface(sans);
            number.setTypeface(sans);
            price.setTypeface(sans);
        }

        public void bind(vas v){
            title.setText(v.getTitle());
            number.setText(v.getNumber());
            price.setText(v.getPrice() + " تومان");
        }
    }
}
