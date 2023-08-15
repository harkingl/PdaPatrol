package com.pda.patrol.request;

import android.content.Context;

import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONException;
import org.json.JSONObject;

public class GetInspectionDetailRequest extends BaseRequest<InspectionDetail> {
    private String id;
    public GetInspectionDetailRequest(Context context, String id) {
        super(context);
        this.id = id;
    }

    @Override
    protected String url() {
        return UrlManager.INSTALL_INSPECTION + "/" + id;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
//        obj.put("id", id);

        return obj.toString();
    }

    @Override
    protected InspectionDetail result(JSONObject json) throws Exception {
        JSONObject data = json.optJSONObject("data");

        InspectionDetail result = new InspectionDetail();
        if(data != null) {
            return result.parse(data);
        }
        return result;
    }
}
