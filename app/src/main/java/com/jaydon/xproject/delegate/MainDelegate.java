package com.jaydon.xproject.delegate;

import com.jaydon.xproject.R;
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
public class MainDelegate extends AppDelegate {

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
    }
}
