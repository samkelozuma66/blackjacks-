package com.example.blackjacks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class checkpoinAdapter extends RecyclerView.Adapter<checkpoinAdapter.MyViewHolder> {
    private Context context;
    private LayoutInflater inflater;

    JSONArray cpList;
    JSONObject cpArray;
    Drawable check;
    Drawable closed;


    public checkpoinAdapter(Context context,JSONArray cpList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.cpList = cpList;



        closed = context.getResources().getDrawable(R.drawable.ic_closed_foreground);
        check  = context.getResources().getDrawable(R.drawable.ic_check_foreground);
        //this.<your string arraylist>=<your string arraylist>;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.checkpoint_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        try {
            cpArray  = cpList.getJSONObject(position);
             holder.cpCode.setText(cpArray.getString("cp_code"));
            String data = cpArray.getString("data");
            JSONArray jarr =  new JSONArray(data);
            if(jarr.getString(0).equals("yes"))
            {
                holder.cp1.setImageDrawable(check);
            }
            if(jarr.getString(1).equals("yes"))
            {
                holder.cp2.setImageDrawable(check);
            }
            if(jarr.getString(2).equals("yes"))
            {
                holder.cp3.setImageDrawable(check);
            }
            if(jarr.getString(3).equals("yes"))
            {
                holder.cp4.setImageDrawable(check);
            }
            if(jarr.getString(4).equals("yes"))
            {
                holder.cp5.setImageDrawable(check);
            }
            if(jarr.getString(5).equals("yes"))
            {
                holder.cp6.setImageDrawable(check);
            }
            if(jarr.getString(6).equals("yes"))
            {
                holder.cp7.setImageDrawable(check);
            }
            if(jarr.getString(7).equals("yes"))
            {
                holder.cp8.setImageDrawable(check);
            }
            if(jarr.getString(8).equals("yes"))
            {
                holder.cp9.setImageDrawable(check);
            }
            if(jarr.getString(9).equals("yes"))
            {
                holder.cp10.setImageDrawable(check);
            }
            if(jarr.getString(10).equals("yes"))
            {
                holder.cp11.setImageDrawable(check);
            }
            if(jarr.getString(11).equals("yes"))
            {
                holder.cp12.setImageDrawable(check);
            }
            if(jarr.getString(12).equals("yes"))
            {
                holder.cp13.setImageDrawable(check);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return cpList.length();
    }


    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView cpCode;
        ImageView cp1;
        ImageView cp2;
        ImageView cp3;
        ImageView cp4;
        ImageView cp5;
        ImageView cp6;
        ImageView cp7;
        ImageView cp8;
        ImageView cp9;
        ImageView cp10;
        ImageView cp11;
        ImageView cp12;
        ImageView cp13;

        public MyViewHolder(View itemView) {
            //super();

            super(itemView);
            cpCode            = (TextView)itemView.findViewById(R.id.cp_code);
            cp1               = (ImageView) itemView.findViewById(R.id.cp1);
            cp2               = (ImageView) itemView.findViewById(R.id.cp2);
            cp3               = (ImageView) itemView.findViewById(R.id.cp3);
            cp4               = (ImageView) itemView.findViewById(R.id.cp4);
            cp5               = (ImageView) itemView.findViewById(R.id.cp5);
            cp6               = (ImageView) itemView.findViewById(R.id.cp6);
            cp7               = (ImageView) itemView.findViewById(R.id.cp7);
            cp8               = (ImageView) itemView.findViewById(R.id.cp8);
            cp9               = (ImageView) itemView.findViewById(R.id.cp9);
            cp10               = (ImageView) itemView.findViewById(R.id.cp10);
            cp11               = (ImageView) itemView.findViewById(R.id.cp11);
            cp12               = (ImageView) itemView.findViewById(R.id.cp12);
            cp13               = (ImageView) itemView.findViewById(R.id.cp13);

            //mediCenterName  = (TextView)itemView.findViewById(R.id.MediCenterName);
            //reason          = (TextView)itemView.findViewById(R.id.reason);
            //status          = (TextView)itemView.findViewById(R.id.status);

            //serial_number = (TextView)itemView.findViewById(R.id.serialNo_CL);
        }
    }
}
