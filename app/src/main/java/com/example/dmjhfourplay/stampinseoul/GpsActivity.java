package com.example.dmjhfourplay.stampinseoul;

import androidx.fragment.app.Fragment;

import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
//    AlertReceiver receiver;
    TextView locationText;

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
    private Boolean isFabOpen = false;

    private FloatingActionButton fab, fab1;
    private DrawerLayout d1;
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

                d1.closeDrawer(drawer);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //=============================== 플로팅 버튼, 드로어 =====================================//
        fab = view.findViewById(R.id.fab);

        fab_open = AnimationUtils.loadAnimation(view.getContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(view.getContext(),R.anim.fab_close);

        d1 = view.findViewById(R.id.dl);
        drawer = view.findViewById(R.id.drawer);
        ConstraintLayout gps_back = view.findViewById(R.id.gps_back);

        fab.setOnClickListener(this);
        drawer.setOnTouchListener(this);
        d1.setDrawerListener(listener);

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
    public void onClick(View view) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }


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
}
