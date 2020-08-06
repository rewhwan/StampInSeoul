package com.example.dmjhfourplay.stampinseoul;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

//================ 내 정보 => 1번 Fragment => GpsActivity ===================//

public class GpsActivity extends Fragment implements View.OnClickListener,View.OnTouchListener {

    LocationManager locManager; //위치정보 관련
    AlertReceiver receiver; //GPS 값을 받는 브로드캐스트 리시버 관련
    TextView locationText;
    PendingIntent proximityIntent; //등록버튼 관련
    
    Toast mToast; // 토스트 메시지 관련

    // GPS 위치 관련 변수
    boolean isPermitted = false;
    boolean isLocRequested = false;
    boolean isAlertRegistered = false;

    // 접근위치 관련 상수
    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    // == LocationListener 관련 변수
    boolean win = false;
    private boolean gpsTest = false;
    private float min = 300.0f;

    // == 등록버튼, 해제버튼
    Button alert, alert_release;

    // == recyclerView.addOnItemTouchListener() 관련 변수
    double lastlat = 0.0;
    double lastlng = 0.0;

    // == 리사이클러뷰
    private ArrayList<ThemeData> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GpsAdapter gpsAdapter;
    private View view;

    ImageView imgGpsPicture;

    // == 플로팅 버튼, 드로어
    private Animation fab_open, fab_close;

    private FloatingActionButton fab, fab1;
    private DrawerLayout dl;
    private ConstraintLayout drawer;

    int[] img = {R.drawable.gps_back1, R.drawable.gps_back2, R.drawable.gps_back3, R.drawable.gps_back4, R.drawable.gps_back5 };

    // == 로딩 애니메이션
    private GpsAnimationDialog gpsAnimationDialog;

