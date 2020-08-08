package com.example.dmjhfourplay.stampinseoul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

    //카카오 로그인 세션 콜백 관련 클래스

public class LoginSessionCallback implements ISessionCallback {

    //전역변수
    private Activity activity;
    private Context context;

    //DB를 관리해주는 객체 변수
    private DBHelper dbHelper;

    //로그인 정보를 가지고 있는 변수
    public static Long userId = null;

    //Constructor
    public LoginSessionCallback(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    //로그인 세션이 열렸을때
    @Override
    public void onSessionOpened() {

        //유저 정보를 받아오는 함수 me
        UserManagement.getInstance().me(new MeV2ResponseCallback() {

            //로그인에 실패했을 때
            @Override
            public void onFailure(ErrorResult errorResult) {
                int result = errorResult.getErrorCode();

                if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                    Toast.makeText(activity, "네트워크 연결이 불안정합니다.\n다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    activity.finish();
                } else {
                    Toast.makeText(activity, "로그인 도중 오류가 발생했습니다.\n"+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            //로그인 도중 세션이 비정상적인 이유로 닫혔을 때.
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(activity, "세션이 닫혔습니다.\n다시 시도해 주세요. "+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }

            //로그인에 성공했을 때. MeV2Response 객체 넘어오는데, 로그인한 유저의 정보를 담고 있는 중요객체.
            @Override
            public void onSuccess(MeV2Response result) {

                dbHelper = DBHelper.getInstance(context);

                //로그인한 유저의 id를 변수로 저장
                userId = result.getId();

                //유저의 정보를 테이블에 저장시켜준다
                dbHelper.userAdd(result);
                // 새로 로그인한 유저의 전용 테이블 2개 생성
                dbHelper.newUserAddTBL(result);

                //카카오톡 로그인 정보를 로그로 남긴
                Log.d("LoginID", userId.toString());

                //로그인 이후 테마 액티비티로 이동
                Intent intent = new Intent(context, ThemeActivity.class);

                //테마 액티비티에 정보를 전달합니다.
                intent.putExtra("name", result.getNickname()); // 유저 닉네임
                intent.putExtra("profile", result.getProfileImagePath()); // 카카오톡 프로필 이미지
                intent.putExtra("id", result.getId()); //유저 ID정보

                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    //로그인 세션이 열리지 않았을때
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Toast.makeText(context, "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+ exception.toString(), Toast.LENGTH_SHORT).show();
    }

}
