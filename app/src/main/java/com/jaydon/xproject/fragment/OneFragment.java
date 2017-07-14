package com.jaydon.xproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.jaydon.rxretrofit.util.NetworkUtil;
import com.jaydon.xproject.R;
import com.jaydon.xproject.base.BaseFragment;
import com.jaydon.xproject.delegate.OneFragmentDelegate;
import com.jaydon.xproject.utils.PublicMethod;
import com.jaydon.xproject.utils.ShowToastUtil;
import com.jaydon.xproject.view.BaseSliderView;
import com.jaydon.xproject.view.SliderLayout;
import com.jaydon.xproject.view.TextSliderView;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/13 11:44
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class OneFragment extends BaseFragment<OneFragmentDelegate> implements BaseSliderView.OnSliderClickListener {

    private SliderLayout slider_vp;

    @Override
    protected Class<OneFragmentDelegate> getDelegateClass() {
        Log.e("1111111", "getDelegateClass()");
        return OneFragmentDelegate.class;
    }

    @Override
    protected void bindEvenListener() {
        super.bindEvenListener();

        slider_vp = viewDelegate.get(R.id.slider_vp);

        for (String name : viewDelegate.file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(PublicMethod.getContext());
            textSliderView.description(name)
                    .image(viewDelegate.file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);

            slider_vp.addSlider(textSliderView);
        }
        viewDelegate.setSliderLayoutInfo();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        ShowToastUtil.showLongToast(PublicMethod.getContext(), slider.getBundle().get("extra") + "");
    }
}
