package com.jaydon.xproject.delegate;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.jaydon.xproject.R;
import com.jaydon.xproject.activity.LoginActivity;
import com.jaydon.xproject.activity.MainActivity;
import com.jaydon.xproject.utils.BitmapUtil;
import com.jaydon.xproject.utils.BlurUtil;
import com.jaydon.xproject.utils.PublicMethod;
import com.jaydon.xproject.view.CustomVideoView;
import com.kymjs.themvp.view.AppDelegate;



/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/15 22:59
 * 描述	      登录界面
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GuideAndLoginDelegate extends AppDelegate {
    AppCompatActivity activity;
    private ImageView iv_gif;
    private Button bt;
    private ImageView iv_blur;
    private TextView tv_look;
    private CustomVideoView videoview;
    private ViewPager guide_viewpage;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_guidelogin;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        activity = getActivity();

        /**glide显示gif*/
//        RequestOptions option = new RequestOptions();
//        option.bitmapTransform(new BlurTransformation(PublicMethod.getContext(), 5));
//        Glide.with(PublicMethod.getContext()).load(R.mipmap.bg2).apply(option).into(iv_blur);

//        locationPicBlur();
        initView();
//        initEvent();
    }

    private void initView() {
        guide_viewpage = get(R.id.guide_viewpage);
    }


    /**
     * 本地图片高斯模糊
     */
    private void locationPicBlur() {
        final String pattern = "3";

        int scaleRatio = 0;
        if (TextUtils.isEmpty(pattern)) {
            scaleRatio = 0;
        } else if (scaleRatio < 0) {
            scaleRatio = 10;
        } else {
            scaleRatio = Integer.parseInt(pattern);
        }

        //        获取需要被模糊的原图bitmap
        Resources res = activity.getResources();
        Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.bg_guide3);

        //        scaledBitmap为目标图像，10是缩放的倍数（越大模糊效果越高）
        Bitmap blurBitmap = BlurUtil.toBlur(scaledBitmap, scaleRatio);
        iv_blur.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_blur.setImageBitmap(blurBitmap);
    }

}
