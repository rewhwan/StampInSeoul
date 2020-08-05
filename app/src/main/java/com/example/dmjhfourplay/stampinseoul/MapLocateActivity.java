package com.example.dmjhfourplay.stampinseoul;

import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MapLocateActivity extends Fragment implements OnMapReadyCallback, View.OnTouchListener, View.OnClickListener{

    static final String TAG = "MapLocateActivity";

    boolean win = false;

    double lat = 0.0;
    double lng = 0.0;

    LocationManager locManager;
    PendingIntent proximityIntent;

    static ArrayList<ThemeData> list = new ArrayList<>();
    ArrayList<MarkerOptions> cctvList = new ArrayList<MarkerOptions>();
    ArrayList<String> check = new ArrayList<>();
    static GoogleMap googleMaps;
    static GoogleMap googleMaps2;

    private FragmentManager fragmentManager;
    private MapView mapView;

    private View view1;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MapLocateAdapter mapLocateAdapter;

    // ===== 플로팅, 드로어 버튼 변수

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    private FloatingActionButton fab, fab1, fab2;
    private DrawerLayout drawerLayout;
    private ConstraintLayout drawer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view1 = inflater.inflate(R.layout.activity_map_location,container,false);

        fragmentManager = getActivity().getFragmentManager();

        //리사이클
        recyclerView = view1.findViewById(R.id.recyclerView);

        linearLayoutManager = new LinearLayoutManager(view1.getContext());

        recyclerView.setLayoutManager(linearLayoutManager);

        listSetting();

        mapLocateAdapter = new MapLocateAdapter(R.layout.map_item,list);

        recyclerView.setAdapter(mapLocateAdapter);

        //맵
        mapView = view1.findViewById(R.id.fgGoogleMap);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //리사이클 아이템 클릭 이벤트
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                boolean tag=true;

                if(check.size() == 0){

                    check.add(list.get(position).getTitle());

                }else{

                    for( int x=0 ; x < check.size() ; x++ ){

                        if(check.get(x).equals(list.get(position).getTitle())){
                            check.remove(x);

                            //Toast.makeText(getContext(), "테스트 else", Toast.LENGTH_SHORT).show();
                            tag=false;
                            break;
                        }
                    }

                    if(tag){
                        check.add(list.get(position).getTitle());
                    }
                }

                mapView = view1.findViewById(R.id.fgGoogleMap);

                // mapView.onCreate(savedInstanceState);

                mapView.getMapAsync(MapLocateActivity.this);

                drawerLayout.closeDrawer(drawer);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fab = view1.findViewById(R.id.fab);
        fab1 = view1.findViewById(R.id.fab1);
        fab2 = view1.findViewById(R.id.fab2);

        fab_open = AnimationUtils.loadAnimation(view1.getContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(view1.getContext(),R.anim.fab_close);

        drawerLayout = view1.findViewById(R.id.drawerLayout);
        drawer = view1.findViewById(R.id.drawer);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        drawer.setOnTouchListener(this);
        drawerLayout.setDrawerListener(listener);

        return view1;
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        // 슬라이딩을 시작 했을때 이벤트 발생
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        }
        // 메뉴가 열었을때 이벤트 발생
        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
        }
        // 메뉴를 닫았을때 이벤트 발생
        @Override
        public void onDrawerClosed(@NonNull View drawerView) {
        }
        // 메뉴 바가 상태가 바뀌었을때 이벤트 발생
        @Override
        public void onDrawerStateChanged(int newState) {
        }
    };

    private void listSetting() {
        list.removeAll(list);

        MainActivity.db = MainActivity.dbHelper.getWritableDatabase();

        Cursor cursor;

        cursor = MainActivity.db.rawQuery("SELECT * FROM STAMP_"+LoginActivity.userId+";", null);

        if(cursor != null){
            while(cursor.moveToNext()){
                list.add(new ThemeData(cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4)));
            }
        }
    }

    public void anim() {

        if (isFabOpen) {

            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
        @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.fab : anim(); break;
            case R.id.fab1 :
                anim();
                drawerLayout.openDrawer(drawer);
            break;
            case R.id.fab2 :
                anim();
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(view1.getContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                }
                break;

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMaps=googleMap;

        googleMaps2 = googleMap;

        googleMaps.clear();

        if (check.size() >= 1){

            for (String x : check){

                for (ThemeData y :list){

                    if (x.equals(y.getTitle())){
                        //위치값
                        LatLng latLng = new LatLng(y.getMapY(), y.getMapX());
                        //지도에 표시 마킹

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(y.getTitle());
                        markerOptions.snippet(y.getAddr());
                        markerOptions.position(latLng);

                        googleMaps.addMarker(markerOptions);
                        googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                    }
                }
            }
        }
        if(win){

            LatLng latLng = new LatLng(lat, lng);

            Log.d(TAG, lat+" "+lng);

            MarkerOptions markerOptions = new MarkerOptions();

            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.placeholder);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 120, 120, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            markerOptions.title("내 위치");
            markerOptions.snippet("현재 위치입니다.");
            markerOptions.position(latLng);
            markerOptions.getIcon();
            googleMaps.addMarker(markerOptions);
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

            win = false;
        }
    }

    // 인터넷에서 위치 가져오기
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            lat = location.getLatitude();
            lng = location.getLongitude();

            win = true;

            mapView = view1.findViewById(R.id.fgGoogleMap);

            mapView.getMapAsync(MapLocateActivity.this);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
        };
}
