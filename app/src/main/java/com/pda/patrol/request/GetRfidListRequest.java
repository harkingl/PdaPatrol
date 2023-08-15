package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import androidx.core.content.res.TypedArrayUtils;

import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.entity.UserInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetRfidListRequest extends BaseRequest<List<RfidItem>> {
    private boolean scan;
    private String[] epcs;
    private boolean ad;
    public GetRfidListRequest(Context context, boolean scan, String[] epcs, boolean ad) {
        super(context);
        this.scan = scan;
        this.epcs = epcs;
        this.ad = ad;
    }

    @Override
    protected String url() {
        return UrlManager.GET_RFID_INFO;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("scan", scan);
        if(epcs != null) {
//            obj.put("epcs[]", TextUtils.join(",", epcs));
//            obj.put("epcs[]", Arrays.toString(epcs));
            obj.put("epc[]", epcs[0]);
        }
        obj.put("ad", ad);

        return obj.toString();
    }

    @Override
    protected List<RfidItem> result(JSONObject json) throws Exception {
        JSONArray data = json.optJSONArray("data");
        List<RfidItem> list = new ArrayList<>();
        if(data != null) {
            for(int i = 0; i < data.length(); i++) {
                list.add(new RfidItem().parse(data.getJSONObject(i)));
            }
        }
        return list;
    }
}
