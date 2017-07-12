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

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.jaydon.rxretrofit.callback.ResponseCallback;
import com.jaydon.rxretrofit.cookie.RxRetrofitCookieManager;
import com.jaydon.rxretrofit.cache.CookieCacheImpl;
import com.jaydon.rxretrofit.download.DownLoadCallBack;
import com.jaydon.rxretrofit.download.DownSubscriber;
import com.jaydon.rxretrofit.cookie.SharedPrefsCookiePersistor;
import com.jaydon.rxretrofit.request.RxRetrofitRequest;
import com.jaydon.rxretrofit.util.FileUtil;
import com.jaydon.rxretrofit.util.LogWraper;
import com.jaydon.rxretrofit.util.Utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.Part;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RxRetrofit adapts a Java interface to Retrofit call by using annotations on the declared methods to
 * define how requests are made. Create instances using {@linkplain Builder
 * the builder} and pass your interface to {@link #} to generate an implementation.
 * <p/>
 * For example,
 * <pre>{@code
 * RxRetrofit novate = new RxRetrofit.Builder()
 *     .baseUrl("http://api.example.com")
 *     .addConverterFactory(GsonConverterFactory.create())
 *     .build();
 * <p/>
 * MyApi api = RxRetrofit.create(MyApi.class);
 * Response<User> user = api.getUser().execute();
 * }</pre>
 *
 * @author Tamic (skay5200@163.com)
 */
public final class RxRetrofit {

    private static Map<String, String> headers;
    private static Map<String, String> parameters;
    private static Retrofit.Builder retrofitBuilder;
    private static Retrofit retrofit;
    private static OkHttpClient.Builder okhttpBuilder;
    public static BaseApiService apiManager;
    private static OkHttpClient okHttpClient;
    private static Context mContext;
    private final okhttp3.Call.Factory callFactory;
    private final String baseUrl;
    private final List<Converter.Factory> converterFactories;
    private final List<CallAdapter.Factory> adapterFactories;
    private final Executor callbackExecutor;
    private final boolean validateEagerly;
    private Observable<ResponseBody> downObservable;
    private Map<String, Observable<ResponseBody>> downMaps = new HashMap<String, Observable<ResponseBody>>() {
    };
    private Observable.Transformer exceptTransformer = null;
    public static final String TAG = "RxRetrofit";

    /**
     * Mandatory constructor for the RxRetrofit
     */
    RxRetrofit(okhttp3.Call.Factory callFactory, String baseUrl, Map<String, String> headers,
               Map<String, String> parameters, BaseApiService apiManager,
               List<Converter.Factory> converterFactories, List<CallAdapter.Factory> adapterFactories,
               Executor callbackExecutor, boolean validateEagerly) {
        this.callFactory = callFactory;
        this.baseUrl = baseUrl;
        this.headers = headers;
        this.parameters = parameters;
        this.apiManager = apiManager;
        this.converterFactories = converterFactories;
        this.adapterFactories = adapterFactories;
        this.callbackExecutor = callbackExecutor;
        this.validateEagerly = validateEagerly;
    }

    /**
     * create ApiService
     */
    public <T> T create(final Class<T> service) {

        return retrofit.create(service);
    }

    /**
     * @param subscriber
     */
    public <T> T call(Observable<T> observable, BaseSubscriber<T> subscriber) {
        return (T) observable.compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * @param subscriber
     */
    public <T> T execute(RxRetrofitRequest request, BaseSubscriber<T> subscriber) {
        return handleCall(request, subscriber);
    }

    private <T> T handleCall(RxRetrofitRequest request, BaseSubscriber<T> subscriber) {
        //todo dev
     return null;
    }

    /**
     * RxRetrofit execute get
     * <p>
     * return parsed data
     * <p>
     * you don't need to parse ResponseBody
     */
    public <T> T executeGet(final String url, final Map<String, Object> maps, final ResponseCallBack<T> callBack) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }


    /**
     * RxRetrofit execute get request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxGet(final String url, final Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxGet(url, url, maps, callBack);
    }


    /**
     * RxRetrofit execute get request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxGet(String tag, final String url, final Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     * RxRetrofit execute post request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPost(final String url, @FieldMap(encoded = true) Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxPost(url, url, maps, callBack);
    }

    /**
     * RxRetrofit execute Post request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPost(String tag, final String url, @FieldMap(encoded = true) Map<String, Object> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executePost(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }


    /**
     * RxRetrofit execute Put request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPut(final String url, final @FieldMap(encoded = true) Map<String, T>  maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxPut(url, url, maps, callBack);
    }


    /**
     * RxRetrofit execute Put request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxPut(String tag, final String url, final @FieldMap(encoded = true) Map<String, T>  maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executePut(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     * RxRetrofit execute Delete request
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxDelete(final String url, final Map<String, T> maps, ResponseCallback<T, ResponseBody> callBack) {
        return rxDelete(url, url, maps, callBack);
    }


    /**
     * RxRetrofit execute Delete request
     * @param tag request tag
     * @param url path or url
     * @param maps parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T rxDelete(String tag, final String url, final Map<String, T> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executeDelete(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     *  RxRetrofit RxUpload by post With Part
     * @param url path or url
     * @param requestBody  requestBody
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxUploadWithPart(String url, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return RxUploadWithPart(url, url, requestBody, callBack);
    }

    /**
     * RxRetrofit RxUpload by post With Part
     * @param tag request tag
     * @param url path or url
     * @param requestBody requestBody
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxUploadWithPart(Object tag, String url, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlieWithPart(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     *  RxRetrofit RxUpload by post
     * @param url path or url
     * @param description description
     * @param requestBody  requestBody
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxUpload(String url, RequestBody description, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return RxUpload(url, url, description, requestBody, callBack);
    }

    /**
     * RxRetrofit RxUpload by post
     * @param tag request tag
     * @param url path or url
     * @param description description
     * @param requestBody     MultipartBody.Part
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Rxjava Subscription
     */
    public <T> T RxUpload(Object tag, String url, RequestBody description, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlie(url, description, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     *  RxRetrofit RxUpload by post With Body
     * @param url url
     * @param requestBody requestBody
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T RxUploadWithBody(String url, RequestBody requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return RxUploadWithBody(url, url, requestBody, callBack);
    }


    /**
     *  RxRetrofit RxUpload by post With Body
     * @param tag tag
     * @param url url
     * @param requestBody requestBody
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T RxUploadWithBody(Object tag, String url, RequestBody requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.postRequestBody(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     *  RxRetrofit RxUpload by post Body Maps
     * @param url url
     * @param maps RequestBody files
     * @param callBack back
     * @param <T>
     * @return Rxjava Subscription
     */
    public <T> T RxUploadWithBodyMaps(String url, Map<String, RequestBody> maps, ResponseCallback<T, ResponseBody> callBack) {
        return RxUploadWithBodyMaps(url, url,maps, callBack);
    }


    /**
     * RxRetrofit RxUpload by post With BodyMaps
     * @param tag tag
     * @param url url
     * @param maps RequestBody files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjav 1.x Subscription
     */
    public <T> T RxUploadWithBodyMaps(Object tag, String url, Map<String, RequestBody> maps, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFiles(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     * RxRetrofit RxUpload by post With BodyMaps
     * @param url url
     * @param files  MultipartBody.Part files
     * @param callBack
     * @param <T>
     * @return Rxjav 1.x Subscription
     */
    public <T> T RxUploadWithPartMap(String url, Map<String, MultipartBody.Part> files, ResponseCallback<T, ResponseBody> callBack) {
        return RxUploadWithPartMap(url, url, files, callBack);
    }

    /**
     * RxRetrofit RxUpload by post With BodyMaps
     * @param tag tag
     * @param url url
     * @param files MultipartBody.Part files
     * @param callBack ResponseCallback
     * @param <T>
     * @return Rxjav 1.x Subscription
     */
    public <T> T RxUploadWithPartMap(Object tag, String url, Map<String, MultipartBody.Part> files, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlieWithPartMap(url, files)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     * RxRetrofit Post by Form
     * @param url path or url
     * @param parameters  parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Rxjav 1.x Subscription
     */
    public <T> T RxForm(String url, @FieldMap(encoded = true) Map<String, Object> parameters, ResponseCallback<T, ResponseBody> callBack) {
        return RxForm(url, url, parameters, callBack);
    }

    /**
     * RxRetrofit Post by Form
     * @param tag request tag
     * @param url path or url
     * @param parameters  parameters  maps
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxForm(Object tag, String url, @FieldMap(encoded = true) Map<String, Object> parameters, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.postForm(url, parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }

    /**
     * RxRetrofit  Post by Body
     * @param url path or url
     * @param bean Object bean
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxBody(String url, Object bean, ResponseCallback<T, ResponseBody> callBack) {
        return RxBody(url, url, bean, callBack);
    }


    /**
     * RxRetrofit  Post by Body
     * @param tag request tag
     * @param url path or url
     * @param bean Object bean
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxBody(Object tag, String url, Object bean,  ResponseCallback<T, ResponseBody> callBack) {
       return (T) apiManager.executePostBody(url, bean)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }


    /**
     * RxRetrofit  Post by Json
     * @param url path or url
     * @param jsonString   jsonString
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxJson(String url, String jsonString, ResponseCallback<T, ResponseBody> callBack) {
        return RxJson(url, url, jsonString, callBack);
    }


    /**
     * RxRetrofit  Post by Json
     * @param tag request tag
     * @param url path or url
     * @param callBack  ResponseCallback
     * @param <T>  T return parsed data
     * @return Subscription
     */
    public <T> T RxJson(Object tag, String url, String jsonString, ResponseCallback<T, ResponseBody> callBack) {
        return (T)apiManager.postRequestBody(url, Utils.createJson(jsonString))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack));
    }


    /**
     * RxRetrofit execute get
     * <p>
     * return parsed data
     * <p>
     * you don't need to parse ResponseBody
     */
    public <T> T executeGet(Class<T> entityClass, final String url, final Map<String, Object> maps, final ResponseCallBack<T> callBack) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext,callBack));
    }


    /**
     * RXJAVA schedulersTransformer
     * AndroidSchedulers.mainThread()
     */
    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * RXJAVA schedulersTransformer
     *
     * Schedulers.io()
     */
    final Observable.Transformer schedulersTransformerDown = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    };

    /**
     * handleException Transformer
     * @param <T>
     * @return  Transformer
     */
    public <T> Observable.Transformer<RxRetrofitResponse<T>, T> handleErrTransformer() {

        if (exceptTransformer != null) return exceptTransformer;

        else return exceptTransformer = new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable)/*.map(new HandleFuc<T>())*/.onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }


    /**
     * HttpResponseFunc
     * @param <T> Observable
     */
    private static class HttpResponseFunc<T> implements Func1<java.lang.Throwable, Observable<T>> {
        @Override
        public Observable<T> call(java.lang.Throwable t) {
            return Observable.error(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(t));
        }
    }

    /**  T
     * @param <T> response
     */
    private class HandleFuc<T> implements Func1<RxRetrofitResponse<T>, T> {
        @Override
        public T call(RxRetrofitResponse<T> response) {
            if (response == null || (response.getData() == null && response.getResult() == null)) {
                throw new JsonParseException("后端数据不对");
            }
            /*if (!response.isOk()) {
                throw new RuntimeException(response.getCode() + "" + response.getMsg() != null ? response.getMsg() : "");
            }
*/
            return response.getData();
        }
    }

    /**
     * Retroift get
     *
     * @param url
     * @param maps
     * @param subscriber
     * @param <T>
     * @return no parse data
     */
    public <T> T get(String url, Map<String, Object> maps, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * /**
     * RxRetrofit executePost
     *
     * @return no parse data
     * <p>
     * you must to be parse ResponseBody
     * <p>
     * <p/>
     * For example,
     * <pre>{@code
     * RxRetrofit novate = new RxRetrofit.Builder()
     *     .baseUrl("http://api.example.com")
     *     .addConverterFactory(GsonConverterFactory.create())
     *     .build();
     *
     * novate.post("url", parameters, new BaseSubscriber<ResponseBody>(context) {
     *    @Override
     *   public void onError(Throwable e) {
     *
     *   }
     *
     *  @Override
     *  public void onNext(ResponseBody responseBody) {
     *
     *   // todo you need to parse responseBody
     *
     *  }
     *  });
     * <p/>
     *
     * }</pre>
     */
    public <T> T post(String url, @FieldMap(encoded = true) Map<String, Object> parameters, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePost(url, (Map<String, Object>) parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit executePost
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executePost(final String url, @FieldMap(encoded = true) Map<String, Object> parameters, final ResponseCallBack<T> callBack) {

        return (T) apiManager.executePost(url, parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }


    /**
     * RxRetrofit Post by Form
     *
     * @param url
     * @param subscriber
     */
    public <T> T form(String url, @FieldMap(encoded = true) Map<String, Object> fields, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postForm(url, fields)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }


    /**
     * RxRetrofit execute Post by Form
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeForm(final String url, final @FieldMap(encoded = true) Map<String, Object> fields, final ResponseCallBack<T> callBack) {
        return (T) apiManager.postForm(url, fields)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }


    /**
     * http Post by Body
     * you  need to parse ResponseBody
     *
     * @param url
     * @param subscriber
     */
    public void body(String url, Object body, Subscriber<ResponseBody> subscriber) {
        apiManager.executePostBody(url, body)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * http execute Post by body
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeBody(final String url, final Object body, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();
            return (T) apiManager.executePostBody(url, body)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }


    /**
     * http Post by json
     * you  need to parse ResponseBody
     *
     * @param url
     * @param jsonStr    Json String
     * @param subscriber
     */
    public<T> T  json(String url, String jsonStr, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, Utils.createJson(jsonStr))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * http execute Post by Json
     *
     * @param url
     * @param jsonStr Json String
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeJson(final String url, final String jsonStr, final ResponseCallBack<T> callBack) {
            return (T) apiManager.postRequestBody(url, Utils.createJson(jsonStr))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }

    /**
     * RxRetrofit delete
     *
     * @param url
     * @param maps
     * @param subscriber
     * @param <T>
     * @return no parse data
     */
    public <T> T delete(String url, Map<String, T> maps, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executeDelete(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit Execute http by Delete
     *
     * @return parsed data
     * you don't need to   parse ResponseBody
     */
    public <T> T executeDelete(final String url, final Map<String, T> maps, final ResponseCallBack<T> callBack) {
        final Type[] types = callBack.getClass().getGenericInterfaces();

        return (T) apiManager.executeDelete(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }

    /**
     * RxRetrofit put
     *
     * @param url
     * @param parameters
     * @param subscriber
     * @param <T>
     * @return no parse data
     */
    public <T> T put(String url, final @FieldMap(encoded = true) Map<String, T> parameters, BaseSubscriber<ResponseBody> subscriber) {
        return (T) apiManager.executePut(url, (Map<String, Object>) parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit Execute  Http by Put
     *
     * @return parsed data
     * you don't need to parse ResponseBody
     */
    public <T> T executePut(final String url, final @FieldMap(encoded = true) Map<String, T> parameters, final ResponseCallBack<T> callBack) {

        return (T) apiManager.executePut(url, (Map<String, Object>) parameters)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxRetrofitSubscriber<T>(mContext, callBack));
    }


    /**
     * RxRetrofit Test
     *
     * @param url        url
     * @param maps       maps
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T test(String url, Map<String, T> maps, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.getTest(url, (Map<String, Object>) maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit upload
     *
     * @param url
     * @param requestBody requestBody
     * @param subscriber  subscriber
     * @param <T>         T
     * @return
     */
    public <T> T upload(String url, RequestBody requestBody, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * uploadImage
     *
     * @param url        url
     * @param file       file
     * @param subscriber
     * @param <T>
     * @return
     */
    public <T> T uploadImage(String url, File file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.upLoadImage(url, Utils.createImage(file))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit upload Flie
     *
     * @param url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T uploadFlie(String url, RequestBody file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit upload Flie
     *
     * @param url
     * @param file       file
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T uploadFlie(String url, RequestBody description, MultipartBody.Part file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFlie(url, description, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**
     * RxRetrofit upload Flies
     *
     * @param url
     * @param subscriber subscriber
     * @param <T>        T
     * @return
     */
    public <T> T uploadFlies(String url, Map<String, RequestBody> files, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFiles(url, files)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }


    /**
     * RxRetrofit upload Flies WithPartMap
     * @param url
     * @param partMap
     * @param file
     * @param subscriber
     * @param <T>
     * @return
     */
    public <T> T uploadFileWithPartMap(String url, Map<String, RequestBody> partMap,
                                       @Part("file") MultipartBody.Part file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFileWithPartMap(url, partMap, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }


    /**
     * RxRetrofit download
     *
     * @param url
     * @param callBack
     */
    public <T> T download(String url, DownLoadCallBack callBack) {
        return download(url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * @param url
     * @param name
     * @param callBack
     */
    public <T> T download(String url, String name, DownLoadCallBack callBack) {
        return download(FileUtil.generateFileKey(url, name), url, null, name, callBack);
    }

    /**
     * downloadMin
     *
     * @param url
     * @param callBack
     */
    public <T> T  downloadMin(String url, DownLoadCallBack callBack) {
        return downloadMin(FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url)), url, callBack);
    }

    /**
     * downloadMin
     * @param key  key
     * @param url url
     * @param callBack CallBack
     */
    public <T> T downloadMin(String key, String url, DownLoadCallBack callBack) {
        return downloadMin(key, url, FileUtil.getFileNameWithURL(url), callBack);
    }

    /**
     * downloadMin
     * @param key key
     * @param url down url
     * @param name name
     * @param callBack callBack
     */
    public <T> T downloadMin(String key, String url, String name, DownLoadCallBack callBack) {
        return downloadMin(key, url, null, name, callBack);
    }

    /**
     * download small file
     * @param key
     * @param url
     * @param savePath
     * @param name
     * @param callBack
     */
    public <T> T  downloadMin(String key, String url, String savePath, String name, DownLoadCallBack callBack) {

        if(TextUtils.isEmpty(key)) {
            key = FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url));
        }

        if (downMaps.get(key) == null) {
            downObservable = apiManager.downloadSmallFile(url);
        } else {
            downObservable = downMaps.get(key);
        }
        downMaps.put(key, downObservable);
        return executeDownload(key, savePath, name, callBack);
    }


    /**
     * @param key
     * @param url
     * @param savePath
     * @param name
     * @param callBack
     */
    public <T> T download(String key, String url, String savePath, String name, DownLoadCallBack callBack) {
        if(TextUtils.isEmpty(key)) {
            key = FileUtil.generateFileKey(url, FileUtil.getFileNameWithURL(url));
        }
        if (downMaps.get(key) == null) {
            downObservable = apiManager.downloadFile(url);
        } else {
            downObservable = downMaps.get(url);
        }
        downMaps.put(key, downObservable);
        return executeDownload(key, savePath, name, callBack);
    }

    /**
     * executeDownload
     * @param key
     * @param savePath
     * @param name
     * @param callBack
     */
    private <T> T executeDownload(String key, String savePath, String name, DownLoadCallBack callBack) {
        /*if (RxRetrofitDownLoadManager.isDownLoading) {
            downMaps.get(key).unsubscribeOn(Schedulers.io());
            RxRetrofitDownLoadManager.isDownLoading = false;
            RxRetrofitDownLoadManager.isCancel = true;
            return;
        }*/
        //RxRetrofitDownLoadManager.isDownLoading = true;
        if(downMaps.get(key)!= null) {
            return (T) downMaps.get(key).compose(schedulersTransformerDown)
                    .compose(handleErrTransformer())
                    .subscribe(new DownSubscriber<ResponseBody>(key, savePath, name, callBack, mContext));
        }

        return null;

    }

    /**
     * Mandatory Builder for the Builder
     */
    public static final class Builder {

        private static final int DEFAULT_TIMEOUT = 5;
        private static final int DEFAULT_MAXIDLE_CONNECTIONS = 5;
        private static final long DEFAULT_KEEP_ALIVEDURATION = 8;
        private static final long caheMaxSize = 10 * 1024 * 1024;

        private okhttp3.Call.Factory callFactory;
        private String baseUrl;
        private Boolean isLog = false;
        private Boolean isCookie = false;
        private Boolean isCache = true;
        private List<InputStream> certificateList;
        private HostnameVerifier hostnameVerifier;
        private CertificatePinner certificatePinner;
        private List<Converter.Factory> converterFactories = new ArrayList<>();
        private List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        private Executor callbackExecutor;
        private boolean validateEagerly;
        private Context context;
        private RxRetrofitCookieManager cookieManager;
        private Cache cache = null;
        private Proxy proxy;
        private File httpCacheDirectory;
        private SSLSocketFactory sslSocketFactory;
        private ConnectionPool connectionPool;
        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE;

        public Builder(Context context) {
            // Add the base url first. This prevents overriding its behavior but also
            // ensures correct behavior when using novate that consume all types.
            okhttpBuilder = new OkHttpClient.Builder();
            retrofitBuilder = new Retrofit.Builder();
            if(context instanceof Activity) {
                this.context  = ((Activity) context).getApplicationContext();
            } else {
                this.context = context;
            }
        }

        /**
         * The HTTP client used for requests. default OkHttpClient
         * <p/>
         * This is a convenience method for calling {@link #callFactory}.
         * <p/>
         * Note: This method <b>does not</b> make a defensive copy of {@code client}. Changes to its
         * settings will affect subsequent requests. Pass in a {@linkplain OkHttpClient#clone() cloned}
         * instance to prevent this if desired.
         */
        @NonNull
        public Builder client(OkHttpClient client) {
            retrofitBuilder.client(Utils.checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * Add ApiManager for serialization and deserialization of objects.
         *//*
        public Builder addApiManager(final Class<ApiManager> service) {

            apiManager = retrofit.create((Utils.checkNotNull(service, "apiManager == null")));
            //return retrofit.create(service);
            return this;
        }*/

        /**
         * Specify a custom call factory for creating {@link } instances.
         * <p/>
         * Note: Calling {@link #client} automatically sets this value.
         */
        public Builder callFactory(okhttp3.Call.Factory factory) {
            this.callFactory = Utils.checkNotNull(factory, "factory == null");
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder connectTimeout(int timeout) {
            return connectTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder writeTimeout(int timeout) {
            return writeTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * open default logcat
         *
         * @param isLog
         * @return
         */
        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        /**
         * open sync default Cookie
         *
         * @param isCookie
         * @return
         */
        public Builder addCookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }

        /**
         * open default Cache
         *
         * @param isCache
         * @return
         */
        public Builder addCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        public Builder proxy(Proxy proxy) {
            okhttpBuilder.proxy(Utils.checkNotNull(proxy, "proxy == null"));
            return this;
        }

        /**
         * Sets the default write timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder writeTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.writeTimeout(timeout, unit);
            } else {
                okhttpBuilder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }

        /**
         * Sets the connection pool used to recycle HTTP and HTTPS connections.
         * <p>
         * <p>If unset, a new connection pool will be used.
         */
        public Builder connectionPool(ConnectionPool connectionPool) {
            if (connectionPool == null) throw new NullPointerException("connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder connectTimeout(int timeout, TimeUnit unit) {
            if (timeout != -1) {
                okhttpBuilder.connectTimeout(timeout, unit);
            } else {
                okhttpBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return this;
        }


        /**
         * Set an API base URL which can change over time.
         *
         * @see BaseUrl(HttpUrl)
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Utils.checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * Add converter factory for serialization and deserialization of objects.
         */
        public Builder addConverterFactory(Converter.Factory factory) {
            this.converterFactory = factory;
            return this;
        }

        /**
         * Add a call adapter factory for supporting service method return types other than {@link CallAdapter
         * }.
         */
        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            this.callAdapterFactory = factory;
            return this;
        }

        /**
         * Add Header for serialization and deserialization of objects.
         */
        public <T> Builder addHeader(Map<String, T> headers) {
            okhttpBuilder.addInterceptor(new BaseInterceptor(Utils.checkNotNull(headers, "header == null")));
            return this;
        }

        /**
         * Add parameters for serialization and deserialization of objects.
         */
        public <T> Builder addParameters(Map<String, T> parameters) {
            okhttpBuilder.addInterceptor(new BaseInterceptor(Utils.checkNotNull(parameters, "parameters == null")));
            return this;
        }

        /**
         * Returns a modifiable list of interceptors that observe a single network request and response.
         * These interceptors must call {@link Interceptor.Chain#proceed} exactly once: it is an error
         * for a network interceptor to short-circuit or repeat a network request.
         */
        public Builder addInterceptor(Interceptor interceptor) {
            okhttpBuilder.addInterceptor(Utils.checkNotNull(interceptor, "interceptor == null"));
            return this;
        }

        /**
         * The executor on which {@link Call} methods are invoked when returning {@link Call} from
         * your service method.
         * <p/>
         * Note: {@code executor} is not used for {@linkplain #addCallAdapterFactory custom method
         * return types}.
         */
        public Builder callbackExecutor(Executor executor) {
            this.callbackExecutor = Utils.checkNotNull(executor, "executor == null");
            return this;
        }

        /**
         * When calling {@link #create} on the resulting {@link Retrofit} instance, eagerly validate
         * the configuration of all methods in the supplied interface.
         */
        public Builder validateEagerly(boolean validateEagerly) {
            this.validateEagerly = validateEagerly;
            return this;
        }

        /**
         * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
         * outgoing HTTP requests.
         * <p/>
         * <p>If unset, {@linkplain RxRetrofitCookieManager#NO_COOKIES no cookies} will be accepted nor provided.
         */
        public Builder cookieManager(RxRetrofitCookieManager cookie) {
            if (cookie == null) throw new NullPointerException("cookieManager == null");
            this.cookieManager = cookie;
            return this;
        }

        /**
         *
         */
        public Builder addSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public Builder addHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public Builder addCertificatePinner(CertificatePinner certificatePinner) {
            this.certificatePinner = certificatePinner;
            return this;
        }


        /**
         * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
         * outgoing HTTP requests.
         * <p/>
         * <p>If unset, {@linkplain RxRetrofitCookieManager#NO_COOKIES no cookies} will be accepted nor provided.
         */
        public Builder addSSL(String[] hosts, int[] certificates) {
            if (hosts == null) throw new NullPointerException("hosts == null");
            if (certificates == null) throw new NullPointerException("ids == null");


            addSSLSocketFactory(RxRetrofitHttpsFactroy.getSSLSocketFactory(context, certificates));
            addHostnameVerifier(RxRetrofitHttpsFactroy.getHostnameVerifier(hosts));
            return this;
        }

        public Builder addNetworkInterceptor(Interceptor interceptor) {
            okhttpBuilder.addNetworkInterceptor(interceptor);
            return this;
        }

        /**
         * setCache
         *
         * @param cache cahe
         * @return Builder
         */
        public Builder addCache(Cache cache) {
            int maxStale = 60 * 60 * 24 * 3;
            return addCache(cache, maxStale);
        }

        /**
         * @param cache
         * @param cacheTime ms
         * @return
         */
        public Builder addCache(Cache cache, final int cacheTime) {
            addCache(cache, String.format("max-age=%d", cacheTime));
            return this;
        }

        /**
         * @param cache
         * @param cacheControlValue Cache-Control
         * @return
         */
        private Builder addCache(Cache cache, final String cacheControlValue) {
            REWRITE_CACHE_CONTROL_INTERCEPTOR = new CacheInterceptor(mContext, cacheControlValue);
            REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE = new CacheInterceptorOffline(mContext, cacheControlValue);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            this.cache = cache;
            return this;
        }

        /**
         * Create the {@link Retrofit} instance using the configured values.
         * <p/>
         * Note: If neither {@link #client} nor {@link #callFactory} is called a default {@link
         * OkHttpClient} will be created and used.
         */
        public RxRetrofit build() {

            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            if (okhttpBuilder == null) {
                throw new IllegalStateException("okhttpBuilder required.");
            }

            if (retrofitBuilder == null) {
                throw new IllegalStateException("retrofitBuilder required.");
            }
            /** set Context. */
            mContext = context;

            //ConfigLoader.loadConfig(mContext);
            /**
             * Set a fixed API base URL.
             *
             * @see #baseUrl(HttpUrl)
             */
            retrofitBuilder.baseUrl(baseUrl);

            /** Add converter factory for serialization and deserialization of objects. */
            if (converterFactory == null) {
                converterFactory = GsonConverterFactory.create();
            }
            ;

            retrofitBuilder.addConverterFactory(converterFactory);
            /**
             * Add a call adapter factory for supporting service method return types other than {@link
             * Call}.
             */
            if (callAdapterFactory == null) {
                callAdapterFactory = RxJavaCallAdapterFactory.create();
            }
            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            LogWraper.setDebug(isLog && !BuildConfig.DEBUG);

            if (isLog) {
                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));

                okhttpBuilder.addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            if (sslSocketFactory != null) {
                okhttpBuilder.sslSocketFactory(sslSocketFactory);
            }

            if (hostnameVerifier != null) {
                okhttpBuilder.hostnameVerifier(hostnameVerifier);
            }


            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(mContext.getCacheDir(), "Novate_Http_cache");
            }

            if (isCache) {
                try {
                    if (cache == null) {
                        cache = new Cache(httpCacheDirectory, caheMaxSize);
                    }

                    addCache(cache);

                } catch (Exception e) {
                    Log.e("OKHttp", "Could not create http cache", e);
                }
                if (cache == null) {
                    cache = new Cache(httpCacheDirectory, caheMaxSize);
                }
            }

            if (cache != null) {
                okhttpBuilder.cache(cache);
            }

            /**
             * Sets the connection pool used to recycle HTTP and HTTPS connections.
             *
             * <p>If unset, a new connection pool will be used.
             */
            if (connectionPool == null) {

                connectionPool = new ConnectionPool(DEFAULT_MAXIDLE_CONNECTIONS, DEFAULT_KEEP_ALIVEDURATION, TimeUnit.SECONDS);
            }
            okhttpBuilder.connectionPool(connectionPool);

            /**
             * Sets the HTTP proxy that will be used by connections created by this client. This takes
             * precedence over {@link #proxySelector}, which is only honored when this proxy is null (which
             * it is by default). To disable proxy use completely, call {@code setProxy(Proxy.NO_PROXY)}.
             */
            if (proxy == null) {
                okhttpBuilder.proxy(proxy);
            }

            /**
             * Sets the handler that can accept cookies from incoming HTTP responses and provides cookies to
             * outgoing HTTP requests.
             *
             * <p>If unset, {@link RxRetrofit RxRetrofitCookieManager#NO_COOKIES no cookies} will be accepted nor provided.
             */
            if (isCookie && cookieManager == null) {
                //okhttpBuilder.cookieJar(new NovateCookieManger(context));
                okhttpBuilder.cookieJar(new RxRetrofitCookieManager(new CookieCacheImpl(), new SharedPrefsCookiePersistor(context)));

            }

            if (cookieManager != null) {
                okhttpBuilder.cookieJar(cookieManager);
            }

            /**
             *okhttp3.Call.Factory callFactory = this.callFactory;
             */
            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }
            /**
             * create okHttpClient
             */
            okHttpClient = okhttpBuilder.build();
            /**
             * set Retrofit client
             */

            retrofitBuilder.client(okHttpClient);

            /**
             * create Retrofit
             */
            retrofit = retrofitBuilder.build();
            /**
             *create BaseApiService;
             */
            apiManager = retrofit.create(BaseApiService.class);

            return new RxRetrofit(callFactory, baseUrl, headers, parameters, apiManager, converterFactories, adapterFactories,
                    callbackExecutor, validateEagerly);
        }
    }





   /**
     * ResponseCallBack <T> Support your custom data model
    * 兼容1.3.3.2以下版本 更高以上版本已过时
     */
   @Deprecated
    public interface ResponseCallBack<T> {

        public void onStart();

        public void onCompleted();

        public abstract void onError(Throwable e);

        @Deprecated
        public abstract void onSuccee(RxRetrofitResponse<T> response);

        public void onsuccess(int code, String msg, T response, String originalResponse);

    }
}


