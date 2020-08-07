package com.example.dmjhfourplay.stampinseoul;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class API implements Response.Listener,Response.ErrorListener{

    //======================= 전역 변수 =====================================

    //API를 불러올 때에 필요한 변수들
    private static final String KEY = "lHgLEEta%2BrgVVBrz0a5LWCybmPuBiL4Hok1S%2FMrUxkdv0dhe0B6xRnMflYD4JGvLZ96jbaDWDomL8oQOL%2BF0NQ%3D%3D";
    private static final String APP_NAME = "Stamp In Seoul";

    //API를 통해서 받아온 데이터를 저장하는 ArrayList
    private ArrayList<ThemeData> apiList = new ArrayList<>();

    //Volley -> 앱의 네트워킹을 쉽고 빠르게 해주는 HTTP 라이브러리
    private RequestQueue queue;

    private static API api;

    private RecyclerView recyclerView;

    ThemeAdapter adapter;
    private String recycleViewFlag;

    public String getRecycleViewFlag() {
        return recycleViewFlag;
    }

    public void setRecycleViewFlag(String recycleViewFlag) {
        this.recycleViewFlag = recycleViewFlag;
    }

    //Constructor
    private API() {}
    //싱글톤 패턴 디자인
    public static API getInstance() {
        if(api == null) {
            api = new API();
        }
        return api;
    }

    //한국광광공사 API를 가져올때 사용
    public ArrayList<ThemeData> getThemeData(Activity activity, int contentTypeId) {

        this.recycleViewFlag = recycleViewFlag;

        adapter = new ThemeAdapter(R.layout.item_theme,activity,apiList);

        //새로운 RequestQueue를 생성해줌
        queue = Volley.newRequestQueue(activity);

        //제공받은 매개변수로 API를 알맞게 가져오도록 문자열 변수 선언
        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/areaBasedList?ServiceKey=" + KEY
                + "&areaCode=1&contentTypeId="+contentTypeId+"&listYN=Y&arrange=P"
                + "&numOfRows=20&pageNo=1&MobileOS=AND&MobileApp="
                + APP_NAME + "&_type=json";


        //JSON 객체를 요청하는 객체를 생성
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,this, this);

        //큐를 실행시켜줍니다.
        queue.add(jsonObjectRequest);


        return apiList;
    }// End of getThemeData


    @Override
    public void onResponse(Object response) {

        JSONObject jsonObject = (JSONObject)response;

        try {
            //API를 통해서 받아온 JSON 파일을 파싱하여 데이터만 배열에 저장합니다.
            JSONObject parse_response = (JSONObject) jsonObject.get("response");
            JSONObject parse_body = (JSONObject) parse_response.get("body");
            JSONObject parse_items = (JSONObject) parse_body.get("items");
            JSONArray parse_itemlist = (JSONArray) parse_items.get("item");

            //데이터를 저장하기 전에 초기화를 실행시켜 준다.
            apiList.clear();

            //JSON으로 받은 정보를 ThemeData 객체로 만들어 ArrayList에 추가해줍니다.
            for(int i = 0; i < parse_itemlist.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) parse_itemlist.get(i);

                String firstimage = jsonObject2.getString("firstimage");
                String title = jsonObject2.getString("title");
                String addr = jsonObject2.getString("addr1");
                double mapx = jsonObject2.getDouble("mapx");
                double mapy = jsonObject2.getDouble("mapy");
                int contentid = jsonObject2.getInt("contentid");

                ThemeData themeData = new ThemeData(title,addr,mapx,mapy,firstimage,contentid);
                apiList.add(themeData);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("API", error.getMessage() + "에러");
    }
}
