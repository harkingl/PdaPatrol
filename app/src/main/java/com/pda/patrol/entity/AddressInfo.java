package com.pda.patrol.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class AddressInfo implements Serializable {
    public String id;
    public double lng;
    public double lat;
    public String address;

    public AddressInfo parse(JSONObject obj) {
        id = obj.optString("id");
        lng = obj.optDouble("lng");
        lat = obj.optDouble("lat");
        address = obj.optString("address");

        return this;
    }
}
