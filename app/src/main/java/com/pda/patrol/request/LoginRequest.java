package com.pda.patrol.request;

import android.content.Context;

import com.pda.patrol.entity.UserInfo;
import com.pda.patrol.server.okhttp.BaseRequest;
import com.pda.patrol.server.util.UrlManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRequest extends BaseRequest<UserInfo> {
    private String username;
    private String password;
    public LoginRequest(Context context, String username, String password) {
        super(context);
        this.username = username;
        this.password = password;
    }

    @Override
    protected String url() {
        return UrlManager.USER_LOGIN;
    }

    @Override
    protected String body()  throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("password", password);

        return obj.toString();
    }

    @Override
    protected UserInfo result(JSONObject json) throws Exception {
        JSONObject data = json.optJSONObject("data");
        UserInfo info = UserInfo.getInstance();
        if(data != null) {
            info.setPhone(data.optString("mobile"));
            info.setUserId(data.optString("uid"));
            info.setToken(data.optString("token"));
            info.setName(data.optString("nickname"));
            info.storeUserInfo();
        }
        return info;
    }
}
