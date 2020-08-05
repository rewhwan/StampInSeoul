package com.example.dmjhfourplay.stampinseoul;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;

public class GpsAnimationDialog extends ProgressDialog {

    private Context c;

    public GpsAnimationDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        c = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_roading_animation);

        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("location_search.json");
        animationView.loop(true);
        animationView.playAnimation();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
