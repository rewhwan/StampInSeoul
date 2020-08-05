package com.example.dmjhfourplay.stampinseoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ThemeActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener, ViewPager.OnPageChangeListener, View.OnClickListener {

    private FragmentStatePagerAdapter fragmentStatePagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    boolean isDragged;

    private long backButtonTime = 0;

    private ListView listView;

    // 찜 데이터베이스와 연동되는 리스트
    private ArrayList<ThemeData> list = new ArrayList<>();
    // 찜 목록에서 선택한 것만 담는 리스트
    private ArrayList<ThemeData> checkedList = new ArrayList<>();
    // 찜 목록에 뿌려주기 위해 만든 리스트
    private ArrayList<String> titleList = new ArrayList<>();

    //플로팅 버튼 전역변수
    private FloatingActionButton fab, fab1, fab2;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    private EditText edtSearch;
    private ImageButton btnSearch;

    String strNickname, strProfile;
    Long strId;
    Long ID;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        // 플로팅 버튼, 드로어 레이아웃 설정
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);

        // 애니메이션 변수
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        //이벤트 등록
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            //플로팅 버튼을 열고 닫는 버튼
            case R.id.fab :
                anim();
                break;

            //BottomMenuActivity로 이동시켜주는 버튼
            case R.id.fab1 :
                anim();
                Intent intent = new Intent(ThemeActivity.this, BottomMenuActivity.class);
                startActivity(intent);
                break;

            //찜목록을 보여주는 버튼
            case R.id.fab2:
                list.removeAll(list);
                checkedList.removeAll(checkedList);
                titleList.removeAll(titleList);
                anim();

                final View viewDialog = view.inflate(view.getContext(),R.layout.dialog_favorites,null);
                listView = viewDialog.findViewById(R.id.listView);

                //DB에 있는 찜목록 테이블에 들어있는 리스트를 가져와준다
                MainActivity.db = MainActivity.dbHelper.getWritableDatabase();
                final Cursor cursor;
                cursor = MainActivity.db.rawQuery("Select * FROM ZZIM_"+ LoginActivity.userId +";",null);

                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        list.add(new ThemeData(cursor.getString(0),cursor.getString(1),cursor.getDouble(2),cursor.getDouble(3),cursor.getString(4)));
                        titleList.add(cursor.getString(0));
                    }
                }

                //찜목록을 체크박스로 선택할 수 있도록 세팅해줍니다
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.item_check_box_color,titleList);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                Button btnSave = viewDialog.findViewById(R.id.btnSave);
                Button btnExit = viewDialog.findViewById(R.id.btnExit);

                final Dialog dialog = new Dialog(viewDialog.getContext());

                //Check
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SparseBooleanArray booleans = listView.getCheckedItemPositions();

                        //스탬프 리스트에 이미 있는 항목을 선택한 경우 checkedlist에 들어가지 못함
                        if(booleans.get(position)) {
                            checkedList.add(list.get(position));
                            Log.d("TAG", " 처음 체크했을때 체크리스트 : " + checkedList);

                            //DB관련 코드들
                            MainActivity.db = MainActivity.dbHelper.getWritableDatabase();
                            Cursor cursor;
                            cursor = MainActivity.db.rawQuery("SELECT title FROM STAMP_"+ LoginActivity.userId+";",null);

                            if(checkedList != null) {
                                while (cursor.moveToNext()) {
                                    if(cursor.getString(0).equals(list.get(position).getTitle())) {
                                        Toast.makeText(getApplicationContext(), list.get(position).getTitle() + " 이미 스탬프 리스트에 들어 있습니다.", Toast.LENGTH_LONG).show();
                                        checkedList.remove(list.get(position));
                                    }
                                }
                                cursor.moveToFirst();
                            }
                        }else {
                            checkedList.remove(list.get(position));
                        }
                        cursor.close();
                    }
                });

                //Delete
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String query = "DELETE FROM ZZIM_"+ LoginActivity.userId +" WHERE title ='"+ list.get(position).getTitle()+"';";
                        MainActivity.db.execSQL(query);

                        adapter.remove(titleList.get(position));
                        adapter.notifyDataSetChanged();
                        list.remove(list.get(position));

                        return true;
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(viewDialog);
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.db = MainActivity.dbHelper.getWritableDatabase();
                        Cursor cursor;
                        cursor = MainActivity.db.rawQuery("SELECT * FROM STAMP_"+ LoginActivity.userId+";",null);
                        cursor.moveToFirst();

                        if(checkedList.size()+cursor.getCount() > 8) {
                            Toast.makeText(ThemeActivity.this, "스탬프 리스트에 8개 이상 담을 수 없습니다.\n현재 스탬프 리스트에는 "+cursor.getCount()+"개 담겨있습니다.", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ThemeActivity.this, "스탬프 리스트에 잘 담았습니다", Toast.LENGTH_SHORT).show();
                            for (ThemeData themeData : checkedList) {
                                String query = "INSERT INTO STAMP_"+ LoginActivity.userId +"(title, addr, mapX, mpaY, firstImage) VALUES ('"+ themeData.getTitle()+"'," + "'"
                                        +themeData.getAddr()+"','"
                                        +themeData.getMapX()+"','"
                                        +themeData.getMapY()+"','"
                                        +themeData.getFirstImage()+"');";

                                MainActivity.db.execSQL(query);
                            }
                        }
                        cursor.close();
                        dialog.dismiss();
                    }
                });

                btnExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                cursor.close();
                break;

        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    //플로팅 버튼을 눌렀을 때 열고 닫히는 애니메이션 효과
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
}