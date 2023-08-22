package com.pda.patrol.server.okhttp;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;

public abstract class BaseFileRequest<R> extends BaseRequest<R> {

    protected BaseFileRequest(Context context) {
        super(context);
    }

    /**
     *
     * @return 上传的文件
     * @throws JSONException
     */
    protected abstract File fileBody() throws JSONException;

    /**
     *
     * @return 上传时，json数据对应的key
     * @throws JSONException
     */
    protected abstract String uploadDataKey();

    /**
     *
     * @return 上传时，json数据
     * @throws JSONException
     */
    protected abstract String uploadData() throws JSONException;

    protected String uploadFileKey() {
        return "file";
    }

    @Override
    protected RequestBody buildBody() throws Exception {
        String fileName = toAscii(fileBody().getName());
//        String fileName = fileBody().getName();
        String fileDisposition = "form-data; name=\"" + uploadFileKey() + "\"; filename=\"" + fileName + "\"";
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), fileBody());

        MultipartBody.Builder b = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", fileDisposition), fileBody);
        if (!TextUtils.isEmpty(uploadDataKey()) && !TextUtils.isEmpty(uploadData())) {
            String StringDisposition = "form-data; name=\"" + uploadDataKey() + "\"";
            RequestBody StringBody = RequestBody.create(null, uploadData());
            b.addPart(Headers.of("Content-Disposition", StringDisposition), StringBody);
        }
        return b.build();
    }


    public static String toAscii(String s) {
        for (int i = 0, length = s.length(), c; i < length; i += Character.charCount(c)) {
            c = s.codePointAt(i);
            if (!((c <= '\u001f' && c != '\t') || c >= '\u007f')) {
                continue;
            }

            Buffer buffer = new Buffer();
            buffer.writeUtf8(s, 0, i);
            for (int j = i; j < length; j += Character.charCount(c)) {
                c = s.codePointAt(j);
                buffer.writeUtf8CodePoint(c > '\u001f' && c < '\u007f' ? c : '?');
            }
            return buffer.readUtf8();
        }
        return s;
    }
}
