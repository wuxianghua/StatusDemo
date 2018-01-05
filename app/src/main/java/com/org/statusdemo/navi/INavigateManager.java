package com.org.statusdemo.navi;

import com.org.nagradcore.navi.AStarPath;

import java.util.List;

/**
 * Created by Administrator on 2018/1/4/004.
 */

public interface INavigateManager <Route>{

    enum NavigateState {
        OK(0),
        SWITCH_NAVIGATE_SUCCESS(1),
        CLIP_NAVIGATE_SUCCESS(2),
        NAVIGATE_REQUEST_ERROR(3),
        NAVIGATE_REQUEST_TIMEOUT(4),
        NAVIGATE_UNKNOWN_ERROR(5),
        NAVIGATE_NOT_FOUND(6),
        CLIP_NAVIGATE_ERROR(7),
        PLANARGRAPH_ERROR(8);
        int state;
        public static INavigateManager.NavigateState getState(int state) {
            INavigateManager.NavigateState[] var1 = values();
            for(INavigateManager.NavigateState s : var1) {
                if(s.state == state) {
                    return s;
                }
            }
            return OK;
        }
        NavigateState(int state) {
            this.state = state;
        }
        public int getState() {
            return this.state;
        }
    }

    interface Listener<T> {
        void onNavigateComplete(NavigateState state, List<AStarPath> routes, T route);
    }

    void setNavigateListener(Listener<Route> listener);

    void navigation(double fromX, double fromY, long fromPlanargraph, double toX, double toY, long toPlanargraph);

    void destructor();
}
