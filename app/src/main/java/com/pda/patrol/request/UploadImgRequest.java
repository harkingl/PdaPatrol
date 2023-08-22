package com.pda.patrol.request;

import android.content.Context;

import com.pda.patrol.server.okhttp.BaseFileRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UploadImgRequest extends BaseFileRequest<String> {
    private File file;
    public UploadImgRequest(Context context, File file) {
        super(context);

        this.file = file;
    }

    @Override
    protected String url() {
        return UrlManager.UPLOAD_FILE;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();

        return obj.toString();
    }

    @Override
    protected String result(JSONObject json) throws Exception {

        return json.optString("data");
    }

    @Override
    protected File fileBody() throws JSONException {
        return file;
    }

    @Override
    protected String uploadDataKey() {
        return null;
    }

    @Override
    protected String uploadData() throws JSONException {
        return null;
    }
}
