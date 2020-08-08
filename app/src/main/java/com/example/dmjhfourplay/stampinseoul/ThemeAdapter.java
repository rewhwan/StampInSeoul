package com.example.dmjhfourplay.stampinseoul;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.hanks.library.bang.SmallBangView;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.MyViewHolder> {
    int i=0;
    final static String TAG = "ThemeActivity";

    Context context;
    ArrayList<ThemeData> list;

    int layout;

    View view;
    View viewDialog;

    static final String KEY = "lHgLEEta%2BrgVVBrz0a5LWCybmPuBiL4Hok1S%2FMrUxkdv0dhe0B6xRnMflYD4JGvLZ96jbaDWDomL8oQOL%2BF0NQ%3D%3D";
    static final String appName = "StampInSeoul";

    ThemeData detailThemeData = new ThemeData();

    RequestQueue queue;

    Bitmap bitmap;

    public ThemeAdapter(int layout, Context context, ArrayList<ThemeData> list) {
        super();
        this.layout = layout;
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txtView.setText(list.get(position).getTitle());

        Glide.with(context).load(list.get(position).getFirstImage()).into(holder.imgView);

        holder.itemView.setTag(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = (int) v.getTag();

                ThemeAdapter.AsyncTaskClassSub asyncSub = new ThemeAdapter.AsyncTaskClassSub();
                asyncSub.execute(position);

            }
        });

        //싱글톤 이후 삭제할 코드 =====
        MainActivity.dbHelper = DBHelper.getInstance(context);
        MainActivity.db = MainActivity.dbHelper.getWritableDatabase();

        Cursor cursor;

        cursor = MainActivity.db.rawQuery("SELECT title FROM ZZIM_"+ LoginSessionCallback.userId+";", null);
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(list.get(position).getTitle())){
                list.get(position).setHart(true);
                break;
            }
        }
        if(cursor!=null){
            cursor.close();
        }

        if (list.get(position).isHart()) {
            holder.Like_heart.setSelected(true);
        }else{
            holder.Like_heart.setSelected(false);
        }


        // holder.Like_heart.setSelected(false);

        holder.Like_heart.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                MainActivity.db = MainActivity.dbHelper.getWritableDatabase();

                try {

                    if (list.get(position).isHart()) {

                        // 하트 선택 해제

                        holder.Like_heart.setSelected(false);
                        list.get(position).setHart(false);

                        String zzimDelete = "DELETE FROM ZZIM_"+LoginActivity.userId+" WHERE title='"+list.get(position).getTitle()+"';";
                        MainActivity.db.execSQL(zzimDelete);

                    } else {

                        // 하트 선택

                        holder.Like_heart.setSelected(true);

                        list.get(position).setHart(true);

                        String zzimInsert = "INSERT INTO ZZIM_" + LoginActivity.userId + " VALUES('" + list.get(position).getTitle() + "', '"
                                + list.get(position).getAddr() + "', '"
                                + list.get(position).getMapX() + "', '"
                                + list.get(position).getMapY() + "', '"
                                + list.get(position).getFirstImage() + "');";

                        MainActivity.db.execSQL(zzimInsert);

                        Log.d("TAG", "하트 선택 : "+list.get(position).getFirstImage());
                        Log.d("TAG", "하트를 선택하면 ZZIM 테이블 디비에 들어간다 : " + list.get(position).getAddr());

                        holder.Like_heart.likeAnimation(new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                            }

                        });

                    }



                }catch(SQLException e){

                    Log.d(TAG, e.getMessage());

                }

            } // onClick

        });

        /*if (list.get(position).isHart()) {
            holder.Like_heart.setSelected(true);
        }*/

    }

    @Override
    public int getItemCount() {
        return list == null ? 0: list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtView;
        public ImageView imgView;

        public SmallBangView Like_heart;
        public ImageView imageHeart;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.txtView);
            imgView = itemView.findViewById(R.id.imgView);

            Like_heart = itemView.findViewById(R.id.like_heart);
            imageHeart = itemView.findViewById(R.id.imageHeart);

        }
    }

    public ThemeData TourData(int position){

        return list != null ? list.get(position) : null;
    }


    class AsyncTaskClassSub extends android.os.AsyncTask<Integer, ThemeData, ThemeData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ThemeData doInBackground(Integer... integers) {

            int position = integers[0];

            ThemeData myThemeData1 = list.get(position);

            ThemeData themeData = getData(myThemeData1.getContentsID());

            return themeData;
        }

        @Override
        protected void onProgressUpdate(ThemeData... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ThemeData themeData) {
            super.onPostExecute(themeData);

            viewDialog = View.inflate(context, R.layout.dialog_info, null);

            TextView txt_Detail_title = viewDialog.findViewById(R.id.txt_Detail_title);
            TextView txt_Detail_addr = viewDialog.findViewById(R.id.txt_Detail_addr);
            TextView txt_Detail_info = viewDialog.findViewById(R.id.txt_Detail_info);

            Button btnExit = viewDialog.findViewById(R.id.btnExit);

            ImageView img_Datail_info = viewDialog.findViewById(R.id.img_Datail_info);

            txt_Detail_title.setText(themeData.getTitle());
            txt_Detail_addr.setText(themeData.getAddr());
            txt_Detail_info.setText(themeData.getOverView());

            Glide.with(context).load(themeData.getFirstImage()).override(500, 300).into(img_Datail_info);

            final Dialog dialog = new Dialog(viewDialog.getContext());

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.setContentView(viewDialog);
            dialog.show();

            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });


        }
    } // end of AsyncTaskClass

    private ThemeData getData(int contentID) {

        queue = Volley.newRequestQueue(context);

        String url = "http://api.visitkorea.or.kr/openapi/service/"
                + "rest/KorService/detailCommon?ServiceKey=" + KEY
                + "&contentId=" + contentID
                + "&firstImageYN=Y&mapinfoYN=Y&addrinfoYN=Y&defaultYN=Y&overviewYN=Y"
                + "&pageNo=1&MobileOS=AND&MobileApp="
                + appName + "&_type=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject parse_response = (JSONObject) response.get("response");
                            JSONObject parse_body = (JSONObject) parse_response.get("body");
                            JSONObject parse_items = (JSONObject) parse_body.get("items");
                            JSONObject parse_itemlist = (JSONObject) parse_items.get("item");

                            detailThemeData.setFirstImage(parse_itemlist.getString("firstimage"));
                            detailThemeData.setTitle(parse_itemlist.getString("title"));
                            detailThemeData.setAddr(parse_itemlist.getString("addr1"));
                            detailThemeData.setOverView(parse_itemlist.getString("overview"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(jsObjRequest);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return detailThemeData;
    }
}
