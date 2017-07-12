package com.jaydon.xproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.jaydon.xproject.R;
import com.jaydon.xproject.base.BaseActivity;
import com.jaydon.xproject.delegate.GuideAndLoginDelegate;
import com.jaydon.xproject.view.CustomVideoView;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/15 22:58
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GuideAndLoginActivity extends BaseActivity<GuideAndLoginDelegate> implements View.OnClickListener {

    private CustomVideoView videoview;
    private ViewPager guide_viewpage;
    private LinearLayout indicator_layout;
    private Drawable background;

    @Override
    protected Class getDelegateClass() {
        return GuideAndLoginDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

        setVideoView();
        initView();
        setUpView();
        initEvent();
    }

    private void initView() {
        guide_viewpage = viewDelegate.get(R.id.guide_viewpage);
        indicator_layout = viewDelegate.get(R.id.indicator_layout);
    }

    private void setUpView() {
        background = guide_viewpage.getBackground();
        guide_viewpage.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
    }

    private void setVideoView() {
        //加载视频资源控件
        videoview = (CustomVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        //播放
        videoview.start();

        /**背景音乐静音*/
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0f, 0f);
            }
        });

        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });
    }

    private void initEvent() {
        viewDelegate.get(R.id.tv_look).setOnClickListener(this);
        viewDelegate.get(R.id.bt_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_look:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                /**activity切换动画*/
                overridePendingTransition(R.anim.animation_in, R.anim.animation_out);
                finish();
                break;

            case R.id.bt_login:
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                /**activity切换动画*/
                overridePendingTransition(R.anim.animation_in, R.anim.animation_out);
                break;
        }
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    @Override
    protected void onRestart() {
        setVideoView();
        super.onRestart();
    }

    @Override
    public void onStop() {
        super.onStop();
        videoview.stopPlayback();
    }
}
