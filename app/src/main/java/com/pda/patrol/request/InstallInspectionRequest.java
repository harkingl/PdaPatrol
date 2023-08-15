package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class InstallInspectionRequest extends BaseRequest<Boolean> {
    private String name;
    private String id;
    private String[] fileIds;
    private String[] rfidIds;
    private String remark;
    public InstallInspectionRequest(Context context, String name, String id, String[] fileIds, String[] rfidIds, String remark) {
        super(context);

        this.name = name;
        this.id = id;
        this.fileIds = fileIds;
        this.rfidIds = rfidIds;
        this.remark = remark;
    }

    @Override
    protected String url() {
        return UrlManager.INSTALL_INSPECTION;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("id", id);
        if(fileIds != null) {
            obj.put("fileIds", Arrays.toString(fileIds));
        }
        obj.put("rfidIds", Arrays.toString(rfidIds));
        if(!TextUtils.isEmpty(remark)) {
            obj.put("remark", remark);
        }

        return obj.toString();
    }

    @Override
    protected Boolean result(JSONObject json) throws Exception {

        return true;
    }
}
