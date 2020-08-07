package com.example.dmjhfourplay.stampinseoul;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class tutorial_fragment4 extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //메인 액티비티의 setContentView와 같은 기능
        view = inflater.inflate(R.layout.tutorial_frag4,container,false);

        //하드웨어 가속을 사용해서 부드럽게 스와이프가 되도록함
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        return view;
    }
}
