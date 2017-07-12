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
package com.jaydon.rxretrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.jaydon.rxretrofit.config.ConfigLoader;
import com.jaydon.rxretrofit.exception.FormatException;
import com.jaydon.rxretrofit.exception.ServerException;
import com.jaydon.rxretrofit.util.LogWraper;
import com.jaydon.rxretrofit.util.ReflectionUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;

import static com.jaydon.rxretrofit.RxRetrofit.TAG;

/**
 * RxRetrofitSubscriber
 * Created by Tamic on 2016-06-06.
 * @param <T>
 */
class RxRetrofitSubscriber<T> extends BaseSubscriber<ResponseBody> {

    private RxRetrofit.ResponseCallBack<T> callBack;

    private Type finalNeedType;

    public RxRetrofitSubscriber(Context context, RxRetrofit.ResponseCallBack callBack) {
        super(context);
        this.callBack = callBack;
    }

    @Override
    public void onStart() {
        super.onStart();

        Type[] types = ReflectionUtil.getParameterizedTypeswithInterfaces(callBack);
        if (ReflectionUtil.methodHandler(types) == null || ReflectionUtil.methodHandler(types).size() == 0) {
            LogWraper.e(TAG, "callBack<T> 中T不合法: -->" + finalNeedType);
            throw new NullPointerException("callBack<T> 中T不合法");
        }
        finalNeedType = ReflectionUtil.methodHandler(types).get(0);
        // todo some common as show loadding  and check netWork is NetworkAvailable
        if (callBack != null) {
            callBack.onStart();
        }

    }

    @Override
    public void onCompleted() {
        // todo some common as  dismiss loadding
        if (callBack != null) {
            callBack.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (callBack != null) {
            callBack.onError(e);
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            byte[] bytes = responseBody.bytes();
            String jsStr = new String(bytes);
            LogWraper.d("RxRetrofit", "ResponseBody:" + jsStr.trim());
            if (!ConfigLoader.isFormat(context)) {
                callBack.onsuccess(0, "", null, jsStr);
                return;
            }
            if (callBack != null) {

                try {

                    /**
                     * if need parse baseRespone<T> use ParentType, if parse T use childType . defult parse baseRespone<T>
                     *
                     *  callBack.onSuccee((T) JSON.parseArray(jsStr, (Class<Object>) finalNeedType));
                     *  Type finalNeedType = needChildType;
                     */
                    int code = 1;
                    String msg = "";
                    String dataStr ="";
                    T dataResponse = null;
                    RxRetrofitResponse<T> baseResponse = null;

                    try {
                        JSONObject jsonObject = new JSONObject(jsStr.trim());
                        code = jsonObject.optInt("code");
                        msg = jsonObject.optString("msg");
                        baseResponse = new RxRetrofitResponse<>();
                        baseResponse.setCode(code);
                        baseResponse.setMessage(msg);
                        dataStr = jsonObject.opt("data").toString();
                        if (dataStr.isEmpty())  {
                            dataStr = jsonObject.opt("result").toString();
                        }

                        if (dataStr.isEmpty())  {
                          baseResponse.setResult(null);
                        }

                        if (!dataStr.isEmpty() && dataStr.charAt(0) == '{') {
                            dataStr = jsonObject.optJSONObject("data").toString();
                            if (dataStr.isEmpty())  {
                                dataStr = jsonObject.optJSONObject("result").toString();
                            }
                            dataResponse = (T) new Gson().fromJson(dataStr, ReflectionUtil.newInstance(finalNeedType).getClass());
                            if (ConfigLoader.isFormat(context) && dataResponse == null) {
                                LogWraper.e(TAG, "dataResponse 无法解析为:" + finalNeedType);
                                throw new FormatException();
                            }

                        } else if (dataStr.charAt(0) == '[') {
                            LogWraper.e(TAG, "data为数对象无法转换: --- " + finalNeedType);
                            //dataStr = jsonObject.optJSONArray("data").toString();
                            //dataResponse = (T) new Gson().fromJson(dataStr, finalNeedType);
                            //dataResponse = (T) new Gson().fromJson(dataStr,  ReflectionUtil.newInstance(finalNeedType).getClass());
                            throw new ClassCastException();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        LogWraper.e(TAG, e.getLocalizedMessage());
                        if (callBack != null) {
                            callBack.onError(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(e));
                        }
                    }



                    if(dataResponse != null) {
                        baseResponse.setData(dataResponse);
                    }

                    if (baseResponse.isOk(context) && dataResponse == null) {

                        LogWraper.d(TAG, "Response data 数据获取失败！");
                        callBack.onsuccess(0, "", null, jsStr);
                    }

                    if (ConfigLoader.isFormat(context) && baseResponse == null) {
                        LogWraper.e(TAG, "dataResponse 无法解析为:" + finalNeedType);
                        throw new FormatException();
                    }
                    baseResponse.setData(dataResponse);

                    if (baseResponse.isOk(context)) {
                        callBack.onsuccess(code, msg, dataResponse, jsStr);
                        callBack.onSuccee(baseResponse);

                    } else {
                        msg = baseResponse.getMsg() != null ? baseResponse.getMsg() : baseResponse.getError() != null ? baseResponse.getError() : baseResponse.getMessage() != null ? baseResponse.getMessage() : "api未知异常";

                        ServerException serverException = new ServerException(baseResponse.getCode(), msg);
                        callBack.onError(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(serverException));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    LogWraper.e(TAG, e.getLocalizedMessage().toString());
                    if (callBack != null) {
                        callBack.onError(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(new NullPointerException("Response 解析失败！")));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onError(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(e));
            }
        }
    }
}