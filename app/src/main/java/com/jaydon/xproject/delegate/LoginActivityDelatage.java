package com.jaydon.xproject.delegate;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.jaydon.xproject.R;
import com.jaydon.xproject.utils.BlurUtil;
import com.kymjs.themvp.view.AppDelegate;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/16 17:21
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class LoginActivityDelatage extends AppDelegate implements View.OnClickListener {

    private AppCompatActivity activity;
    private ImageView iv_login;
    private ImageView iv_back;

    @Override
    public int getRootLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        activity = getActivity();
        initView();
        locationPicBlur();
        initEvent();
    }

    private void initView() {
        iv_login = (ImageView) activity.findViewById(R.id.iv_login_bg);
        iv_back = (ImageView) activity.findViewById(R.id.iv_back);
    }

    private void initEvent() {
        iv_back.setOnClickListener(this);
    }

    /**
     * 本地图片高斯模糊
     */
    private void locationPicBlur() {
        final String pattern = "8";

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
        Bitmap scaledBitmap = BitmapFactory.decodeResource(res, R.mipmap.bg_login);

        //        scaledBitmap为目标图像，10是缩放的倍数（越大模糊效果越高）
        Bitmap blurBitmap = BlurUtil.toBlur(scaledBitmap, scaleRatio);
        iv_login.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_login.setImageBitmap(blurBitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                activity.finish();
                activity.overridePendingTransition(R.anim.animation_in, R.anim.animation_out);
                break;
        }
    }
}
