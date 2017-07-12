package com.jaydon.rxretrofit.config;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jaydon.rxretrofit.util.FileUtil;
import com.jaydon.rxretrofit.util.LogWraper;

import java.util.HashMap;

/**
 * Created by Tamic on 2016-11-07.
 */

public class ConfigLoader {

    private static Config config;

    private final static String CONFIG_NAME = "novate-config.json";

    public static boolean checkSucess(Context context, int code) {

        if(loadConfig(context) == null){
            return true;
        }
        LogWraper.v("RxRetrofit", "web :" + code + ">>>>>>>>>>>>isOk：" + config.getSucessCode().contains(String.valueOf(code)));
        return config.getSucessCode().contains(String.valueOf(code));
    }

    public static Config loadConfig(Context context) {

        if (config != null) {
            return config;
        }
        String jsonStr = FileUtil.loadFromAssets(context, CONFIG_NAME);
        if (TextUtils.isEmpty(jsonStr)) {
            LogWraper.e("RxRetrofit", "缺乏默认配置 <" + CONFIG_NAME + ">文件，请加入");
            return null;
        }
        try {
            config =  new Gson().fromJson(jsonStr, Config.class);
        } catch(JsonSyntaxException exception) {
            LogWraper.e("RxRetrofit", "loaderConfig 配置数据无法解析: 请正确配置 <" + CONFIG_NAME + ">文件");
            return null;

        }
        return config = new Gson().fromJson(jsonStr, Config.class);
    }

    public static boolean isFormat(Context context) {
        if(loadConfig(context) == null){
            return false;
        }
        return TextUtils.equals(config.getIsFormat(), "true");
    }

    public static HashMap<String, String> getErrorConfig() {
       return config.getErrorInfo();
    }

}
