package com.example.dmjhfourplay.stampinseoul;

import android.view.View;

//인위적으로 제작한 리사이클러뷰를 위한 클릭 리스너 인터페이스

public interface ClickListener {

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
