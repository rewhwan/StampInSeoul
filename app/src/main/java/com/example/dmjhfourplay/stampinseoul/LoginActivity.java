package com.example.dmjhfourplay.stampinseoul;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;

//LoginActivity 로그인 처리 클래스

public class LoginActivity extends AppCompatActivity {

    //UI객체 변수
    private Button btnLogin;
    private LoginButton btn_kakao_login;

    //카카오 로그인에 따라서 처리를 도와준다
    private LoginSessionCallback loginSessionCallback;
    //로그인 정보를 가지고 있는 변수
    public static Long userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //QUSTNDP UI객체를 저장
        btnLogin = findViewById(R.id.btnLogin);
        btn_kakao_login = findViewById(R.id.btn_kakao_login);

        //SessionCallback 초기화
        loginSessionCallback = new LoginSessionCallback(this,getApplicationContext());
        //현재 세션에 콜백 붙임
        Session.getCurrentSession().addCallback(loginSessionCallback);
        //자동 로그인
        Session.getCurrentSession().checkAndImplicitOpen();

        //버튼 클릭 이벤트
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_kakao_login.performClick();
            }
        });
    }

    //호출한 액티비티로부터 결과를 받을때 사용
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    //액티비티 생명주기 콜백함수
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 현재 액티비티 제거시 콜백도 같이 제거
        Session.getCurrentSession().removeCallback(loginSessionCallback);
    }

}