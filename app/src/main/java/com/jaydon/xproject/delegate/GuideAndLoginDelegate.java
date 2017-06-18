package com.jaydon.xproject.delegate;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.jaydon.xproject.R;
import com.jaydon.xproject.activity.LoginActivity;
import com.jaydon.xproject.activity.MainActivity;
import com.jaydon.xproject.utils.BitmapUtil;
import com.jaydon.xproject.utils.BlurUtil;
import com.jaydon.xproject.utils.PublicMethod;
import com.kymjs.themvp.view.AppDelegate;

import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/15 22:59
 * 描述	      登录界面
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class GuideAndLoginDelegate extends AppDelegate implements View.OnClickListener {
    AppCompatActivity activity;
    private ImageView iv_gif;
    private Button bt;
    private ImageView iv_blur;
    private TextView tv_look;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_guidelogin;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        activity = getActivity();
//        iv_gif = (ImageView) activity.findViewById(R.id.iv_gif);
        iv_blur = (ImageView) activity.findViewById(R.id.iv_blur);
        bt = (Button) activity.findViewById(R.id.bt_login);
        tv_look = (TextView) activity.findViewById(R.id.tv_look);

        /**glide显示gif*/
//        RequestOptions option = new RequestOptions();
//        option.bitmapTransform(new BlurTransformation(PublicMethod.getContext(), 5));
//        Glide.with(PublicMethod.getContext()).load(R.mipmap.bg2).apply(option).into(iv_blur);

        locationPicBlur();
        initEvent();
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

    private void initEvent() {
        tv_look.setOnClickListener(this);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_look:
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                /**activity切换动画*/
                activity.overridePendingTransition(R.anim.animation_in, R.anim.animation_out);
                activity.finish();
                break;

            case R.id.bt_login:
                Intent login = new Intent(activity, LoginActivity.class);
                activity.startActivity(login);
                /**activity切换动画*/
                activity.overridePendingTransition(R.anim.animation_in, R.anim.animation_out);
                break;
        }

    }
}