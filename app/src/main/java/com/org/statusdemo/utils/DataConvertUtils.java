package com.org.statusdemo.utils;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by Administrator on 2018/1/4/004.
 */

public class DataConvertUtils {

    public static double[] webMercator2LatLng(double x1,double y1) {
        double x = x1 / 20037508.34 * 180.0;
        double y = y1 / 20037508.34 * 180.0;
        y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);
        return new double[]{y,x};
    }

    public static Coordinate latlng2WebMercator(double lat,double lng) {
        double x = lng * 20037508.34 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        y = y * 20037508.34 / 180;
        return new Coordinate(x,y);
    }
}
