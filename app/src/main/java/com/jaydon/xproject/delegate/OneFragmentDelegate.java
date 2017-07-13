package com.jaydon.xproject.delegate;

import com.jaydon.xproject.R;
import com.jaydon.xproject.animations.DescriptionAnimation;
import com.jaydon.xproject.view.SliderLayout;
import com.jaydon.xproject.view.ViewPagerEx;
import com.kymjs.themvp.view.AppDelegate;

import java.io.File;
import java.util.HashMap;


/**
 * 创建者     Jaydon Li
 * 创建时间   2017/6/13 11:49
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class OneFragmentDelegate extends AppDelegate implements ViewPagerEx.OnPageChangeListener {

    public HashMap<String, Integer> file_maps;

    @Override
    public int getRootLayoutId() {
        return R.layout.fragment_layout_home;
    }

    @Override
    public void initWidget() {
        super.initWidget();

        HashMap<String, String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        /**装本地图片的map*/
        file_maps = new HashMap<>();
        file_maps.put("Hannibal", R.mipmap.bg_hannibal);
        file_maps.put("Big Bang Theory", R.mipmap.bg_big_bang);
        file_maps.put("House of Cards", R.mipmap.bg_house);
        file_maps.put("Game of Thrones", R.mipmap.bg_game_of_thrones);

//        setSliderLayoutInfo();
    }

    /**
     * 配置viewpager（SliderLayout）相关信息
     */
    public void setSliderLayoutInfo() {
        SliderLayout slider_vp = get(R.id.slider_vp);
        /**控制轮播图显示效果*/
        slider_vp.setPresetTransformer(SliderLayout.Transformer.Accordion);
        /**指示点底部显示*/
        slider_vp.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider_vp.setCustomAnimation(new DescriptionAnimation());
        slider_vp.setDuration(3000);
        slider_vp.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
