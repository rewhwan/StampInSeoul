package com.example.dmjhfourplay.stampinseoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

//ThemeActivity 검색 버튼을 누르면 이동 되는 액티비티

public class SearchActivity extends AppCompatActivity {

    final static String TAG = "ThemeActivity";

    String keyword;

    private EditText edtSearch2;
    private ImageButton btnSearch2;

    RequestQueue queue;

    ArrayList<ThemeData> list = new ArrayList<>();

    RecyclerView recyclerView;
    SearchAdapter adapter;
    LinearLayoutManager layoutManager;
    ProgressDialog pDialog;

    static final String KEY = "lHgLEEta%2BrgVVBrz0a5LWCybmPuBiL4Hok1S%2FMrUxkdv0dhe0B6xRnMflYD4JGvLZ96jbaDWDomL8oQOL%2BF0NQ%3D%3D";
    static final String APPNAME = "StampInSeoul";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch2 = findViewById(R.id.edtSearch2);
        btnSearch2 = findViewById(R.id.btnSearch2);

        //이전 액티비티에서 보내준 검색어을 보내줍니다.
        Intent intent = getIntent();
        String word = intent.getStringExtra("word");

        btnSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = edtSearch2.getText().toString().trim();
                if(word.length() > 1) {
                    searchData(word);
                }
            }
        });

        searchData(word);

    }

    //검색어를 입력받고 검색어를 통해서 DB의 내용을 불러줍니다.
    public void searchData(final String word) {

        //검색어를 UTF-8로 인코딩 하여줍니다.
        try {
            keyword = URLEncoder.encode(word,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        queue = Volley.newRequestQueue(getApplicationContext());

        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/searchKeyword?ServiceKey=" + KEY
                + "&keyword=" + keyword + "&areaCode=1&listYN=Y&arrange=P"
                + "&numOfRows=20&pageNo=1&MobileOS=AND&MobileApp="
                + APPNAME + "&_type=json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MainActivity.db = MainActivity.dbHelper.getWritableDatabase();
                Cursor cursor;
                cursor = MainActivity.db.rawQuery("SELECT title FROM ZZIM_" + LoginActivity.userId + ";", null);

                try {
                    //JSON파일에서 원하는 정보만 추출하여 옵니다
                    JSONObject parse_response = (JSONObject) response.get("response");
                    JSONObject parse_body = (JSONObject) parse_response.get("body");
                    JSONObject parse_items = (JSONObject) parse_body.get("items");
                    JSONArray parse_itemlist = (JSONArray) parse_items.get("item");
                    list.removeAll(list);

                    //받아온 API정보를 배열에서 빼내서 객체로 만들고 어레이리스트에 추가
                    for (int i = 0; i < parse_itemlist.length(); i++) {

                        //
                        JSONObject imsi = (JSONObject) parse_itemlist.get(i);
                        String firstImage = imsi.getString("firstimage");
                        String title = imsi.getString("title");
                        int contentID = imsi.getInt("contentid");
                        ThemeData themeData = new ThemeData(title, firstImage, contentID);

                        while (cursor.moveToNext()) {
                            if (cursor.getString(0).equals(themeData.getTitle())) {
                                themeData.setHart(true);
                            }
                        }
                        cursor.moveToFirst();
                        list.add(themeData);
                    }
                    recyclerView.setAdapter(adapter);

                } catch (ClassCastException e1) {
                    e1.printStackTrace();
                    View viewDialog = View.inflate(getApplicationContext(),R.layout.dialog_search_message,null);
                    Button btnExit = viewDialog.findViewById(R.id.btnExit);

                    final Dialog noSearchDlg = new Dialog(SearchActivity.this);
                    noSearchDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    noSearchDlg.setContentView(viewDialog);
                    noSearchDlg.show();

                    btnExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            noSearchDlg.dismiss();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
        recyclerView = findViewById(R.id.grid_recyclerview);
        adapter = new SearchAdapter(SearchActivity.this,list,R.layout.item_theme);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }
}