package com.jaydon.rxretrofit.download;


public interface UpLoadCallback {
    void onProgress(Object tag, int progress, long speed, boolean done);
}
