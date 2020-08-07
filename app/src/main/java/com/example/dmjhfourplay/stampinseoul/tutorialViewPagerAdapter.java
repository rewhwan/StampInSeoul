package com.example.dmjhfourplay.stampinseoul;

    // 각 듀토리얼 Fragment를 관리하는 뷰 페이저 어댑터 클래스

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class tutorialViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList = new ArrayList<>();

    public tutorialViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //뷰페이저에서 프래그먼트를 교체하여 보여주는 역할
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    //뷰페이저에 몇개의 프레크먼트를 보여줄수 있는지 알려줍니다.
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    //ArrayList에 Fragment를 추가시켜주는 함수
    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }
}
