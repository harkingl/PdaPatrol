package com.pda.patrol.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class TaskStateItem implements Serializable {
    public TaskStateItem(int taskState, String name) {
        this.taskState = taskState;
        this.name = name;
    }
    public int taskState;
    public String name;
}
