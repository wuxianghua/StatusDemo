package com.org.statusdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.org.statusdemo.R;
import com.org.statusdemo.mode.QuickSearchKeyWordModel;
import com.org.statusdemo.utils.DeviceUtils;
import com.org.statusdemo.utils.ResourceManager;
import com.org.statusdemo.widget.flowtag.FlowTagLayout;
import com.org.statusdemo.widget.flowtag.OnTagClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/2/002.
 */

public class QuickSearchGroupAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<QuickSearchKeyWordModel> mKeyWordList;
    private Map<String,TagAdapter> mTagAdapter = new HashMap<>();
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private int mSeparateViewHeight;
    private int mSeparateViewColor;
    private int flowLayoutPadding;

    public QuickSearchGroupAdapter(Context context, List<QuickSearchKeyWordModel> dataList) {
        mContext = context;
        mKeyWordList = dataList == null ? new ArrayList<QuickSearchKeyWordModel>() : dataList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSeparateViewHeight = DeviceUtils.dip2px(mContext, 1);
        flowLayoutPadding = DeviceUtils.dip2px(mContext, 6);
        mSeparateViewColor = mContext.getResources().getColor(R.color.ngr_separate_line);
    }

    public void addAll(List<QuickSearchKeyWordModel> dataList) {
        mKeyWordList.addAll(dataList);
    }

    public List<QuickSearchKeyWordModel> getKeyWordList() {
        return mKeyWordList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    @Override
    public int getGroupCount() {
        return mKeyWordList == null ? 0 : mKeyWordList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 2;
    }

    @Override
    public Object getGroup(int i) {
        return mKeyWordList == null ? null : mKeyWordList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mKeyWordList == null ? null : mKeyWordList.get(i).getChild().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_quick_search_group,viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        QuickSearchKeyWordModel groupModel = mKeyWordList.get(i);
        if (groupModel != null) {
            int resID = ResourceManager.getRidByName(mContext,groupModel.getIconName());
            if (resID == 0) {
                resID = R.mipmap.ic_launcher;
            }
            holder.mImgVGroupIcon.setImageResource(resID);
            holder.mTvGroupTitle.setText(groupModel.getTitle());
            holder.imaVExpand.setBackgroundResource(
                    b ? R.mipmap.ic_list_arrow_up: R.mipmap.ic_list_arrow_down
            );
        }
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (i1 == 0) {
            FlowTagLayout flowTagLayout;
            if (view != null && view instanceof FlowTagLayout) {
                flowTagLayout = (FlowTagLayout) view;
            }else {
                flowTagLayout = new FlowTagLayout(mContext);
            }
            viewGroup.setPadding(flowLayoutPadding,flowLayoutPadding,flowLayoutPadding,flowLayoutPadding);
            final QuickSearchKeyWordModel groupModel = mKeyWordList.get(i);
            if (groupModel != null) {
                TagAdapter tagAdapter = mTagAdapter.get(groupModel.getTitle());
                if (tagAdapter == null) {
                    tagAdapter = new TagAdapter(groupModel.getChild());
                    mTagAdapter.put(groupModel.getTitle(),tagAdapter);
                }
                flowTagLayout.setAdapter(tagAdapter);
                flowTagLayout.setOnTagClickListener(new OnTagClickListener() {
                    @Override
                    public void onItemClick(FlowTagLayout parent, View view, int position) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onClick(groupModel.getChild().get(position));
                        }
                    }
                });
                tagAdapter.notifyDataSetChanged();
            }
            return flowTagLayout;
        }else {
            ImageView mSeparateView;
            if (view != null && view instanceof ImageView) {
                mSeparateView = (ImageView) view;
            }else {
                mSeparateView = new ImageView(mContext);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mSeparateViewHeight
                );
                mSeparateView.setLayoutParams(layoutParams);
                mSeparateView.setBackgroundColor(mSeparateViewColor);
            }
            return mSeparateView;
        }
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private static class ViewHolder {
        View mRootView;
        ImageView mImgVGroupIcon;
        TextView mTvGroupTitle;
        ImageView imaVExpand;

        ViewHolder(View rootView) {
            mRootView = rootView;
            mImgVGroupIcon = mRootView.findViewById(R.id.imgVGroupIcon);
            mTvGroupTitle = mRootView.findViewById(R.id.tvGroupTitle);
            imaVExpand = mRootView.findViewById(R.id.imgVGroupExpand);
        }
    }

    public interface OnItemClickListener {
        void onClick(QuickSearchKeyWordModel.ChildBean childBean);
    }

    private class TagAdapter extends BaseAdapter {

        private final List<QuickSearchKeyWordModel.ChildBean> mDataList;

        TagAdapter(List<QuickSearchKeyWordModel.ChildBean> dataList)  {
            mDataList = dataList;
        }
        @Override
        public int getCount() {
            return mDataList == null? 0 : mDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return mDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.item_flow_label,viewGroup,false);
            }
            TextView textView = view.findViewById(R.id.tv_Tag);
            QuickSearchKeyWordModel.ChildBean childBean = mDataList.get(i);
            if (childBean != null) {
                textView.setText(childBean.getDisplayName());
            }
            return view;
        }
    }
}
