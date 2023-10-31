package com.pda.patrol.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class RfidItem implements Serializable {
    public String img;
//    public int img;
    public String id;
    public String no;
    public String type;
    public String address;
    public String epc;
    // 是否已扫描
    public boolean isScan;
    public String blueToothMac;

    public RfidItem parse(JSONObject obj) {
        img = obj.optString("url");
        id = obj.optString("id");
        no = obj.optString("no");
        type = obj.optString("typeName");
        epc = obj.optString("epc");
        blueToothMac = obj.optString("blueToothMac");

        return this;
    }
}
