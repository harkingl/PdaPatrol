package com.pda.patrol.request;

import android.content.Context;
import android.text.TextUtils;

import com.pda.patrol.entity.AddressInfo;
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.PagedListEntity;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetInspectListRequest extends BaseRequest<PagedListEntity<InspectionDetail>> {
    private int p;
    private int ps;
    private String address;
    private boolean ad;
    public GetInspectListRequest(Context context, int p, int ps, String address, boolean ad) {
        super(context);

        this.p = p;
        this.ps = ps;
        this.address = address;
        this.ad = ad;
    }

    @Override
    protected String url() {
        return UrlManager.GET_INSPECT_LIST;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("p", p);
        obj.put("ps", ps);
        if(!TextUtils.isEmpty(address)) {
            obj.put("address", address);
        }
        obj.put("ad", ad);

        return obj.toString();
    }

    @Override
    protected PagedListEntity<InspectionDetail> result(JSONObject json) throws Exception {
        JSONArray data = json.optJSONArray("data");
        PagedListEntity<InspectionDetail> entity = new PagedListEntity<>();
        List<InspectionDetail> list = new ArrayList<>();
        if(data != null) {
            for(int i = 0; i < data.length(); i++) {
                list.add(new InspectionDetail().parse(data.getJSONObject(i)));
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
