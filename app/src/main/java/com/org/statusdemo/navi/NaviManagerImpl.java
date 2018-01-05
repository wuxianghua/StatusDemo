package com.org.statusdemo.navi;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.org.nagradcore.model.path.TreatedRoadNet;
import com.org.nagradcore.navi.AStar;
import com.org.nagradcore.navi.AStarPath;
import com.org.nagradcore.navi.AStarVertex;
import com.org.nagradcore.navi.DefaultG;
import com.org.nagradcore.navi.DefaultH;
import com.org.nagradcore.navi.VertexLoader;
import com.org.statusdemo.utils.DataConvertUtils;
import com.org.statusdemo.utils.FileUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/4/004.
 */

public class NaviManagerImpl implements INavigateManager {

    private static GeometryFactory geometryFactory = new GeometryFactory();

    private AStar astar;

    private HandlerThread handlerThread;

    private Handler routeHandler;

    private Context context;

    private Listener<FeatureCollection> listener = DEFAULT_LISTENER;

    private static Listener<FeatureCollection> DEFAULT_LISTENER = new Listener<FeatureCollection>() {
        @Override
        public void onNavigateComplete(NavigateState state, List<AStarPath> routes, FeatureCollection route) {

        }
    };

    public NaviManagerImpl(Context context,final String routeDataPath) {
        this.context = context;
        handlerThread = new HandlerThread("naviManagerImpl");
        handlerThread.start();
        routeHandler = new Handler(handlerThread.getLooper());
        routeHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String pathJsonStr = FileUtils.loadFromAssets(NaviManagerImpl.this.context,routeDataPath);
                    JSONObject pathObject = new JSONObject(pathJsonStr);
                    TreatedRoadNet treatedRoadNet = new TreatedRoadNet(pathObject.optLong("mapId"),pathObject.optJSONArray("vertexes"),pathObject.optJSONObject("paths"),pathObject.optJSONObject("connections"));
                    astar = new AStar(new DefaultG(),new DefaultH(),new VertexLoader(treatedRoadNet));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setNavigateListener(Listener listener) {
        this.listener = listener == null ? DEFAULT_LISTENER : listener;
    }

    @Override
    public void navigation(double fromX, double fromY, long fromPlanargraph, double toX, double toY, long toPlanargraph) {
        if (!precondition()) {
            return;
        }
        List<AStarPath> routes = astar.astar(
                geometryFactory.createPoint(new Coordinate(fromX,fromY)),
                fromPlanargraph,
                geometryFactory.createPoint(new Coordinate(toX,toY)),
                toPlanargraph,
                0
        );
        if (routes == null || routes.size() == 0) {
            this.listener.onNavigateComplete(NavigateState.CLIP_NAVIGATE_ERROR,null,null);
            return;
        }

        List<Feature> features = new ArrayList<>();
        for (AStarPath aStarPath : routes) {
            AStarVertex fromVertex = aStarPath.getFrom();
            AStarVertex toVertex = aStarPath.getTo();
            Point startPoint = (Point) fromVertex.getVertex().getShape();
            double[] startPosition = DataConvertUtils.webMercator2LatLng(startPoint.getX(), startPoint.getY());
            Point endPoint = (Point) toVertex.getVertex().getShape();
            double[] endPosition = DataConvertUtils.webMercator2LatLng(endPoint.getX(), endPoint.getY());
            List<Position> positionList = new ArrayList<>();
            positionList.add(Position.fromCoordinates(startPosition[1],startPosition[0]));
            positionList.add(Position.fromCoordinates(endPosition[1],endPosition[0]));
            LineString lineString = LineString.fromCoordinates(positionList);
            features.add(Feature.fromGeometry(lineString));
        }
        FeatureCollection routeFeatureCollection = FeatureCollection.fromFeatures(features);
        this.listener.onNavigateComplete(NavigateState.OK,routes,routeFeatureCollection);
    }

    private boolean precondition() {
        if (astar == null) {
            this.listener.onNavigateComplete(NavigateState.CLIP_NAVIGATE_ERROR,null,null);
            return false;
        }
        return true;
    }

    @Override
    public void destructor() {
        if (routeHandler != null && handlerThread != null) {
            routeHandler.removeCallbacksAndMessages(null);
            routeHandler.post(new Runnable() {
                @Override
                public void run() {
                    handlerThread.quit();
                    routeHandler = null;
                    handlerThread = null;
                }
            });
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destructor();
    }
}
