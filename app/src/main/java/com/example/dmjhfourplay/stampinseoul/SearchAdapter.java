package com.example.dmjhfourplay.stampinseoul;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import xyz.hanks.library.bang.SmallBangView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context context;
    ArrayList<ThemeData> list;
    int layout;

    View view;
    View viewDialog;

    static final String KEY = "lHgLEEta%2BrgVVBrz0a5LWCybmPuBiL4Hok1S%2FMrUxkdv0dhe0B6xRnMflYD4JGvLZ96jbaDWDomL8oQOL%2BF0NQ%3D%3D";
    static final String appName = "StampInSeoul";

    ThemeData detailThemeData = new ThemeData();
    RequestQueue queue;

    public SearchAdapter(Context context, ArrayList<ThemeData> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout,viewGroup,false);
        SearchAdapter.MyViewHolder viewHolder = new SearchAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder myViewHolder, int position) {
        myViewHolder.txtView.setText(list.get(position).getTitle());
        Glide.with(context).load(list.get(position).getFirstImage()).into(myViewHolder.imgView);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtView;
        public ImageView imgView;

        public SmallBangView Like_heart;
        public ImageView imageHeart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.txtView);
            imgView = itemView.findViewById(R.id.imgView);

            Like_heart = itemView.findViewById(R.id.like_heart);
            imageHeart = itemView.findViewById(R.id.imageHeart);
        }
    }
}
