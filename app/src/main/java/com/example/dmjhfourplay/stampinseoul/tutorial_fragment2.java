package com.example.dmjhfourplay.stampinseoul;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

    //듀토리얼 Fragment2

public class tutorial_fragment2 extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //메인 액티비티의 setContentView와 같은 기능
        view = inflater.inflate(R.layout.tutorial_frag2,container,false);

        //하드웨어 가속을 사용해서 부드럽게 스와이프가 되도록함
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        return view;
    }
}
