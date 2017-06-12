package com.jaydon.xproject;

import android.app.Application;
import android.content.Context;

/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/12 15:19
 * 描述	      全局访问  单例
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */

public class AppContext extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
