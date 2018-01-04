package com.org.statusdemo.operationmap;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.geojson.Feature;

/**
 * Created by Administrator on 2017/12/29/029.
 */

public interface IOperationMap {

    //搜索feature
    Feature selectFeature(double x, double y);

    //点击地图
    void setOnSingleTap(OnMapSingleTap onMapSingleTap);

    //添加地图显示mark
    void setMarkIcon(String markName,int resId,int width,int height);

    //添加Mark
    void addMark(String markName, LatLng latLng,String aboveId);
}
