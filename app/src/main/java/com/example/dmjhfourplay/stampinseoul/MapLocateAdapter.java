package com.example.dmjhfourplay.stampinseoul;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MapLocateAdapter extends RecyclerView.Adapter<MapLocateAdapter.CustomViewHolder> {
    private int layout;
    private ArrayList<ThemeData> list;

    static int number = 0;

    public MapLocateAdapter(int layout, ArrayList<ThemeData> list) {
        this.layout = layout;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout,viewGroup,false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MapLocateAdapter.CustomViewHolder customViewHolder, final int position) {
        customViewHolder.txtName.setText(list.get(position).getTitle());
        customViewHolder.txtContent.setText(list.get(position).getAddr());

        customViewHolder.itemView.setTag(position);

        customViewHolder.imaProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //좋아요 된 장소를 버튼 클릭을 통한Google 검색기능 등록 하는 이벤트
                String str = list.get(position).getTitle();

                Uri uri =  Uri.parse("https://www.google.com/search?q="+str+"&oq="+str+"&aqs=chrome");

                Intent intent = new Intent(Intent.ACTION_VIEW,uri);

                intent.setPackage("com.android.chrome");

                view.getContext();
            }
        });

        customViewHolder.imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),CameraActivity.class);

                intent.putExtra("title",list.get(position).getTitle());

                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        //리스트에 값이 들어있을 경우 진행해라
        return (list != null) ? (list.size()) : (0);
    }

    //성능상의 향상을 위해 사용
    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public ImageView imaProfile;
        public ImageView imgPhoto;

        public TextView txtName;
        public TextView txtContent;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            imaProfile = itemView.findViewById(R.id.fImgProfile);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);

            txtName = itemView.findViewById(R.id.txtName);
            txtContent = itemView.findViewById(R.id.txtContent);
        }
    }
}
