package com.org.statusdemo.operationmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;
import com.org.statusdemo.MainActivity;
import com.org.statusdemo.R;
import com.org.statusdemo.operationmap.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/29/029.
 */

public class OperationMapImpl implements IOperationMap {

    private MapboxMap mMapboxMap;
    private Context mContext;

    public OperationMapImpl(MapboxMap mapboxMap, Context context) {
        mMapboxMap = mapboxMap;
        mContext = context;
    }

    //搜索feature
    List<Feature> listFeature;
    @Override
    public Feature selectFeature(double x, double y) {
         listFeature = mMapboxMap.queryRenderedFeatures(mMapboxMap.getProjection().toScreenLocation(new LatLng(x, y)), "Area-Car-Spot");
        if (listFeature.size() == 0) {
            listFeature = mMapboxMap.queryRenderedFeatures(mMapboxMap.getProjection().toScreenLocation(new LatLng(x, y)), "Area-Lane");
            if (listFeature.size() == 0) {
                listFeature = mMapboxMap.queryRenderedFeatures(mMapboxMap.getProjection().toScreenLocation(new LatLng(x, y)), "Area-All");
                if (listFeature.size() == 0) {
                    return null;
                }
            }
        }
        return listFeature.get(0);
    }

    //地图单击事件
    @Override
    public void setOnSingleTap(final OnMapSingleTap onMapSingleTap) {
        mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                onMapSingleTap.singleTap(point.getLatitude(),point.getLongitude());
            }
        });
    }

    //地图显示mark
    @Override
    public void setMarkIcon(String markName, int resId, int width, int height) {
        mMapboxMap.addImage(markName, BitmapUtils.decodeSampledbitmapFromResource(
                mContext.getResources(),
                resId,
                width,
                height
        ));
    }

    //添加图标
    List<Feature> markerCoordinates;
    GeoJsonSource geoJsonSource;
    SymbolLayer markers;
    @Override
    public void addMark(String markName, LatLng latLng,String aboveId) {
        if (markerCoordinates == null) {
            markerCoordinates = new ArrayList<>();
        }
        markerCoordinates.clear();
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromCoordinates(Position.fromCoordinates(latLng.getLongitude(),latLng.getLatitude()))) // Boston Common Park
        );
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
        if (geoJsonSource == null) {
            geoJsonSource = new GeoJsonSource(markName+"-source", featureCollection);
            mMapboxMap.addSource(geoJsonSource);
        }else {
            geoJsonSource.setGeoJson(featureCollection);
        }
        markers = (SymbolLayer) mMapboxMap.getLayer(markName+"-layer");
        if (markers == null) {
            markers = new SymbolLayer(markName+"-layer", markName+"-source")
                    .withProperties(PropertyFactory.iconImage(markName));
            mMapboxMap.addLayer(markers);
        }else {
            markers.setProperties(PropertyFactory.iconImage(markName));
        }
    }
}
