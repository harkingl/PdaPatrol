package com.pda.patrol.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class TypeInfo implements Serializable {
    public String id;
    public String categoryCode;
    public String code;
    public String value;

    public TypeInfo parse(JSONObject obj) {
        id = obj.optString("id");
        categoryCode = obj.optString("categoryCode");
        value = obj.optString("value");

        return this;
    }
}
