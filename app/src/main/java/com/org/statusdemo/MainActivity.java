package com.org.statusdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.Point;
import com.mapbox.services.commons.models.Position;
import com.org.statusdemo.operationmap.IOperationMap;
import com.org.statusdemo.operationmap.OnMapSingleTap;
import com.org.statusdemo.operationmap.OperationMapFactory;
import com.org.statusdemo.operationmap.utils.AnimatorUtil;
import com.org.statusdemo.operationmap.utils.ViewAnimDelegate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private ViewAnimDelegate facilityAnimDelegate;
    private LinearLayout facilityLinearLayout;
    private LinearLayout mEndPointInfo;
    private TextView floorSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,"pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setStyleUrl("asset://style.json");
        mapView.onCreate(savedInstanceState);
        initView();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.getUiSettings().setCompassFadeFacingNorth(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setCompassMargins(20,260,0,0);
                BackgroundLayer layer = new BackgroundLayer("background")
                        .withProperties(PropertyFactory.backgroundColor("#ffffff"));
                mapboxMap.addLayerBelow(layer,"Frame");
                final IOperationMap operationMap = OperationMapFactory.createByMapBox(mapboxMap,MainActivity.this);
                operationMap.setMarkIcon("test",R.mipmap.ic_launcher,30,30);
                operationMap.setOnSingleTap(new OnMapSingleTap() {
                    @Override
                    public void singleTap(double x, double y) {
                        Feature feature = operationMap.selectFeature(x, y);
                        if (feature != null) {
                            operationMap.addMark("test",new LatLng(x,y),"Area-Car-Spot");
                        }
                    }
                });
                /*markerCoordinates.add(Feature.fromGeometry(
                        Point.fromCoordinates(Position.fromCoordinates(121.35146909076829,31.220871387530334))) // Fenway Park
                );
                markerCoordinates.add(Feature.fromGeometry(
                        Point.fromCoordinates(Position.fromCoordinates(121.35058354054432,31.220971657493617))) // The Paul Revere House
                );*/
                /*final IOperationMap operationMap = OperationMapFactory.createByMapBox(mapboxMap,MainActivity.this);
                operationMap.setMarkIcon("test",R.mipmap.bg_number,130,130);
                operationMap.setOnSingleTap(new OnMapSingleTap() {
                    @Override
                    public void singleTap(double x, double y) {
                        Feature feature = operationMap.selectFeature(x, y);
                        operationMap.addMark("test",new LatLng(x,y),"Area-Car-Spot");
                    }
                });*/
            }
        });
    }

    //初始化view
    private void initView() {
        facilityLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        facilityAnimDelegate = new ViewAnimDelegate(facilityLinearLayout, ViewAnimDelegate.Gravity.Top,250);
        mEndPointInfo = (LinearLayout) findViewById(R.id.end_point_info);
        floorSwitch = (TextView) findViewById(R.id.floor_switch);
    }

    public void search(View view) {
        Intent intent = new Intent(this,MapSearchActivity.class);
        startActivity(intent);
    }

    private boolean isShowFacility;
    public void onClick(View view) {
        if (facilityAnimDelegate != null) {
            if (isShowFacility) {
                facilityAnimDelegate.switchVisibilityOnAnim();
                facilityLinearLayout.setVisibility(View.GONE);
                isShowFacility = false;
                mEndPointInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= 16) {
                            mEndPointInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            mEndPointInfo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        int height = mEndPointInfo.getHeight();
                        AnimatorUtil.translationUp(mEndPointInfo,height);
                        AnimatorUtil.translationUp(floorSwitch,height);
                    }
                });

            }else {
                facilityAnimDelegate.switchVisibilityOnAnim();
                facilityLinearLayout.setVisibility(View.VISIBLE);
                isShowFacility = true;
                mEndPointInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= 16) {
                            mEndPointInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            mEndPointInfo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        int height = mEndPointInfo.getHeight();
                        AnimatorUtil.translationDown(mEndPointInfo,height);
                        AnimatorUtil.translationDown(floorSwitch,height);
                    }
                });
            }
        }
    }
}
