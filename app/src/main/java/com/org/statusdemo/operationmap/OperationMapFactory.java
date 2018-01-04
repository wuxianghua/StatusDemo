package com.org.statusdemo.operationmap;

import android.content.Context;

import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Created by Administrator on 2017/12/29/029.
 */

public final class OperationMapFactory {

    public static IOperationMap createByMapBox(MapboxMap mapboxMap, Context context) {
        return new OperationMapImpl(mapboxMap,context);
    }
}
