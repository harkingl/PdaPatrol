package com.pda.patrol.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class RfidItem implements Serializable {
    public String img;
//    public int img;
    public String id;
    public String type;
    public String address;

    public RfidItem parse(JSONObject obj) {
        img = obj.optString("url");
        id = obj.optString("no");
        type = obj.optString("typeName");

        return this;
    }
}