    // == 애니메이션 관련
    LottieAnimationView animationView1 = null;
    LottieAnimationView animationView2 = null;
    LottieAnimationView animationView3 = null;
    LottieAnimationView animationView4 = null;
    LottieAnimationView animationView5 = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_gps,container,false);

        //==================================== DB 관련 ===========================================//
        list.removeAll(list);
        MainActivity.db = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor;
        cursor = MainActivity.db.rawQuery("SELECT * FROM STAMP_"+LoginActivity.userId+";",null);
        if(cursor != null){
            while(cursor.moveToNext()){
                list.add(new ThemeData(cursor.getString(1),
                 cursor.getString(2),cursor.getDouble(3),
                 cursor.getDouble(4),cursor.getString(5)));
            }
        }

        //==================================== 로딩 애니메이션 ====================================//
        gpsAnimationDialog = new GpsAnimationDialog(view.getContext());
        gpsAnimationDialog.show();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gpsAnimationDialog.dismiss();
            }
        }, 2800);

        //==================================== 리사이클러뷰 =======================================//
        recyclerView = view.findViewById(R.id.recyclerView);
        imgGpsPicture = view.findViewById(R.id.imgGpsPicture);

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        gpsAdapter = new GpsAdapter(R.layout.gps_item, list);

        recyclerView.setAdapter(gpsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener(){

            @Override
            public void onClick(View view, int position) {
                lastlng = list.get(position).getMapX();
                lastlat = list.get(position).getMapY();

                Glide.with(view.getContext()).load(list.get(position).getFirstImage()).override(500,300).into(imgGpsPicture);

                dl.closeDrawer(drawer);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //===================================== GPS 관련 =========================================//
        locManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        alert = view.findViewById(R.id.alert);
        alert_release = view.findViewById(R.id.alert_release);

        requestRuntimePermission();

        try {
            if(isPermitted){
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0,gpsLocationListener);
                isLocRequested = true;
            }else{
                Toast.makeText(getContext(), "Permission 이 없습니다..", Toast.LENGTH_SHORT).show();
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }

        alert.setOnClickListener(this);
        alert_release.setOnClickListener(this);

        //=============================== 플로팅 버튼, 드로어 =====================================//
        fab = view.findViewById(R.id.fab);

        fab_open = AnimationUtils.loadAnimation(view.getContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(view.getContext(),R.anim.fab_close);

        dl = view.findViewById(R.id.dl);
        drawer = view.findViewById(R.id.drawer);
        ConstraintLayout gps_back = view.findViewById(R.id.gps_back);

        fab.setOnClickListener(this);
        drawer.setOnTouchListener(this);
        dl.setDrawerListener(listener);

        //================================ 배경 및 애니메이션 =====================================//
        Random ram = new Random();

        int num = ram.nextInt(img.length);
        gps_back.setBackgroundResource(img[num]);

        Animation ai = AnimationUtils.loadAnimation(view.getContext(),R.anim.fade_in);
        gps_back.startAnimation(ai);

        locationText = view.findViewById(R.id.location);
        locationText.setText("등록 버튼을 눌러주세요.");

        String[] s = {"red_wave.json", "blue_wave.json", "yellow_wave.json", "green_wave.json", "black_wave.json"};

        animationView1 = view.findViewById(R.id.animation_view1);
        animationView2 = view.findViewById(R.id.animation_view2);
        animationView3 = view.findViewById(R.id.animation_view3);
        animationView4 = view.findViewById(R.id.animation_view4);
        animationView5 = view.findViewById(R.id.animation_view5);

        animationView1.cancelAnimation();
        animationView1.setAnimation("black_wave.json");
        animationView1.loop(true);
        animationView1.playAnimation();

        animationView2.cancelAnimation();
        animationView2.setAnimation("red_wave.json");
        animationView2.loop(true);
        animationView2.playAnimation();

        animationView2.setVisibility(View.INVISIBLE);

        animationView3.cancelAnimation();
        animationView3.setAnimation("blue_wave.json");
        animationView3.loop(true);
        animationView3.playAnimation();

        animationView3.setVisibility(View.INVISIBLE);

        animationView4.cancelAnimation();
        animationView4.setAnimation("yellow_wave.json");
        animationView4.loop(true);
        animationView4.playAnimation();

        animationView4.setVisibility(View.INVISIBLE);

        animationView5.cancelAnimation();
        animationView5.setAnimation("green_wave.json");
        animationView5.loop(true);
        animationView5.playAnimation();

        animationView5.setVisibility(View.INVISIBLE);

        return view;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alert:
                receiver = new AlertReceiver();
                IntentFilter filter = new IntentFilter("com.example.mu338.stampinseoul");
                v.getContext().registerReceiver(receiver, filter);
                Intent intent = new Intent("com.example.mu338.stampinseoul");
                proximityIntent = PendingIntent.getBroadcast(v.getContext(),0,intent,0);
                try {
                    if(lastlat !=0.0 && lastlng !=0.0){
                        win = true;
                        locManager.addProximityAlert(lastlat,lastlng,min,-1,proximityIntent);
                        Toast.makeText(getActivity(), "GPS 기능을 시작합니다.", Toast.LENGTH_SHORT).show();
                        locationText.setText("GPS 기능이 실행되었습니다.");
                        gpsTest = true;
                    }else{
                        Toast.makeText(view.getContext(), "목적지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (SecurityException e){
                    e.printStackTrace();
                }
                isAlertRegistered = true;
                break;

            case R.id.alert_release:
                min = 300.0f;
                animationView1.setVisibility(View.VISIBLE);
                animationView2.setVisibility(View.INVISIBLE);
                animationView3.setVisibility(View.INVISIBLE);
                animationView4.setVisibility(View.INVISIBLE);
                animationView5.setVisibility(View.INVISIBLE);
                try {
                    if (isAlertRegistered){
                        locManager.removeProximityAlert(proximityIntent);
                        getActivity().unregisterReceiver(receiver);
                        isAlertRegistered = false;
                    }
                    win = false;
                    Toast.makeText(getActivity(), "GPS 기능을 해제합니다.", Toast.LENGTH_SHORT).show();
                    imgGpsPicture.setImageResource(R.drawable.a_dialog_design);
                    locationText.setText("목적지를 선택하여 등록해주세요.");
                    gpsTest = false;
                }catch (SecurityException e){
                    e.printStackTrace();
                }
                break;

            case R.id.fab:

                dl.openDrawer(drawer);
                break;

            default:break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if(isLocRequested){
                locManager.removeUpdates(gpsLocationListener);
                isLocRequested = false;
            }
            if(isAlertRegistered){
                locManager.removeProximityAlert(proximityIntent);
                getActivity().unregisterReceiver(receiver);
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    // onLocationChanged 가 변동이 있을 때 호출되는 함수
    final LocationListener gpsLocationListener = new LocationListener(){

        //단점은 움직여서 값이 변동이 되야 한다 그래야 작동한다.
        @Override
        public void onLocationChanged(Location location) {
            if(win){
                try {
                    locManager.addProximityAlert(lastlat, lastlng, min, -1, proximityIntent);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            }
            if(gpsTest){
                Toast.makeText(getContext(), "목표반경 300미터 밖에 있습니다.", Toast.LENGTH_SHORT).show();
                locationText.setText("목표반경 300미터 밖에 있었요.");
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    // GPS 권한 설정 관련
    private void requestRuntimePermission() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }else{
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,gpsLocationListener);
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,gpsLocationListener);
            isPermitted = true;
        }
    }

    // GPS 관련 함수
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isPermitted = true;
                }else{
                    isPermitted = false;
                }
                return;
            }
        }
    }

    // GPS 값을 받는 브로드캐스트 리시버 내부클래스 설계
    public class AlertReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
            if(isEntering){
                int mm = (int)min;
                switch (mm){
                    case 10:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "목적지에 도착하였습니다.", Toast.LENGTH_SHORT).show();
                        locationText.setText("목표지점에 도착했습니다.");
                        break;
                    case 20:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "목표반경 50미터 안에 들어왔습니다.", Toast.LENGTH_SHORT).show();
                        locationText.setText("목표반경 50미터 안에 들어왔어요.");
                        locManager.addProximityAlert(lastlat, lastlng, min,-1, proximityIntent);
                        min = 10.0f;
                        break;
                    case 50:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.VISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "목표반경 100미터 안에 들어왔습니다.", Toast.LENGTH_SHORT).show();
                        locationText.setText("목표반경 100미터 내에 들어왔어요.");
                        locManager.addProximityAlert(lastlat, lastlng, min, -1, proximityIntent);
                        min = 20.0f;
                        break;
                    case 100:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.VISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "목표반경 200미터 안에 들어왔습니다.", Toast.LENGTH_LONG).show();
                        locationText.setText("목표반경 200미터 내에 들어왔어요.");
                        locManager.addProximityAlert(lastlat, lastlng, min, -1, proximityIntent);
                        min = 50.0f;
                        break;
                    case 200:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.VISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "목표반경 300미터 안에 들어왔습니다.", Toast.LENGTH_LONG).show();
                        locationText.setText("목표반경 300미터 내에 들어왔어요.");
                        locManager.addProximityAlert(lastlat, lastlng, min, -1, proximityIntent);
                        min = 100.0f;
                        break;
                    case 300:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.VISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        locManager.addProximityAlert(lastlat, lastlng , min, -1, proximityIntent);
                        locationText.setText("목표 반경 300미터 근방에 접근했어요.");
                        gpsTest = false;
                        min = 200.0f;
                        break;
                }
            }else{
                int mm = (int)min;
                switch (mm){
                    case 20:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.VISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "목표반경 50미터 멀어졌습니다.", Toast.LENGTH_LONG).show();
                        locationText.setText("목표반경 50미터 내에서 벗어났어요.");
                        min = 50.0f;
                        break;
                    case 50:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.VISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "목표반경 50미터 멀어졌습니다.", Toast.LENGTH_LONG).show();
                        locationText.setText("목표반경 50미터 내에서 벗어났어요.");
                        min = 100.0f;
                        break;
                    case 100:
                        animationView1.setVisibility(View.INVISIBLE);
                        animationView2.setVisibility(View.VISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "목표반경 100미터 멀어졌습니다.", Toast.LENGTH_LONG).show();
                        locationText.setText("목표반경 100미터 내에서 벗어났어요.");
                        min = 200.0f;
                        break;
                    case 200:
                        animationView1.setVisibility(View.VISIBLE);
                        animationView2.setVisibility(View.INVISIBLE);
                        animationView3.setVisibility(View.INVISIBLE);
                        animationView4.setVisibility(View.INVISIBLE);
                        animationView5.setVisibility(View.INVISIBLE);
                        locationText.setText("목표반경 200미터 내에서 벗어났어요.");
                        gpsTest = true;
                        break;
                }
            }
        }
    };

    // DrawerLayout 열리고 닫히는 이벤트 관련 함수
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener(){

        // 슬라이딩을 시작 했을때 이벤트 발생
        @Override
        public void onDrawerSlide(@NonNull View v, float f) {

        }

        // 메뉴가 열었을때 이벤트 발생
        @Override
        public void onDrawerOpened(@NonNull View v) {

        }

        // 메뉴를 닫았을때 이벤트 발생
        @Override
        public void onDrawerClosed(@NonNull View v) {

        }

        // 메뉴바 상태가 바뀌었을때 이벤트 발생
        @Override
        public void onDrawerStateChanged(int i) {

        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
    
    public void shortMessage(String message){
        Runnable r = new Runnable(){
            @Override
            public void run() {
                Toast.makeText(getContext(), "목적지에 도착하였습니다.", Toast.LENGTH_SHORT).show();
                if(mToast != null) mToast.cancel();
                mToast=Toast.makeText(getContext(), "목적지에 도착하였습니다.", Toast.LENGTH_SHORT);
                mToast.show();
            }
        };

    }



}
