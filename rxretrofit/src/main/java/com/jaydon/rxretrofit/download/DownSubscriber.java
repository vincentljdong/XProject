/*
 *    Copyright (C) 2016 Tamic
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
package com.jaydon.rxretrofit.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.jaydon.rxretrofit.BaseSubscriber;
import com.jaydon.rxretrofit.Throwable;
import com.jaydon.rxretrofit.util.LogWraper;
import com.jaydon.rxretrofit.util.Utils;

/**
 * DownSubscriber
 * Created by Tamic on 2016-08-03.
 */
public class DownSubscriber <ResponseBody extends okhttp3.ResponseBody> extends BaseSubscriber<ResponseBody> {
    private DownLoadCallBack callBack;
    private Context context;
    private String path;
    private String name;
    private String key;

    public DownSubscriber(String key, String path, String name, DownLoadCallBack callBack, Context context) {
        super(context);
        this.key = key;
        this.path = path;
        this.name = name;
        this.callBack = callBack;
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack != null) {
           /* if (TextUtils.isEmpty(key)) {
                key = FileUtil.generateFileKey(path, name);
            }*/
            callBack.onStart(key);
        }
    }

    @Override
    public void onCompleted() {
        if (callBack != null) {
            callBack.onCompleted();
        }
    }

    @Override
    public void onError(final Throwable e) {
        LogWraper.e(RxRetrofitDownLoadManager.TAG, "DownSubscriber:>>>> onError:" + e.getMessage());
        if (callBack != null) {
            final Throwable throwable =  new Throwable(e, -100, e.getMessage());
            if (Utils.checkMain()) {
                callBack.onError(throwable);
            } else {
                 new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onError(throwable);
                    }
                });
            }
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        LogWraper.d(RxRetrofitDownLoadManager.TAG, "DownSubscriber:>>>> onNext");
        new RxRetrofitDownLoadManager(callBack).writeResponseBodyToDisk(key, path, name, context, responseBody);

    }
}
