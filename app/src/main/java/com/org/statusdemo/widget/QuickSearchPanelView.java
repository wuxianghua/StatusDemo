package com.org.statusdemo.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.org.statusdemo.R;
import com.org.statusdemo.mode.QuickSearchKeyWordModel;
import com.org.statusdemo.utils.DeviceUtils;
import com.org.statusdemo.utils.ResourceManager;

import java.util.List;

/**
 * Created by Administrator on 2018/1/3/003.
 */

public class QuickSearchPanelView extends BaseSliderView {

    private Context mContext;
    private List<QuickSearchKeyWordModel.ChildBean> mData;
    private int mVerticalSpacing;

    public QuickSearchPanelView(Context context,List<QuickSearchKeyWordModel.ChildBean> data) {
        super(context);
        mContext = context;
        mData = data;
        mVerticalSpacing = DeviceUtils.dip2px(context,5);
    }

    @Override
    public View getView() {
        GridView view = new GridView(mContext);
        view.setNumColumns(4);
        view.setPadding(0,mVerticalSpacing,0,mVerticalSpacing);
        view.setVerticalSpacing(mVerticalSpacing);
        view.setAdapter(new CustomAdapter());
        return view;
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData == null ? null : mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.item_quick_search_panel,viewGroup,false
                );
                holder = new ViewHolder(view);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            QuickSearchKeyWordModel.ChildBean childBean = mData.get(i);
            if (childBean != null) {
                int resId = ResourceManager.getRidByName(mContext,childBean.getIconName());
                if (resId == 0) {
                    resId = R.mipmap.ic_search_hot_01;
                }
                holder.imgVIcon.setBackgroundResource(resId);
                holder.tvKeyWordAlias.setText(childBean.getDisplayName());
            }
            return view;
        }
    }

    private static class ViewHolder {
        ImageView imgVIcon;
        TextView tvKeyWordAlias;

        ViewHolder(View rootView) {
            imgVIcon = rootView.findViewById(R.id.imgVIcon);
            tvKeyWordAlias = rootView.findViewById(R.id.tvKeyWordAlias);
        }
    }
}
