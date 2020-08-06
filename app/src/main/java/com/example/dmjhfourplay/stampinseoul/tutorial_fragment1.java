package com.example.dmjhfourplay.stampinseoul;

    //듀토리얼 Fragment1

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class tutorial_fragment1 extends Fragment {

    private View view;
    private Button btnSkip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //메인 액티비티의 setContentView와 같은 기능
        view = inflater.inflate(R.layout.tutorial_frag1,container,false);

        //Skip버튼의 위젯객체와 클릭이벤트를 선언
        btnSkip = view.findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("service", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putBoolean("firstActivate",false);
                editor.commit();

                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //하드웨어 가속을 사용해서 부드럽게 스와이프가 되도록함
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ImageView marker = view.findViewById(R.id.marker);
        //gif이미지가 움직여서 보이도록 해주는 것
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(marker);
        Glide.with(getActivity()).load(R.drawable.markeranimaition).into(gifImage);
        return view;
    }
}
