package com.pda.patrol.request;

import android.content.Context;

import com.pda.patrol.entity.TypeInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetTypeListRequest extends BaseRequest<List<TypeInfo>> {
    public GetTypeListRequest(Context context) {
        super(context);
    }

    @Override
    protected String url() {
        return UrlManager.GET_ERROR_TYPE_LIST;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        array.put("PATROL_ERROR_TYPE");
        obj.put("categoryCodes", array);
        return obj.toString();
    }

    @Override
    protected List<TypeInfo> result(JSONObject json) throws Exception {
        JSONObject data = json.optJSONObject("data");
        JSONArray array = data.optJSONArray("PATROL_ERROR_TYPE");
        List<TypeInfo> list = new ArrayList<>();
        if(array != null) {
            for(int i = 0; i < array.length(); i++) {
                list.add(new TypeInfo().parse(array.getJSONObject(i)));
            }
        }
        return list;
    }
}
