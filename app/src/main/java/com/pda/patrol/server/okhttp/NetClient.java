package com.pda.patrol.server.okhttp;

import okhttp3.OkHttpClient;

public class NetClient {
    private static OkHttpClient mClient;
    private NetClient() {
        mClient = new OkHttpClient();
    }

    public static OkHttpClient getInstantce() {
        if(mClient == null) {
            mClient = new OkHttpClient();
        }

        return mClient;
    }
}
