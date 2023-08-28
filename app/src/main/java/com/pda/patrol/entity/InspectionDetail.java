package com.pda.patrol.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * 巡检点详情
 */
public class InspectionDetail implements Serializable {
    public String id;
    // "2023-08-10T11:57:52+08:00"
    public String crt;
    // "2023-08-11T11:03:27+08:00"
    public String lut;
    public String name;
    public String remark;
    public String addressId;
    public String[] fileIds;
    public String[] rfidIds;
    public String address;
    public double lng;
    public double lat;
    public String nickname;
    public List<String> fileList;
    public List<RfidItem> rfidList;
    public int tasksCount;


    public InspectionDetail parse(JSONObject obj) throws JSONException {
        id = obj.optString("id");
        crt = obj.optString("crt");
        lut = obj.optString("lut");
        name = obj.optString("name");
        remark = obj.optString("remark");
        addressId = obj.optString("addressId");
        address = obj.optString("address");
        lng = obj.optDouble("lng");
        lat = obj.optDouble("lat");
        nickname = obj.optString("nickname");
        tasksCount = obj.optInt("tasksCount");
        fileList = new ArrayList();
        JSONArray fileArray = obj.optJSONArray("filesInfo");
        if(fileArray != null && fileArray.length() > 0) {
            for(int i = 0; i < fileArray.length(); i++) {
                fileList.add((String) fileArray.get(i));
            }
        }
        JSONArray rfidArray = obj.optJSONArray("rfidsInfo");
        rfidList = new ArrayList<>();
        if(rfidArray != null && rfidArray.length() > 0) {
            for(int i = 0; i < rfidArray.length(); i++) {
                rfidList.add(new RfidItem().parse(rfidArray.getJSONObject(i)));
            }
        }

        return this;
    }
}
