package com.jaydon.xproject.activity;

import android.app.Activity;

import com.jaydon.xproject.base.BaseActivity;
import com.jaydon.xproject.delegate.GuideAndLoginDelegate;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/15 22:58
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GuideAndLoginActivity extends BaseActivity<GuideAndLoginDelegate> {
    @Override
    protected Class getDelegateClass() {
        return GuideAndLoginDelegate.class;
    }
}
