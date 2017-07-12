/*
 *    Copyright (C) 2017 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.jaydon.rxretrofit.callback;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaydon.rxretrofit.RxRetrofitResponse;
import com.jaydon.rxretrofit.util.LogWraper;
import com.jaydon.rxretrofit.util.Utils;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.ResponseBody;

/**
 * RxGenericsCallback<T> 泛型回调
 * Created by Tamic on 2016/6/23.
 */
public abstract class RxGenericsCallback<T, E> extends ResponseCallback<T, E> {

    protected T dataResponse = null;
    protected int code = -1;
    protected String msg = "";
    protected String dataStr = "";
    public RxGenericsCallback() {
    }

    @Override
    public T onHandleResponse(ResponseBody response) throws Exception {
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass == String.class) {
            return (T)new String(response.bytes());
        }
        String jstring = new String(response.bytes());
        LogWraper.d("RxRetrofit", jstring);
        return transform(jstring, entityClass);
    }

    @Override
    public void onNext(final Object tag, Call call, T response) {

        if (Utils.checkMain()) {
            onNext(tag, code, msg, dataResponse);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onNext(tag, code, msg, dataResponse);
                }
            });
        }
    }

    public abstract void onNext(Object tag, int code, String message, T response);

    public T transform(String response, final Class classOfT) throws ClassCastException {

        if (classOfT == RxRetrofitResponse.class) {
            return (T) new Gson().fromJson(response, classOfT);
        }

        JSONObject jsonObject = null;
        LogWraper.e(TAG, response);
        try {
            jsonObject = new JSONObject(response);
            code = jsonObject.optInt("code");
            msg = jsonObject.optString("msg");
            if (TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("error");
            }

            if(TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("message");
            }

            dataStr = jsonObject.opt("data").toString();
            if (TextUtils.isEmpty(dataStr)) {
                dataStr = jsonObject.opt("result").toString();
            }

            if (dataStr.charAt(0) == '{') {
                dataResponse = (T) new Gson().fromJson(dataStr, classOfT);
            } else if (dataStr.charAt(0) == '[') {
                dataStr = jsonObject.optJSONArray("data").toString();
                if (TextUtils.isEmpty(dataStr)) {
                    dataStr = jsonObject.optJSONArray("result").toString();
                }
                 Type collectionType = new TypeToken<List<T>>() {
                }.getType();
                dataResponse = (T) new Gson().fromJson(dataStr,collectionType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataResponse;
    }
}
