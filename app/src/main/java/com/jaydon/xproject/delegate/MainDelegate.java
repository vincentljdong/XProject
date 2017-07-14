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

    private AppCompatActivity activity;

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

    }







    /**
     * tab改变时的监听
     */
    @Override
    public void onTabChanged(String tabId) {
//        ShowToastUtil.showLongToast(PublicMethod.getContext(), fragmentTabHost.getCurrentTab());

        /**设置标题栏名称*/
//        if (fragmentTabHost.getCurrentTab() == 1)
//            title.setText("TWO");
//        else if (fragmentTabHost.getCurrentTab() == 2)
//            title.setText("THREE");
//        else if (fragmentTabHost.getCurrentTab() == 3)
//            title.setText("FOUR");
//        else if (fragmentTabHost.getCurrentTab() == 4)
//            title.setText("FIVE");
//        else
//            title.setText("XProject");
    }

    /**
     * tab触摸监听
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
