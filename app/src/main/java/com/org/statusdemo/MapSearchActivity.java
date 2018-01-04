package com.org.statusdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.org.statusdemo.adapter.QuickSearchGroupAdapter;
import com.org.statusdemo.mode.QuickSearchKeyWordModel;
import com.org.statusdemo.utils.FileUtils;
import com.org.statusdemo.widget.QuickSearchPanelView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/2/002.
 */

public class MapSearchActivity extends Activity {

    private SliderLayout mSliderView;
    private ExpandableListView mExpandLvGroup;
    private QuickSearchGroupAdapter mAdapter;
    private String quickSearchGroupPath = "QuickSearchGroupConfig.json";
    private String quickSearchPanelPath = "QuickSearchPanelConfig.json";
    private final Gson gson = new Gson();
    private List<QuickSearchKeyWordModel> data;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        String searchContents = FileUtils.readFileFromAssets(this, quickSearchGroupPath);
        String searchPanelContents = FileUtils.readFileFromAssets(this,quickSearchPanelPath);
        Type listType = new TypeToken<ArrayList<QuickSearchKeyWordModel>>(){}.getType();
        data = gson.fromJson(searchPanelContents,listType);
        mExpandLvGroup = findViewById(R.id.expandLvGroup);
        mExpandLvGroup.addHeaderView(getSliderView());
        addPanelKeyWords(data);
        mAdapter = new QuickSearchGroupAdapter(this,null);
        mAdapter.addAll((List<QuickSearchKeyWordModel>) gson.fromJson(searchContents, listType));
        mAdapter.setOnItemClickListener(new QuickSearchGroupAdapter.OnItemClickListener() {
            @Override
            public void onClick(QuickSearchKeyWordModel.ChildBean childBean) {

            }
        });
        mExpandLvGroup.setAdapter(mAdapter);
    }

    public void addPanelKeyWords(List<QuickSearchKeyWordModel> keyWordModels) {
        QuickSearchKeyWordModel model = keyWordModels.get(0);
        if (model == null) {
            return;
        }
        int pageCount = (int) Math.ceil(model.getChild().size()/8.0);
        for (int i = 0; i < pageCount; i++) {
            QuickSearchPanelView view = new QuickSearchPanelView(this,
                    model.getChild().subList(i*8,8*(i+1)>model.getChild().size()?
                    model.getChild().size() : 8*(i+1)));
            mSliderView.addSlider(view);
        }
    }

    private View getSliderView() {
        if (mSliderView == null) {
            mSliderView = new SliderLayout(this);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,500
            );
            mSliderView.setLayoutParams(layoutParams);
            mSliderView.stopAutoCycle();
        }
        return mSliderView;
    }

    //返回
    public void imagback(View view) {
        finish();
    }
}
