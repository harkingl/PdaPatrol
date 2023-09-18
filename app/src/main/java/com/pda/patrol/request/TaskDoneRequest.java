package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class TaskDoneRequest extends BaseRequest<Boolean> {
    private String id;
    private String[] fileIds;
    private int isNormal;
    private String abnormalType;
    private String abnormalResult;
    private String abnormalInfo;
    private String nickname;

    private String remark;
    public TaskDoneRequest(Context context, String id, String[] fileIds, int isNormal, String abnormalType, String abnormalResult, String abnormalInfo, String nickname) {
        super(context);

        this.id = id;
        this.fileIds = fileIds;
        this.isNormal = isNormal;
        this.abnormalType = abnormalType;
        this.abnormalResult = abnormalResult;
        this.abnormalInfo = abnormalInfo;
        this.nickname = nickname;
    }

    @Override
    protected String url() {
        return UrlManager.TASK_DONE + id;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        if(fileIds != null && fileIds.length > 0) {
            JSONArray array = new JSONArray();
            for(String file : fileIds) {
                array.put(file);
            }
            obj.put("fileIds", array);
        }
        obj.put("isNormal", isNormal);
        if(!TextUtils.isEmpty(abnormalType)) {
            obj.put("abnormalType", abnormalType);
        }
        if(!TextUtils.isEmpty(abnormalResult)) {
            obj.put("abnormalResult", abnormalResult);
        }
        if(!TextUtils.isEmpty(abnormalInfo)) {
            obj.put("abnormalInfo", abnormalInfo);
        }
        if(!TextUtils.isEmpty(nickname)) {
            obj.put("nickname", nickname);
        }

        return obj.toString();
    }

    @Override
    protected Boolean result(JSONObject json) throws Exception {

        return true;
    }
}
