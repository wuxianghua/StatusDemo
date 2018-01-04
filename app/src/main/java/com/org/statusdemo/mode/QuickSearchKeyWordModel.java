package com.org.statusdemo.mode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/2/002.
 */

public class QuickSearchKeyWordModel {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<ChildBean> getChild() {
        return child;
    }

    public void setChild(List<ChildBean> child) {
        this.child = child;
    }

    /**

     * 一级名称
     */
    private String title;

    /**
     * 图标文件名
     */
    private String iconName;

    /**
     * 状态 0:收起，1:展开
     */
    private int state;
    private List<ChildBean> child = new ArrayList<>();

    public static class ChildBean {

        private String displayName;
        private String searchKeyWord;
        private String iconName;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getSearchKeyWord() {
            return searchKeyWord;
        }

        public void setSearchKeyWord(String searchKeyWord) {
            this.searchKeyWord = searchKeyWord;
        }

        public String getIconName() {
            return iconName;
        }

        public void setIconName(String iconName) {
            this.iconName = iconName;
        }
    }
}
