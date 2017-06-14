package com.jaydon.xproject.enumerate;

import com.jaydon.xproject.R;
import com.jaydon.xproject.fragment.FiveFragment;
import com.jaydon.xproject.fragment.FourFragment;
import com.jaydon.xproject.fragment.OneFragment;
import com.jaydon.xproject.fragment.ThreeFragment;
import com.jaydon.xproject.fragment.TwoFragment;

/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/13 10:43
 * 描述	      tab枚举
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public enum MainTab {
    ONE(0, R.string.tab_one, R.drawable.selector_tab_one, OneFragment.class),
    TWO(1, R.string.tab_two, R.drawable.selector_tab_two, TwoFragment.class),
    THREE(2, R.string.tab_three, R.drawable.selector_tab_three, ThreeFragment.class),
    FOUR(3, R.string.tab_four, R.drawable.selector_tab_four, FourFragment.class),
    FIVE(4, R.string.tab_five, R.drawable.selector_tab_five, FiveFragment.class);

    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> resCls;

    MainTab(int idx, int resName, int resIcon, Class<?> resCls) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.resCls = resCls;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getResCls() {
        return resCls;
    }

    public void setResCls(Class<?> resCls) {
        this.resCls = resCls;
    }
}
