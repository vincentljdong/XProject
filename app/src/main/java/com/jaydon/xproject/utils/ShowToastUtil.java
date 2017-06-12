package com.jaydon.xproject.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/12 16:01
 * 描述	      封装吐司工具类
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class ShowToastUtil {
    private static Toast mToast;

    public static void showToast(Context context, String content, int duration) {
        mToast = Toast.makeText(context, content, duration);
        mToast.show();
    }

    public static void showLongToast(Context context, String content) {
        showToast(context, content, Toast.LENGTH_LONG);
    }

    public static void showLongToast(Context context, int content) {
        showLongToast(context, String.valueOf(content));
    }

    public static void showShortToast(Context context, String content) {
        showToast(context, content, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, int content) {
        showShortToast(context, String.valueOf(content));
    }
}
