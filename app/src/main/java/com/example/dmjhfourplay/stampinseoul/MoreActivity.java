package com.example.dmjhfourplay.stampinseoul;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;


public class MoreActivity extends Fragment {


    private Button btnLogout;
    private ImageView imgKakao;
    private TextView txtKakaoName;

    String strNickname, strProfile;
    Bitmap bitmap;

    private RecyclerView recyclerView;
    private ExpandableListView eListView;
    private View view;

    // ArrayList<String>으로 새로운 ArrayList 객체 groupList를 만든다.
    private ArrayList<String> groupList = new ArrayList<>();

    // ArrayList<ArrayList<String>>으로 새로운 ArrayList 객체 childList를 만든다.
    private ArrayList<ArrayList<String>> childList = new ArrayList<>();

    // ArrayList<String>으로 새로운 ArrayList 객체 childListContent 1~5를 만든다.
    private ArrayList<String> childListContent1 = new ArrayList<>();
    private ArrayList<String> childListContent4 = new ArrayList<>();
    private ArrayList<String> childListContent5 = new ArrayList<>();

    private DBHelper dbHelper;
    String loginName;
    String loginImage;


    // View를 만든다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 부분 화면으로 넣을 객체를 가져온다.
        view = inflater.inflate(R.layout.activity_more, container, false);

        // activity_more에 있는 UI ID 찾기.
        eListView = (ExpandableListView) view.findViewById(R.id.eListView);
        btnLogout = (Button) view.findViewById(R.id.btnLogout);


        groupList.removeAll(groupList); // groupList에 있는 데이터 정보를 모두 지운다.

        groupList.add("카카오 로그인 정보");
        groupList.add("App 정보");
        groupList.add("개발자 정보");

        dbHelper = DBHelper.getInstance(getContext());
        Cursor cursor = dbHelper.getUserData();
        while (cursor.moveToNext()) {
            if (cursor != null) {
                loginName = cursor.getString(1);
                loginImage = cursor.getString(2);
            }
        }

        childListContent1.add("카카오 닉네임 : " + loginName +"\n카카오 고유번호 : " + LoginSessionCallback.userId);
        childListContent4.add("AppName : Stamp In Seoul\n");
        childListContent5.add("Team Name : 김오박이");
        childListContent5.add("김진혁");
        childListContent5.add("오선환");
        childListContent5.add("박다니엘");
        childListContent5.add("이민혁");

        childList.add(childListContent1);
        childList.add(childListContent4);
        childList.add(childListContent5);

        // ExpendableListView에 어댑터 내용을 적용한다.
        eListView.setAdapter(new MoreAdapter(view.getContext(), groupList, childList));

        // 그룹 클릭시 이벤트 등록.
        eListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                return false;
            }
        });

        // 자식 클릭시 이벤트 등록.
        eListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                return false;
            }
        });

        // 로그아웃 클릭 시 이벤트 등록.
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout();
//                Intent intent = new Intent(view.getContext(), LoginActivity.class); // 로그인 화면으로 이동.
                Toast.makeText(view.getContext(), "로그아웃 합니다.", Toast.LENGTH_SHORT).show(); // 토스트 메시지 띄우기.
            }
        });

        return view;

    }// end of onCreateView

    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {

                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
    }

}// end of class
