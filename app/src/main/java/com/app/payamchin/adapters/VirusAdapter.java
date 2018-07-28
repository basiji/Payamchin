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
import com.app.payamchin.models.virus;

import java.util.List;

public class VirusAdapter extends RecyclerView.Adapter<VirusAdapter.MyViewHolder> {

    private Context context;
    private List<virus> items;

    public VirusAdapter (Context context, List<virus> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.virus_item,parent,false));
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

        private TextView type, danger, path;
        private Typeface sans = Typeface.createFromAsset(context.getAssets(),"iransans.ttf");

        public MyViewHolder(View itemView) {
            super(itemView);

            // Get UI Elements
            type = itemView.findViewById(R.id.virus_type);
            danger = itemView.findViewById(R.id.virus_danger);
            path = itemView.findViewById(R.id.virus_path);

            // Apply fonts
            type.setTypeface(sans);
            danger.setTypeface(sans);
            path.setTypeface(sans);

        }

        public void bind(virus v){

            type.setText(v.getType());
            danger.setText(v.getDanger());
            path.setText(v.getPath());

            danger.setTextColor(context.getResources().getColor(R.color.danger));


        }

    }


}
