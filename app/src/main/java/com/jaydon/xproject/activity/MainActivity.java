package com.jaydon.xproject.activity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaydon.rxretrofit.RxRetrofit;
import com.jaydon.xproject.R;
import com.jaydon.xproject.base.BaseActivity;
import com.jaydon.xproject.delegate.MainDelegate;
import com.jaydon.xproject.enumerate.MainTab;
import com.jaydon.xproject.utils.PublicMethod;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/12 15:19
 * 描述	      view和逻辑分离，在各自的类中，减少复杂度，便于定位bug
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class MainActivity extends BaseActivity<MainDelegate> implements View.OnTouchListener, TabHost.OnTabChangeListener {
    /**
     * MainDelegate是view的控制类
     * <p>
     * view 和逻辑是分开的，用的mvp架构
     */

    private FrameLayout frameLayout;
    private FragmentTabHost fragmentTabHost;
    private TextView title;

    @Override
    protected Class<MainDelegate> getDelegateClass() {
        return MainDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        frameLayout = viewDelegate.get(R.id.fl);
        fragmentTabHost = viewDelegate.get(R.id.tab);
        title = viewDelegate.get(R.id.title);

        /**初始化FragmentTabHos*/
        initFragmentTabHost();
    }

    private void initFragmentTabHost() {
        fragmentTabHost.setup(PublicMethod.getContext(), getSupportFragmentManager(), R.id.fl);
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
            Drawable tabDrawable = getResources().getDrawable(tabValue.getResIcon());
            tabText.setCompoundDrawablesWithIntrinsicBounds(null, tabDrawable, null, null);
            tabText.setText(getString(tabValue.getResName()));
            tabSpec.setIndicator(tabInflate);
            tabSpec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(PublicMethod.getContext());
                }
            });

            Bundle bundle = new Bundle();
            bundle.putString("key", String.valueOf(tabValue.getResName()));
            fragmentTabHost.addTab(tabSpec, tabValue.getResCls(), bundle);
            fragmentTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
