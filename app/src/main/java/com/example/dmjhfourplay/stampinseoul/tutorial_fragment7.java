package com.example.dmjhfourplay.stampinseoul;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class tutorial_fragment7 extends Fragment {

    private View view;
    private Button btnStart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //메인 액티비티의 setContentView와 같은 기능
        view = inflater.inflate(R.layout.tutorial_frag7,container,false);

        //시작하기 버튼 위젯객체와 클릭이벤트를 선언
        btnStart = view.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //다음에는 듀토리얼을 보지않고 지나가도록 sharedPreference로 저장
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("service", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor =sharedPreferences.edit();
                editor.putBoolean("firstActivate",false);
                editor.commit();

                //로그인 액티비티로 이동
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //하드웨어 가속을 사용해서 부드럽게 스와이프가 되도록함
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        return view;
    }
}
