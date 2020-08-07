package com.example.dmjhfourplay.stampinseoul;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// StartActivity. 어플 시작시 로딩 제공.
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        //카카오톡 로그인에 필요한 해시키값을 보여줍니다.
        getHashKey();

        //듀토리얼을 관람한적이 있는지를 확인해줍니다.
        SharedPreferences sharedPreferences = getSharedPreferences("service", Context.MODE_PRIVATE);
        Boolean firstActivateFlag = sharedPreferences.getBoolean("firstActivate",true);

        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        //듀토리얼을 관람했는지 여부에 따라서 로그인 혹은 듀토리얼로 분기점을 나눔
        if(firstActivateFlag) {
            //듀토리얼
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }else {
            //로그인
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    // 개인 카카오 키 해시 발급 메소드
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
}