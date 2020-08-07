package com.example.dmjhfourplay.stampinseoul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomMenuActivity extends AppCompatActivity {
    private BottomNavigationView bottomMenu;
    private FrameLayout frameLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private MapLocateActivity mapLocateActivity;
    private GpsActivity gpsActivity;
    private AlbumActivity albumActivity;
    private MoreActivity moreActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        //객체 ID 저장하는 함수
        findViewByIdFunction();

        //엑티비티 등록하는 함수
        newActivityFunction();

        //Fragment 순서를 정해주는 함수
        setChangeFragment(0);

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_gps : setChangeFragment(0); break;
                    case R.id.action_map : setChangeFragment(1); break;
                    case R.id.action_review : setChangeFragment(2); break;
                    case R.id.action_more : setChangeFragment(3); break;
                }
                return true;
            }
        });
    }

    private void newActivityFunction() {
        mapLocateActivity = new MapLocateActivity();
        gpsActivity = new GpsActivity();
        albumActivity = new AlbumActivity();
        moreActivity = new MoreActivity();
    }

    private void findViewByIdFunction() {
        bottomMenu = findViewById(R.id.bottomMenu);
        frameLayout = findViewById(R.id.frameLayout);
    }

    private void setChangeFragment(int position) {
        //화면을 전환하기 위해서는 매니저 필요.
        fragmentManager = getSupportFragmentManager();

        //프래그먼트 매니저 권한을 받아서 화면 체인지 하는 트렌젝션이 필요.
        fragmentTransaction = fragmentManager.beginTransaction();

        switch (position){
            case 0 : fragmentTransaction.replace(R.id.frameLayout,gpsActivity); break;
            case 1 :
                MapLocateActivity mainFragment = new MapLocateActivity();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,mainFragment,"main").commit();
            break;
            case 2 : fragmentTransaction.replace(R.id.frameLayout,albumActivity); break;
            case 3 : fragmentTransaction.replace(R.id.frameLayout,moreActivity); break;
        }
        fragmentTransaction.commit();

        return;
    }
}