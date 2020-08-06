package com.example.dmjhfourplay.stampinseoul;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

    //각 테마 Fragment를 관리하는 어댑터 클래스

public class ThemeViewPagerAdapter extends FragmentStatePagerAdapter {

    public ThemeViewPagerAdapter(FragmentManager fm) { super(fm); }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new Theme_festival_frag();
            case 1: return new Theme_shpping_frag();
            case 2: return new Theme_festival_frag();
            case 3: return new Theme_festival_frag();
            case 4: return new Theme_festival_frag();
//            case 1: return Theme_shpping_frag.newInstance();
//            case 2: return Theme_food_frag.newInstance();
//            case 3: return Theme_Acti_frag.newInstance();
//            case 4: return Theme_culture_frag.newInstance();
//            default: return null;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    //상단의 탭 레이아웃 인디케이터에 텍스트를 선언해주는것
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){

            case 0: return "축제";
            case 1: return "쇼핑";
            case 2: return "맛집";
            case 3: return "액티비티";
            case 4: return "문화";
            default: return null;
        }
    }

}
