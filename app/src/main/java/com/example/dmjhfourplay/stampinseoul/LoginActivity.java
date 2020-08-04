package com.example.dmjhfourplay.stampinseoul;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

//LoginActivity 로그인 처리 클래스

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private LoginButton btn_kakao_login;

    private SessionCallback sessionCallback;

    public static Long userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);

        btn_kakao_login = findViewById(R.id.btn_kakao_login);

        //카카오 로그인
        sessionCallback = new SessionCallback();                    //SessionCallback 초기화
        Session.getCurrentSession().addCallback(sessionCallback);   //현재 세션에 콜백 붙임

        Session.getCurrentSession().checkAndImplicitOpen();         //자동 로그인

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_kakao_login.performClick();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 현재 액티비티 제거시 콜백도 같이 제거
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {

        //로그인 세션이 열렸을때
        @Override
        public void onSessionOpened() {

            //유저 정보를 받아오는 함수 이름 me
            UserManagement.getInstance().me(new MeV2ResponseCallback() {

                //로그인에 실패했을 때
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(LoginActivity.this, "네트워크 연결이 불안정합니다.\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 도중 오류가 발생했습니다.\n"+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                //로그인 도중 세션이 비정상적인 이유로 닫혔을 때.
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(LoginActivity.this, "세션이 닫혔습니다. 다시시도해 주세요. "+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                //로그인에 성공했을 때. MeV2Response 객체 넘어오는데, 로그인한 유저의 정보를 담고 있는 중요객체.
                @Override
                public void onSuccess(MeV2Response result) {

                    try {

                        MainActivity.dbHelper = new DBHelper(getApplicationContext());

                        userId = result.getId();

                        MainActivity.db = MainActivity.dbHelper.getWritableDatabase();


                        String insertUserInfo = "INSERT or REPLACE INTO userTBL values('"
                                + result.getId() + "' , '"
                                + result.getNickname() + "' , '"
                                + result.getProfileImagePath() + "');";

                        //+ " SELECT * FROM userTBL WHERE NOT EXISTS (SELECT userId FROM userTBL WHERE userID='" + result.getId() + "') LIMIT 1;";

                        MainActivity.db.execSQL(insertUserInfo);

                        // 새로 로그인한 유저의 전용 테이블 2개 생성
                        String createZzimTBL = "CREATE TABLE IF NOT EXISTS ZZIM_" + result.getId() + "("
                                + "title TEXT PRIMARY KEY, "
                                + "addr TEXT, "
                                + "mapX REAL, "
                                + "mapY REAL, "
                                + "firstImage TEXT); ";

                        MainActivity.db.execSQL(createZzimTBL);

                        String createStampTBL = "CREATE TABLE IF NOT EXISTS STAMP_" + result.getId() + "("
                                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + "title TEXT, "
                                + "addr TEXT, "
                                + "mapX REAL, "
                                + "mapY REAL, "
                                + "firstImage TEXT, "
                                + "picture TEXT, "
                                + "content_pola TEXT, "
                                + "content_title TEXT, "
                                + "contents TEXT, "
                                + "complete INTEGER);";


                        MainActivity.db.execSQL(createStampTBL);

                    } catch (SQLiteConstraintException e) {

                    }

                    userId = result.getId();

                    Log.d("TAG", userId.toString());

                    Intent intent = new Intent(getApplicationContext(), ThemeActivity.class);

                    intent.putExtra("name", result.getNickname()); // 유저 닉네임
                    intent.putExtra("profile", result.getProfileImagePath()); // 카카오톡 프로필 이미지
                    intent.putExtra("id", result.getId());

                    startActivity(intent);
                    finish();
                }
            });
        }

        //로그인 세션이 열리지 않았을때
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+ exception.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}