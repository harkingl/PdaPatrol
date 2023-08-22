package com.pda.patrol.server.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.entity.UserInfo;
import com.pda.patrol.server.util.UrlManager;
import com.pda.patrol.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseRequest<R> {
    private static final String TAG = "BaseRequest";

    private RequestListener<R> mListener;

    private static OkHttpClient mClient = new OkHttpClient();

    private Context mContext;

    protected BaseRequest(Context context) {
        mContext = context;
    }

    protected abstract String url();

    protected abstract String body() throws JSONException;

    protected RequestBody buildBody() throws Exception {
        String body = body();
        LogUtil.d(TAG, "Request params：" + body);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
    }

    protected RequestBody buildFormBody() throws Exception {
        String body = body();
        LogUtil.d(TAG, "Request params：" + body);
        FormBody.Builder builder = new FormBody.Builder();

        try {
            JSONObject obj = new JSONObject(body());
            if(obj.length() > 0) {
                JSONArray names = obj.names();
                for(int i = 0; i < names.length(); i++) {
                    String key = names.getString(i);
                    Object value = obj.get(key);
                    builder.add(key, (String)value);
                }

            }
        } catch (JSONException e) {

        }

        return builder.build();
    }

    protected Request buildPost() throws Exception {
        RequestBody body = null;
//        if(url().contains(UrlManager.USER_LOGIN)) {
//            body = buildFormBody();
//        } else {
            body = buildBody();
//        }

        return new Request.Builder()
                .url(UrlManager.formatUrl(url()))
                .headers(buildHeaders())
                .post(body)
                .build();
    }

    protected Request buildGet() throws Exception {
        String url = UrlManager.formatUrl(url());

        return new Request.Builder()
                .url(url + getParams())
                .headers(buildHeaders())
                .get()
                .build();
    }

    // 获取Get请求方式参数
    private String getParams() {
        try {
            JSONObject obj = new JSONObject(body());
            if(obj.length() > 0) {
                StringBuffer sb = new StringBuffer("?");
                JSONArray names = obj.names();
                for(int i = 0; i < names.length(); i++) {
                    String key = names.getString(i);
                    Object value = obj.get(key);
                    if(i != 0) {
                        sb.append("&");
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(value);
                }

                return sb.toString();
            }
        } catch (JSONException e) {

        }

        return "";
    }

    private Headers buildHeaders() {
        Headers.Builder b = new Headers.Builder();
        String token = UserInfo.getInstance().getToken();
        if(!TextUtils.isEmpty(UserInfo.getInstance().getToken())) {
            b.add("Authorization", token);
        }

        return b.build();
    }

    /**
     * 异步请求 默认post请求
     * @param isPost 是否post请求
     * @param listener
     */
    public void schedule(boolean isPost, RequestListener<R> listener) {
        this.mListener = listener;

        Observable.just(isPost)
                .map(new Function<Boolean, JSONObject>() {
                    @Override
                    public JSONObject apply(Boolean isPost) throws Exception {
                        Request request = isPost ? buildPost() : buildGet();
                        LogUtil.d(TAG, "Request：" + request.toString());
                        Response response = mClient.newCall(request).execute();
                        return new JSONObject(response.body().string());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull JSONObject jsonObject) {
                        try {
                            LogUtil.d(TAG, "Response data：" + url() + " " + jsonObject.toString());
                            CodeChecker.getInstance(mContext).checkCode(jsonObject);
                            if(mListener != null) {
                                mListener.onSuccess(result(jsonObject));
                            }
                        } catch (Exception e) {
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (listener != null) {
                            listener.onFailed(e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 同步请求
     * @return
     * @throws Exception
     */
    public R execute(boolean isPost) throws Exception {
        Request request = isPost ? buildPost() : buildGet();
        LogUtil.d(TAG, "Request：" + request.toString());
        try {
            Response response = mClient.newCall(request).execute();

            JSONObject obj = new JSONObject(response.body().string());
            CodeChecker.getInstance(mContext).checkCode(obj);

            return result(obj);
        } catch (Exception e) {
            throw e;
        }
    }

    protected abstract R result(JSONObject json) throws Exception;
}
