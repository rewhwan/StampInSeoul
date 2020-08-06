package com.example.dmjhfourplay.stampinseoul;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    //SearchActivity에 필요한 어댑터 클래스

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context context;
    ArrayList<ThemeData> list;
    int layout;
    ProgressDialog pDialog;

    View view;
    View viewDialog;

    static final String KEY = "lHgLEEta%2BrgVVBrz0a5LWCybmPuBiL4Hok1S%2FMrUxkdv0dhe0B6xRnMflYD4JGvLZ96jbaDWDomL8oQOL%2BF0NQ%3D%3D";
    static final String appName = "StampInSeoul";

    ThemeData detailThemeData = new ThemeData();
    RequestQueue queue;

    public SearchAdapter(Context context, ArrayList<ThemeData> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout,viewGroup,false);
        SearchAdapter.MyViewHolder viewHolder = new SearchAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.MyViewHolder myViewHolder, final int position) {
        myViewHolder.txtView.setText(list.get(position).getTitle());
        Glide.with(context).load(list.get(position).getFirstImage()).into(myViewHolder.imgView);

        myViewHolder.itemView.setTag(position);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                SearchAdapter.AsyncTaskClass asyncSub = new SearchAdapter.AsyncTaskClass();
                asyncSub.execute(position);
            }
        });

        myViewHolder.Like_heart.setSelected(false);
        myViewHolder.Like_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.db = MainActivity.dbHelper.getWritableDatabase();

                if(list.get(position).isHart()) {
                    //하트 선택해제
                    myViewHolder.Like_heart.setSelected(false);
                    list.get(position).setHart(false);

                    String zzimDelet = "DELETE FROM ZZIM_"+ LoginActivity.userId +" WHERE title ='"+ list.get(position).getTitle()+"';";
                    MainActivity.db.execSQL(zzimDelet);
                }else {
                    myViewHolder.Like_heart.setSelected(true);
                    list.get(position).setHart(true);

                    String zzimInsert = "INSERT INTO ZZIM_"+ LoginActivity.userId +"(title,addr,mapX,mapY) VALUES('"+ list.get(position).getTitle()+"', '"
                            + list .get(position).getAddr() + "', '"
                            + list .get(position).getMapX() + "', '"
                            + list .get(position).getMapY() + "');";
                    MainActivity.db.execSQL(zzimInsert);
                    myViewHolder.Like_heart.likeAnimation(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationResume(Animator animation) {
                            super.onAnimationResume(animation);
                        }
                    });
                }
            }
        });

        if(list.get(position).isHart()) {
            myViewHolder.Like_heart.setSelected(true);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtView;
        public ImageView imgView;

        public SmallBangView Like_heart;
        public ImageView imageHeart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtView = itemView.findViewById(R.id.txtView);
            imgView = itemView.findViewById(R.id.imgView);

            Like_heart = itemView.findViewById(R.id.like_heart);
            imageHeart = itemView.findViewById(R.id.imageHeart);
        }
    }

    class  AsyncTaskClass extends AsyncTask<Integer, ThemeData, ThemeData> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayLoader();
        }

        @Override
        protected ThemeData doInBackground(Integer... integers) {
            int position = integers[0];

            ThemeData myThemeData1 = list.get(position);
            ThemeData themeData = getData(myThemeData1.getContentsID());

            return themeData;
        }

        @Override
        protected void onProgressUpdate(ThemeData... values) { super.onProgressUpdate(values); }

        @Override
        protected void onPostExecute(ThemeData themeData) {
            super.onPostExecute(themeData);

            viewDialog = View.inflate(context, R.layout.dialog_info, null);

            TextView txt_detail_title = viewDialog.findViewById(R.id.txt_Detail_title);
            TextView txt_detail_addr = viewDialog.findViewById(R.id.txt_Detail_addr);
            TextView txt_detail_info = viewDialog.findViewById(R.id.txt_Detail_info);
            ImageView img_detail_info = viewDialog.findViewById(R.id.img_Datail_info);

            Button btnExit = viewDialog.findViewById(R.id.btnExit);

            Glide.with(context).load(themeData.getFirstImage()).override(500,300).into(img_detail_info);

            txt_detail_title.setText(themeData.getTitle());
            txt_detail_addr.setText(themeData.getAddr());
            txt_detail_info.setText(themeData.getOverView());

            final Dialog dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(viewDialog);
            dialog.show();
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }

        private ThemeData getData(int contentID) {

            queue = Volley.newRequestQueue(context);

            String url = "http://api.visitkorea.or.kr/openapi/service/"
                    + "rest/KorService/detailCommon?ServiceKey=" + KEY
                    + "&contentId=" + contentID
                    + "&firstImageYN=Y&mapinfoYN=Y&addrinfoYN=Y&defaultYN=Y&overviewYN=Y"
                    + "&pageNo=1&MobileOS=AND&MobileApp="
                    + appName + "&_type=json";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismiss();
                    JSONObject parse_response = null;
                    try {
                        parse_response = (JSONObject) response.get("response");
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
                    pDialog.dismiss();
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            queue.add(jsonObjectRequest);


            return detailThemeData;
        }

        private void displayLoader() {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("잠시만 기다려 주세요..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }


}
