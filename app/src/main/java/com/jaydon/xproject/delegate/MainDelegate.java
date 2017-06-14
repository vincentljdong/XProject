package com.jaydon.xproject.delegate;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.jaydon.xproject.R;
import com.jaydon.xproject.enumerate.MainTab;
import com.jaydon.xproject.utils.PublicMethod;
import com.jaydon.xproject.utils.ShowToastUtil;
import com.kymjs.themvp.view.AppDelegate;

/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/12 15:19
 * 描述	      主界面的ui显示控制类
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class MainDelegate extends AppDelegate implements TabHost.OnTabChangeListener, View.OnTouchListener {

    private FrameLayout frameLayout;
    private FragmentTabHost fragmentTabHost;
    private AppCompatActivity activity;
    private TextView title;

    /**
     * setCintentView另一种方式
     */
    @Override
    public int getRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        activity = getActivity();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        frameLayout = get(R.id.fl);
        fragmentTabHost = get(R.id.tab);
        title = get(R.id.title);
        /**自定义标题栏的一些配置*/
        title.setText("XProject");

        /**初始化FragmentTabHos*/
        initFragmentTabHost();
    }

    private void initFragmentTabHost() {
        fragmentTabHost.setup(PublicMethod.getContext(), activity.getSupportFragmentManager(), R.id.fl);
        if (Build.VERSION.SDK_INT > 10) {
            fragmentTabHost.getTabWidget().setShowDividers(0);
            initTab();
            fragmentTabHost.setCurrentTab(0);
            fragmentTabHost.setOnTabChangedListener(this);
        }
    }

    private void initTab() {
        MainTab[] values = MainTab.values();
        int length = values.length;
        for (int i = 0; i < length; i++) {
            /**找到每一个枚举值*/
            MainTab tabValue = values[i];
            /**创建一个新的选项卡*/
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(String.valueOf(tabValue.getResName()));
            /**tab布局*/
            View tabInflate = LayoutInflater.from(PublicMethod.getContext()).inflate(R.layout.tab_indicator, null);
            /**初始化内部控件*/
            TextView tabText = (TextView) tabInflate.findViewById(R.id.tab_title);
            /**获取当前tab图标*/
            Drawable tabDrawable = activity.getResources().getDrawable(tabValue.getResIcon());
            tabText.setCompoundDrawablesWithIntrinsicBounds(null, tabDrawable, null, null);
            tabText.setText(activity.getString(tabValue.getResName()));
            tabSpec.setIndicator(tabInflate);
            tabSpec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(activity);
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("key", String.valueOf(tabValue.getResName()));
            fragmentTabHost.addTab(tabSpec, tabValue.getResCls(), bundle);
            fragmentTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }

    /**
     * tab改变时的监听
     */
    @Override
    public void onTabChanged(String tabId) {
//        ShowToastUtil.showLongToast(PublicMethod.getContext(), fragmentTabHost.getCurrentTab());

        if (fragmentTabHost.getCurrentTab() == 1)
            title.setText("TWO");
        else if (fragmentTabHost.getCurrentTab() == 2)
            title.setText("THREE");
        else if (fragmentTabHost.getCurrentTab() == 3)
            title.setText("FOUR");
        else if (fragmentTabHost.getCurrentTab() == 4)
            title.setText("FIVE");
        else
            title.setText("XProject");
    }

    /**
     * tab触摸监听
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
