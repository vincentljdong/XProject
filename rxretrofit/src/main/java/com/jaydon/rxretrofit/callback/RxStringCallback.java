package com.jaydon.rxretrofit.callback;




import android.util.Log;

import okhttp3.Call;
import okhttp3.ResponseBody;

/**
 * RxStringCallback  字符串解析器
 *
 * Created by Tamic on 2017-5-30.
 * ink :https://github.com/Tamicer/Novate
 *
 */
public abstract class RxStringCallback extends ResponseCallback<String, ResponseBody> {

    @Override public String onHandleResponse(ResponseBody response) throws Exception {
        String  responseString = new String(response.bytes());
        Log.d("RxRetrofit", responseString);
        return responseString;
    }

    @Override
    public void onNext(Object tag, Call call, String response) {
      onNext(tag, response);
    }

    public abstract void onNext(Object tag, String response);
}
