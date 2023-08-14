package com.pda.patrol.server.okhttp;

import android.content.Context;
import android.content.Intent;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.Constants;

import org.json.JSONObject;

/***
 * Code check
 */
public class CodeChecker {
    private static CodeChecker mInstance;
    private Context mContext;
    private CodeChecker(Context context){
        this.mContext=context;
    }

    public static CodeChecker getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new CodeChecker(context);
        }

        return mInstance;
    }

    public void checkCode(JSONObject object) throws Exception {
        int code = object.optInt("code", 200);
        String errorInfo = "";
        String message = object.optString("msg", "请求失败:" + code);
        switch (code) {
            case 200:
            case 0:
                break;
//            case 1003://TODO
//                errorInfo = mContext.getString(R.string.repeat_login_tip);
//                sendBroadcast(TIMCommonConstants.LOGIN_TIMEOUT_BROADCAST, code, errorInfo);
//                throw new RequestException(errorInfo, code);
            case 401:
                errorInfo = mContext.getString(R.string.expired_login_tip);
                sendBroadcast(Constants.LOGIN_TIMEOUT_BROADCAST, code, errorInfo);
                throw new RequestException(errorInfo, code);
            default:
                throw new RequestException(message, code);
        }

    }

    private void sendBroadcast(String action, int code, String msg) {
        final Intent i = new Intent(action).putExtra("code", code).putExtra("message", msg);
        mContext.sendBroadcast(i);
    }
}
