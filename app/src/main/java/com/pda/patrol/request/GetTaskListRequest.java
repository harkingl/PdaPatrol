package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetTaskListRequest extends BaseRequest<PagedListEntity<TaskInfo>> {
    private int taskState;
    private String inspectionId;
    private boolean ad;
    private int p;
    private int ps;
    public GetTaskListRequest(Context context, int taskState, String inspectionId, boolean ad, int p, int ps) {
        super(context);

        this.taskState = taskState;
        this.inspectionId = inspectionId;
        this.ad = ad;
        this.p = p;
        this.ps = ps;
    }

    @Override
    protected String url() {
        return UrlManager.GET_TASK_LIST;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("taskState", taskState);
        if(!TextUtils.isEmpty(inspectionId)) {
            obj.put("inspectionId", inspectionId);
        }
        // 返回所有
        obj.put("ad", ad);
        obj.put("p", p);
        obj.put("ps", ps);

        return obj.toString();
    }

    @Override
    protected PagedListEntity<TaskInfo> result(JSONObject json) throws Exception {
        JSONArray data = json.optJSONArray("data");
        PagedListEntity<TaskInfo> entity = new PagedListEntity<>();
        List<TaskInfo> list = new ArrayList<>();
        if(data != null) {
            for(int i = 0; i < data.length(); i++) {
                list.add(new TaskInfo().parse(data.getJSONObject(i)));
            }
        }
        entity.setList(list);
        JSONObject pageObj = json.optJSONObject("page");
        if(pageObj != null) {
            entity.setPageNo(pageObj.optInt("p"));
            entity.setPageSize(pageObj.optInt("ps"));
            entity.setPageSize(pageObj.optInt("cnt"));
        }
        return entity;
    }
}
