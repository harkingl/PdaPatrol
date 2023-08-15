package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetTaskListRequest extends BaseRequest<List<TaskInfo>> {
    private int taskState;
    private String inspectionId;
    public GetTaskListRequest(Context context, int taskState, String inspectionId) {
        super(context);

        this.taskState = taskState;
        this.inspectionId = inspectionId;
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
        obj.put("ad", true);

        return obj.toString();
    }

    @Override
    protected List<TaskInfo> result(JSONObject json) throws Exception {
        JSONArray data = json.optJSONArray("data");
        List<TaskInfo> list = new ArrayList<>();
        if(data != null) {
            for(int i = 0; i < data.length(); i++) {
                list.add(new TaskInfo().parse(data.getJSONObject(i)));
            }
        }
        return list;
    }
}
