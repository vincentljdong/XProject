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
import android.text.TextUtils;

import com.jaydon.rxretrofit.util.FileUtil;
import com.jaydon.rxretrofit.util.LogWraper;
import com.jaydon.rxretrofit.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by Tamic on 2016-07-11.
 */
public class RxRetrofitDownLoadManager {

    private DownLoadCallBack callBack;

    public static final String TAG = "RxRetrofit:DownLoadManager";

    private static String fileSuffix = ".tmpl";

    private static String defPath = "";

    private Handler handler;

    public static boolean isDownLoading = false;

    public static boolean isCancel = false;

    private String key;

    public RxRetrofitDownLoadManager(DownLoadCallBack callBack) {
        this.callBack = callBack;
        handler = new Handler(Looper.getMainLooper());
    }

    private static RxRetrofitDownLoadManager sInstance;

    /**
     * DownLoadManager getInstance
     */
    public static synchronized RxRetrofitDownLoadManager getInstance(DownLoadCallBack callBack) {
        if (sInstance == null) {
            sInstance = new RxRetrofitDownLoadManager(callBack);
        }
        return sInstance;
    }

    public boolean writeResponseBodyToDisk(final String key, String path, String name, Context context, ResponseBody body) {

        if (body == null) {
           LogWraper.e(TAG,  key + " : ResponseBody is null");
            finalonError(new NullPointerException("the "+ key + " ResponseBody is null"));
            return false;
        }
       LogWraper.v(TAG,  "Key:-->" + key);

        String type ="";
        if (body.contentType() != null) {
            type = body.contentType().toString();
        } else {
           LogWraper.d(TAG, "MediaType-->,无法获取");
        }

        if (!TextUtils.isEmpty(type)) {
           LogWraper.d(TAG, "contentType:>>>>" + body.contentType().toString());
            if (!TextUtils.isEmpty(MimeType.getInstance().getSuffix(type))){
                fileSuffix = MimeType.getInstance().getSuffix(type);
            }
        }

        if (!TextUtils.isEmpty(name)) {
            if (!name.contains(".")) {
                name = name + fileSuffix;
            }
        }
        // FIx bug:filepath error,    by username @NBInfo  with gitHub
        if (path == null) {
            File filepath = new File(path = context.getExternalFilesDir(null) + File.separator +"DownLoads");
            if (!filepath.exists()){
                filepath.mkdirs();
            }
            path = context.getExternalFilesDir(null) + File.separator +"DownLoads" + File.separator;
        }

        if (new File(path + name).exists()) {
            FileUtil.deleteFile(path);
        }
       LogWraper.d(TAG, "path:-->" + path);
       LogWraper.d(TAG, "name:->" + name);
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path + name);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                int updateCount = 0;
               LogWraper.d(TAG, "file length: " + fileSize);
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                   LogWraper.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    final int progress = (int) (fileSizeDownloaded * 100 / fileSize);
                   LogWraper.d(TAG, "file download progress : " + progress);
                    if (updateCount == 0 || progress >= updateCount) {
                        updateCount += 1;
                        if (callBack != null) {
                            handler = new Handler(Looper.getMainLooper());
                            final long finalFileSizeDownloaded = fileSizeDownloaded;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onProgress(key, progress, finalFileSizeDownloaded, fileSize);
                                }
                            });
                        }
                    }
                }
              /*  while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1 || isCancel) {
                        break;
                    }

                    isDownLoading = true;
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                   LogWraper.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    if (callBack != null) {
                        if (callBack != null) {
                            final long finalFileSizeDownloaded = fileSizeDownloaded;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onProgress(key, finalFileSizeDownloaded, fileSize);
                                }
                            }, 200);
                        }
                    }
                }*/

                outputStream.flush();
                LogWraper.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                isDownLoading = false;
                if (callBack != null) {
                    final String finalName = name;
                    final String finalPath = path;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSucess(key, finalPath, finalName, fileSize);
                        }
                    });
                   LogWraper.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                   LogWraper.d(TAG, "file downloaded: is sucess");
                }
                return true;
            } catch (IOException e) {
                finalonError(e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            finalonError(e);
            return false;
        }
    }

    private void finalonError(final Exception e) {
        if (callBack == null) {
            return;
        }

        if (Utils.checkMain()) {
            callBack.onError(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(e));
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callBack.onError(com.jaydon.rxretrofit.exception.RxRetrofitException.handleException(e));
                }
            });
        }
    }
}
