package com.jaydon.xproject.activity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.jaydon.xproject.R;
import com.jaydon.xproject.base.BaseActivity;
import com.jaydon.xproject.delegate.MainDelegate;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/12 15:19
 * 描述	      view和逻辑分离，在各自的类中，减少复杂度，便于定位bug
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class MainActivity extends BaseActivity<MainDelegate> {

    /**
     * MainDelegate是view的控制类
     * <p>
     * view 和逻辑是分开的，用的mvp架构
     */
    @Override
    protected Class<MainDelegate> getDelegateClass() {
        return MainDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}
