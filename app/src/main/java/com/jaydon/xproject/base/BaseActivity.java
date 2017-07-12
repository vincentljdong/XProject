package com.jaydon.xproject.base;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaydon.xproject.R;
import com.jaydon.xproject.utils.SystemUtil;
import com.kymjs.themvp.presenter.ActivityPresenter;
import com.kymjs.themvp.view.IDelegate;

/**
 * Created by Jaydon on 2017/6/12.
 */

public abstract class BaseActivity<T extends IDelegate> extends ActivityPresenter<T> {

    private TextView mTitle;
    private RelativeLayout rl_bg;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        super.setContentView(R.layout.activity_title);

        /**
         * 状态栏沉浸色
         *
         * 自5.0引入 Material Design ,状态栏对开发者更加直接,
         * 可以直接调用 setStatusBarColor 来设置状态栏的颜色.
         * */
        setImmersionColor();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * 子类可自定义标题栏标题
     */
    public void setTitle(String title) {
//        mTitle.setText(title);
    }

    /**
     * 子类可自定义标题栏颜色
     */
    public void setToolBarBackground(String color) {
        rl_bg.setBackgroundColor(Color.parseColor(color));
    }

    private void setImmersionColor() {
//        ShowToastUtil.showLongToast(PublicMethod.getContext(), Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    localLayoutParams.flags);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Base Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
