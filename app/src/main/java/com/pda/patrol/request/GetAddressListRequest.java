package com.pda.patrol.request;

import android.content.Context;

import com.pda.patrol.entity.AddressInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetAddressListRequest extends BaseRequest<List<AddressInfo>> {
    public GetAddressListRequest(Context context) {
        super(context);
    }

    @Override
    protected String url() {
        return UrlManager.GET_ADDRESS_LIST;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();

        return obj.toString();
    }

    @Override
    protected List<AddressInfo> result(JSONObject json) throws Exception {
        JSONArray data = json.optJSONArray("data");
        List<AddressInfo> list = new ArrayList<>();
        if(data != null) {
            for(int i = 0; i < data.length(); i++) {
                list.add(new AddressInfo().parse(data.getJSONObject(i)));
            }
        }
        return list;
    }
}
