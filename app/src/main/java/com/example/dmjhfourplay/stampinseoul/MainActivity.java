package com.example.dmjhfourplay.stampinseoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import me.relex.circleindicator.CircleIndicator;

//듀토리얼 Fragment 뷰페이저 클래스

public class MainActivity extends AppCompatActivity {

    private long backButtonTime = 0;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_viewpager);

        viewPager = findViewById(R.id.viewPager);

        fragmentPagerAdapter = new tutorialViewPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);

        //인디케이터를 설정해준다
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }



    //뷰 페이저에 프레그먼트를 추가해줍니다
    private void setupViewPager(ViewPager viewPager) {
        tutorialViewPagerAdapter adapter = new tutorialViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new tutorial_fragment1());
        adapter.addFragment(new tutorial_fragment2());
        adapter.addFragment(new tutorial_fragment3());
        adapter.addFragment(new tutorial_fragment4());
        adapter.addFragment(new tutorial_fragment5());
        adapter.addFragment(new tutorial_fragment6());
        adapter.addFragment(new tutorial_fragment7());
        viewPager.setAdapter(adapter);
    }
}
