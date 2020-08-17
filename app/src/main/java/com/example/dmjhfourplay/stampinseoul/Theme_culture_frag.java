package com.example.dmjhfourplay.stampinseoul;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.airbnb.lottie.LottieAnimationView;
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

//ThemeActivity 문화 테마 Fragment

public class Theme_culture_frag extends Fragment {

    private View view;
    ProgressDialog pDialog;

    RecyclerView recyclerView;
    ThemeAdapter adapter;
    LinearLayoutManager layoutManager;

    RequestQueue queue;
    View dialogView;
    AlertDialog.Builder dialog;
    //메인화면 출력용
    ArrayList<ThemeData> list = new ArrayList<>();
    //상세 다이얼로그 출력용
    ThemeData detailThemeData = new ThemeData();
    TextView txt_Detail_Info;
    ImageView img_Detail_Info;

    final static String TAG = "ThemeActivity";
    static String key;
    static final String appName = "StampInSeoul";

    ArrayList<Integer> contentIdList = new ArrayList<>();

    LottieAnimationView animationView2 = null;

    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        key = getString(R.string.api_key);

        view = inflater.inflate(R.layout.frag_theme,container,false);

        recyclerView = view.findViewById(R.id.grid_recyclerview);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        Theme_culture_frag.AsyncTaskClassMain async = new Theme_culture_frag.AsyncTaskClassMain();
        async.execute();

        adapter = new ThemeAdapter(R.layout.item_theme,getActivity(),list);

        return view;
    }

    class AsyncTaskClassMain extends AsyncTask<Integer,Long, String> {

        //일반 쓰레드 돌리기 전 메인쓰레드에서 보여줄 화면처리
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayLoader();
        }

        //일반 쓰레드에서 돌릴 네트워크 작업
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

    //contentid를 위한 함수(contentId는 detailCommon에서 쓰기 위해 구한다)
    private void getAreaBasedList() {
        queue = Volley.newRequestQueue(getActivity());
        //문화 14
        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/areaBasedList?ServiceKey=" + key
                + "&areaCode=1&contentTypeId=14&listYN=Y&arrange=R"
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

                    for(int i = 0; i < parse_itemlist.length();i++) {
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG,error.getMessage()+"에러");
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
