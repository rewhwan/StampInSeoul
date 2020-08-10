package com.example.dmjhfourplay.stampinseoul;


    //ThemeActivity 쇼핑 테마 Fragment

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kakao.network.response.JSONObjectResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Theme_shpping_frag extends Fragment {

    private View view;
    ProgressDialog pDialog;

    RecyclerView recyclerView;
    ThemeAdapter adapter;
    LinearLayoutManager layoutManager;

    RequestQueue queue;

    AlertDialog.Builder dialog;
    //메인 화면 출력용
    ArrayList<ThemeData> list = new ArrayList<>();

    //상세 다이얼로그 출력용
    ThemeData detailThemeData = new ThemeData();

    final static String TAG = "ThemeActivity";
    static String key;
    static final String appName = "StampInSeoul";

    public Theme_shpping_frag() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //String.xml 에서 api키값을 가져옵니다.
        key = getString(R.string.api_key);

        view = inflater.inflate(R.layout.frag_theme,container,false);

        recyclerView = view.findViewById(R.id.grid_recyclerview);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        Theme_shpping_frag.AsyncTaskClassMain async = new Theme_shpping_frag.AsyncTaskClassMain();
        async.execute();

        adapter = new ThemeAdapter(R.layout.item_theme,getActivity(),list);

        return view;
    }

    class AsyncTaskClassMain extends AsyncTask<Integer, Long, String> {

        //일반쓰레드를 돌리기 전에 메인쓰레드에서 보여줄 화면을 처리합니다.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayLoader();
        }

        //일반 쓰레드에서 돌릴 네트워크 작
        @Override
        protected String doInBackground(Integer... integers) {
            getAreaBasedList();
            return "작업 종료";
        }


        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //contentid를 위한 함수 (contentid는 detailCommon에서 쓰기 위해서 구한다)
    private void getAreaBasedList() {

        queue = Volley.newRequestQueue(getActivity());
        //쇼핑의 타입 ID는 38번
        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/areaBasedList?ServiceKey=" + key
                + "&areaCode=1&contentTypeId=38&listYN=Y&arrange=P"
                + "&numOfRows=50&pageNo=1&MobileOS=AND&MobileApp="
                + appName + "&_type=json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();

                try {
                    JSONObject parse_response = (JSONObject) response.get("response");
                    JSONObject parse_body = (JSONObject) parse_response.get("body");
                    JSONObject parse_items = (JSONObject) parse_body.get("items");
                    JSONArray parse_itemlist = (JSONArray) parse_items.get("item");

                    list.removeAll(list);

                    for(int i = 0; i < parse_itemlist.length(); i++) {
                        JSONObject jsonObject = (JSONObject) parse_itemlist.get(i);
                        String firstimage = jsonObject.getString("firstimage");
                        String title = jsonObject.getString("title");
                        String addr = jsonObject.getString("addr1");
                        double mapx = jsonObject.getDouble("mapx");
                        double mapy = jsonObject.getDouble("mapy");
                        int contentid = jsonObject.getInt("contentid");

                        ThemeData themeData = new ThemeData(title,addr,mapx,mapy,firstimage,contentid);

                        list.add(themeData);
                    }
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG,error.getMessage() + "에러");
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("잠시만 기다려 주세요..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

}
