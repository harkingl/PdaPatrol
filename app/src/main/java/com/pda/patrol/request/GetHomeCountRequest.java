package com.pda.patrol.request;

import android.content.Context;

import com.pda.patrol.entity.HomeCountInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONException;
import org.json.JSONObject;

public class GetHomeCountRequest extends BaseRequest<HomeCountInfo> {
    public GetHomeCountRequest(Context context) {
        super(context);
    }

    @Override
    protected String url() {
        return UrlManager.HOME_COUNT;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();

        return obj.toString();
    }

    @Override
    protected HomeCountInfo result(JSONObject json) throws Exception {
        JSONObject data = json.optJSONObject("data");

        HomeCountInfo result = new HomeCountInfo();
        if(data != null) {
            return result.parse(data);
        }
        return result;
    }
}
