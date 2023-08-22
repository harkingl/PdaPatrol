package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class InstallInspectionRequest extends BaseRequest<String> {
    private String name;
    private String addressId;
    private String[] fileIds;
    private String[] rfidIds;
    private String remark;
    public InstallInspectionRequest(Context context, String name, String addressId, String[] fileIds, String[] rfidIds, String remark) {
        super(context);

        this.name = name;
        this.addressId = addressId;
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
        obj.put("addressId", addressId);
        if(fileIds != null) {
            JSONArray fileIdArray = new JSONArray();
            for(String fileId : fileIds) {
                fileIdArray.put(fileId);
            }
            obj.put("fileIds", fileIdArray);
        }
        JSONArray rfidIdArray = new JSONArray();
        for(String rfidId : rfidIds) {
            rfidIdArray.put(rfidId);
        }
        obj.put("rfidIds", rfidIdArray);
        if(!TextUtils.isEmpty(remark)) {
            obj.put("remark", remark);
        }

        return obj.toString();
    }

    @Override
    protected String result(JSONObject json) throws Exception {
        JSONObject data = json.optJSONObject("data");
        if(data != null) {
            return data.optString("id");
        }
        return "";
    }
}
