package com.org.statusdemo.operationmap.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Administrator on 2017/12/29/029.
 */

public class BitmapUtils {

    //计算图片的缩放比例
    public static int calcuteInSampleSize(BitmapFactory.Options options,int width,int height) {

        int mHeight = options.outHeight;
        int mWidth = options.outWidth;

        //默认缩放比例
        int inSample = 1;
        if (mHeight > height || mWidth > width) {
            int halfHeight = mHeight / 2;
            int halfWidth = mWidth / 2;
            while(halfHeight / inSample >= height && halfWidth / inSample >= width) {
                inSample *= 2;
            }
        }
        return inSample;
    }

    public static Bitmap decodeSampledbitmapFromResource(Resources resources,int resId,int width,int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置inJustDecodeBounds为true,预先加载Bitmap的宽高参数
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources,resId,options);
        //计算图片的采样率
        options.inSampleSize = calcuteInSampleSize(options,width,height);
        //根据图片采样率加载图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources,resId,options);
    }
}
