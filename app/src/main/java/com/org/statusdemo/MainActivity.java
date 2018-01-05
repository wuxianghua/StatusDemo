package com.org.statusdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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
import com.org.statusdemo.navi.INavigateManager;
import com.org.statusdemo.navi.NaviManagerImpl;
import com.org.statusdemo.operationmap.IOperationMap;
import com.org.statusdemo.operationmap.OnMapSingleTap;
import com.org.statusdemo.operationmap.OperationMapFactory;
import com.org.statusdemo.operationmap.utils.AnimatorUtil;
import com.org.statusdemo.operationmap.utils.ViewAnimDelegate;
import com.org.statusdemo.utils.DataConvertUtils;
import com.vividsolutions.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private ViewAnimDelegate facilityAnimDelegate;
    private LinearLayout facilityLinearLayout;
    private LinearLayout mEndPointInfo;
    private TextView floorSwitch;
    private RelativeLayout mShowStartEnd;
    private Toolbar titleBar;
    private LinearLayout mSetStartPoint;
    private LinearLayout mSearchInput;
    private Feature startFeature;
    //路线规划
    private INavigateManager routeNaviManager;
    //判断有没有选择起点
    private boolean mIsSetStartPoint;
    private Mapbox mapbox;
    private Coordinate startPoint;
    private Coordinate endPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapbox = Mapbox.getInstance(this,"pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ");
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setStyleUrl("asset://style.json");
        mapView.onCreate(savedInstanceState);
        initView();
        initEvent();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap.getUiSettings().setCompassFadeFacingNorth(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setCompassMargins(20,260,0,0);
                BackgroundLayer layer = new BackgroundLayer("background")
                        .withProperties(PropertyFactory.backgroundColor("#ffffff"));
                mapboxMap.addLayerBelow(layer,"Frame");
                final IOperationMap operationMap = OperationMapFactory.createByMapBox(mapboxMap,MainActivity.this);
                operationMap.setOnSingleTap(new OnMapSingleTap() {
                    @Override
                    public void singleTap(double x, double y) {
                        startFeature = operationMap.selectFeature(x, y);
                        if (startFeature != null) {
                            if (!mIsSetStartPoint) {
                                operationMap.setMarkIcon("test",R.mipmap.ic_map_zhongdian,100,100);
                                operationMap.addMark("test",new LatLng(x,y),"Area-Car-Spot");
                                endPoint = DataConvertUtils.latlng2WebMercator(x,y);
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new LatLng(x,y)).build();
                                mapboxMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(position), 500);
                                mEndPointInfo.setVisibility(View.VISIBLE);
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
                                        AnimatorUtil.translationFloorUp(floorSwitch,height);
                                    }
                                });
                            } else {
                                operationMap.setMarkIcon("testqi",R.mipmap.ic_map_qidian,100,100);
                                operationMap.addMark("testqi",new LatLng(x,y),"Area-Car-Spot");
                                mSetStartPoint.setVisibility(View.VISIBLE);
                                startPoint = DataConvertUtils.latlng2WebMercator(x,y);
                                mSetStartPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        if (Build.VERSION.SDK_INT >= 16) {
                                            mSetStartPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        }else {
                                            mSetStartPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                        }
                                        int height = mSetStartPoint.getHeight();
                                        AnimatorUtil.translationUp(mSetStartPoint,height);
                                        AnimatorUtil.translationFloorUp(floorSwitch,height);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    private void initEvent() {
        routeNaviManager = new NaviManagerImpl(this,"roadNet.json");
        routeNaviManager.setNavigateListener(new INavigateManager.Listener() {
            @Override
            public void onNavigateComplete(INavigateManager.NavigateState state, List routes, Object route) {
                if (state == INavigateManager.NavigateState.OK) {
                    Log.e("haha","luxianguihuachenggong");
                }else {
                    Log.e("haha","luxianguihuachenggong1111");
                }
            }
        });
    }

    //初始化view
    private void initView() {
        facilityLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        facilityAnimDelegate = new ViewAnimDelegate(facilityLinearLayout, ViewAnimDelegate.Gravity.Top,250);
        mEndPointInfo = (LinearLayout) findViewById(R.id.end_point_info);
        floorSwitch = (TextView) findViewById(R.id.floor_switch);
        mShowStartEnd = (RelativeLayout) findViewById(R.id.show_start_end);
        titleBar = (Toolbar) findViewById(R.id.title_bar);
        mSearchInput = (LinearLayout) findViewById(R.id.ll_search_input);
        mSetStartPoint = (LinearLayout) findViewById(R.id.show_set_start);
    }

    public void search(View view) {
        Intent intent = new Intent(this,MapSearchActivity.class);
        startActivity(intent);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iconCommon_image:
                if (facilityAnimDelegate != null) {
                    facilityAnimDelegate.switchVisibilityOnAnim();
                    facilityLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.set_start_point:
                Intent intent = new Intent(this,MapSearchActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.img_back:
                /*mEndPointInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
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
                });*/
                break;
            case R.id.go_here:
                mIsSetStartPoint = true;
                mSearchInput.setVisibility(View.GONE);
                titleBar.setVisibility(View.GONE);
                mShowStartEnd.setVisibility(View.VISIBLE);
                showViewWithAnimation(mShowStartEnd);
                int height = mEndPointInfo.getHeight();
                AnimatorUtil.translationDown(mEndPointInfo, height, new AnimatorUtil.AnimationListener() {
                    @Override
                    public void animationEnd() {
                        mEndPointInfo.setVisibility(View.GONE);
                    }
                });
                AnimatorUtil.translationFloorDown(floorSwitch,height);
                break;
            case R.id.set_start:
                routeNaviManager.navigation(startPoint.x,startPoint.y,1915490,endPoint.x,endPoint.y,1915490);
                break;
        }
    }
    private void showViewWithAnimation(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= 16) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int height = view.getHeight();
                AnimatorUtil.translationUpShow(view,height);
            }
        });
    }
}
