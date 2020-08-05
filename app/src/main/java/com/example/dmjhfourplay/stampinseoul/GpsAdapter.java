package com.example.dmjhfourplay.stampinseoul;


import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


    //================ Gps Activity 어댑터 클래스 ===================//

public class GpsAdapter extends RecyclerView.Adapter<GpsAdapter.CustomViewHolder> {

    private int layout;
    private ArrayList<ThemeData> list;

    public GpsAdapter(int layout, ArrayList<ThemeData> list) {
        this.layout = layout;
        this.list = list;
    }

    @NonNull
    @Override
    public GpsAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GpsAdapter.CustomViewHolder holder, int position) {
        holder.txtName.setText(list.get(position).getTitle());
        holder.txtContent.setText(list.get(position).getAddr());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (list != null) ? (list.size()) : (0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imaProfile;
        public TextView txtName;
        public TextView txtContent;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.fTxtName);
            txtContent = itemView.findViewById(R.id.txtContent);
        }
    }

}