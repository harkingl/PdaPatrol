package com.pda.patrol.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * 任务info
 */
public class TaskInfo implements Serializable {
    public String id;
    // "2023-08-10T11:57:52+08:00"
    public String crt;
    // "2023-08-11T11:03:27+08:00"
    public String lut;
    public String name;
    // 任务状态(-1:全部,0:待执行,1:已完成,2:已逾期)
    public int state;
    public String inspectionId;
    public String endTime;
    public String[] fileIds;
    public String[] rfidIds;
    public int isNormal;
    public String abnormalType;
    public String abnormalResult;
    public String abnormalInfo;
    public String stateName;
    public int taskState;
    public String taskStateName;
    public String inspectionName;
    public String address;
    public double lng;
    public double lat;
    public String nickname;
    List<String> fileList;
    public List<RfidItem> rfidList;


    public TaskInfo parse(JSONObject obj) throws JSONException {
        id = obj.optString("id");
        crt = obj.optString("crt");
        lut = obj.optString("lut");
        endTime = obj.optString("endTime");
        name = obj.optString("name");
        address = obj.optString("address");
        lng = obj.optDouble("lng");
        lat = obj.optDouble("lat");
        nickname = obj.optString("nickname");
        fileList = new ArrayList();
        JSONArray fileArray = obj.optJSONArray("filesInfo");
        if(fileArray != null && fileArray.length() > 0) {
            for(int i = 0; i < fileArray.length(); i++) {
                fileList.add(fileArray.getString(i));
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
