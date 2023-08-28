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
    public String inspectionId;
    public String dealTime;
    public String endTime;
    public String[] fileIds;
    public String[] rfidIds;
    // 巡检状态(1:正常, 2:异常)
    public int isNormal;
    public String abnormalType;
    public String abnormalResult;
    public String abnormalInfo;
    public String stateName;
    // 任务状态(-1:全部,0:待执行,1:已完成,2:已逾期)
    public int taskState;
    public String taskStateName;
    public String inspectionName;
    public String address;
    public double lng;
    public double lat;
    public String nickname;
    public List<String> fileList;
    public List<RfidItem> rfidList;
    public String rfidNo;
    public String rfidTypeName;
    public String rfidUrl;


    public TaskInfo parse(JSONObject obj) throws JSONException {
        id = obj.optString("id");
        crt = obj.optString("crt");
        lut = obj.optString("lut");
        inspectionId = obj.optString("inspectionId");
        dealTime = obj.optString("dealTime");
        endTime = obj.optString("endTime");
        isNormal = obj.optInt("isNormal");
        abnormalType = obj.optString("abnormalType");
        abnormalResult = obj.optString("abnormalResult");
        abnormalInfo = obj.optString("abnormalInfo");
        stateName = obj.optString("stateName");
        taskState = obj.optInt("taskState");
        taskStateName = obj.optString("taskStateName");
        name = obj.optString("name");
        address = obj.optString("address");
        lng = obj.optDouble("lng");
        lat = obj.optDouble("lat");
        nickname = obj.optString("nickname");
        rfidNo = obj.optString("rfidNo");
        rfidTypeName = obj.optString("rfidTypeName");
        rfidUrl = obj.optString("rfidUrl");
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
