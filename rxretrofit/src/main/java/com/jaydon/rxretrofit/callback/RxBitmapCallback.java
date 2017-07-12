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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.ResponseBody;

/**
 * RxBitmapCallback Bitmap回调
 * Created by Tamic on 2017-05-02.
 * ink :https://github.com/Tamicer/Novate
 */
public abstract class RxBitmapCallback extends ResponseCallback<Bitmap, ResponseBody> {
    @Override
    public Bitmap onHandleResponse(ResponseBody response) throws IOException {
        return transform(response, Bitmap.class);
    }

    @Override
    public Bitmap transform(ResponseBody response, Class classOfT) {
        return BitmapFactory.decodeStream(response.byteStream());
    }

    @Override
    public void onNext(Object tag, Call call, Bitmap response) {
        onNext(tag, response);
    }

    public abstract void onNext(Object tag, Bitmap response);
}
