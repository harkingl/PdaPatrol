package com.pda.patrol.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class HomeCountInfo implements Serializable {
    public int inspectionCount;
    public int taskCount;

    public HomeCountInfo parse(JSONObject obj) {
        inspectionCount = obj.optInt("inspectionCount");
        taskCount = obj.optInt("taskCount");

        return this;
    }
}
