package com.org.statusdemo.operationmap.utils;

import android.animation.ObjectAnimator;
import android.graphics.ImageFormat;
import android.view.View;

/**
 * Created by Administrator on 2018/1/3/003.
 */

public class AnimatorUtil {

    //平移动画
    public static void translationDown(View view,float tranlationY){
        if (view == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"translationY",0,tranlationY + 15);
        animator.setDuration(500);
        animator.start();
    }

    public static void translationUp(View view,float tranlationY) {
        if (view == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"translationY",tranlationY + 15,0);
        animator.setDuration(500);
        animator.start();
    }

}
