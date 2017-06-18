package com.jaydon.xproject.activity;

import com.jaydon.xproject.base.BaseActivity;
import com.jaydon.xproject.delegate.LoginActivityDelatage;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/16 17:20
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LoginActivity extends BaseActivity<LoginActivityDelatage> {
    @Override
    protected Class getDelegateClass() {
        return LoginActivityDelatage.class;
    }
}
