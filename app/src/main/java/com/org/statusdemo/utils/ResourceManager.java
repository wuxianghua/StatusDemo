package com.org.statusdemo.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;


public class ResourceManager {

    public enum ResourceType {
        DRAWABLE,
        MIPMAP,
        STRING,
        RAW,
        ANIM,
        LAYOUT,
        ARRAY
    }

    private static String convertType(ResourceType t) {
        switch (t) {
            case DRAWABLE:
                return "drawable";
            case MIPMAP:
                return "mipmap";
            case STRING:
                return "string";
            case RAW:
                return "raw";
            case ANIM:
                return "anim";
            case LAYOUT:
                return "layout";
            case ARRAY:
                return "array";
        }
        return null;
    }

    /**
     * 通过文件路径返回一个图片文件。
     *
     * @param path
     * @return
     */

    public static Bitmap getResByPath(String path) {
        return BitmapFactory.decodeFile(path);
    }

    /**
     * 通过资源ID返回图片。
     *
     * @param context
     * @param resID
     * @return
     */

    public static Bitmap getBitmapByResId(Context context, int resID) {
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }


    /**
     * 通过res文件的名称返回图片。
     *
     * @param context
     * @param name
     * @return
     */

    public static Bitmap getBitmapByName(Context context, String name) {
        int resID = getRidByName(context, name);
        if (resID == 0)
            return null;// 未找到资源图片
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }


    /**
     * 通过res文件的名称返回ID。
     *
     * @param context
     * @param name
     * @return
     */

    public static int getRidByName(Context context, String name) {
//        return getRidByName(context, ResourceType.DRAWABLE, name);
        return getRidByName(context, ResourceType.MIPMAP, name);
    }

    /**
     * 通过res文件的名称返回ID。
     *
     * @param context
     * @param name
     * @return
     */
    public static int getRidByName(Context context, ResourceType type, String name) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        return context.getResources().getIdentifier(name, convertType(type),
                appInfo.packageName);
    }


    /**
     * 通过src文件夹下的图片存放的路径，例如：　String path = "com/prj/test.png"来访问图片资源。
     *
     * @param context
     * @param path
     * @return
     */

    public static InputStream getBitmapStream(Context context, String path) {
        return context.getClassLoader().getResourceAsStream(path);
    }


    /**
     * 读取Assets目录里面的图片，根据的是文件名称。
     *
     * @param context
     * @param name
     * @return
     * @throws IOException
     */

    public static InputStream getBitmapStreamByName(Context context, String name)
            throws IOException {
        return context.getResources().getAssets().open(name);
    }


    /**
     * 获取字符串。
     *
     * @param context
     * @param name
     * @return
     */
    public static String getString(Context context, String name) {
        return context.getResources().getString(getRidByName(context, ResourceType.STRING, name));
    }

}
