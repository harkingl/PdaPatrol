package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.util.ToastUtil;

/***
 * 登陆页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mAccountEt;
    private EditText mPwdEt;
    private TextView mLoginTv;
    private ImageView mEyeIv;
    private boolean mShowEye = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity();
    }

    private void initActivity() {
        setContentView(R.layout.activity_login);

        mAccountEt = findViewById(R.id.login_phone_et);
        mPwdEt = findViewById(R.id.login_pwd_et);
        mEyeIv = findViewById(R.id.login_pwd_eye_iv);
        mLoginTv = findViewById(R.id.login_btn_tv);

        mEyeIv.setOnClickListener(this);
        mLoginTv.setOnClickListener(this);

//        String phone = UserInfo.getInstance().getPhone();
//        if(!TextUtils.isEmpty(phone)) {
//            mAccountEt.setText(phone);
//        }

        mAccountEt.setText("13444446666");
        mPwdEt.setText("111111");
    }


    private void setEyeVisible() {
        if (mShowEye) {
            mPwdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdEt.setSelection(mPwdEt.getText().length());
            mEyeIv.setImageResource(R.drawable.ic_eye_close);
        } else {
            mPwdEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdEt.setSelection(mPwdEt.getText().length());
            mEyeIv.setImageResource(R.drawable.ic_eye_close);
        }
        mShowEye = !mShowEye;
    }

    private void doLogin() {
        String phone = mAccountEt.getText().toString();
        String pwd = mPwdEt.getText().toString();
        if(TextUtils.isEmpty(phone)) {
            ToastUtil.toastLongMessage("请输入手机号");
            return;
        }
        if(phone.length() != 11) {
            ToastUtil.toastLongMessage("手机号格式不正确");
            return;
        }
        if(TextUtils.isEmpty(pwd)) {
            ToastUtil.toastLongMessage("请输入密码");
            return;
        }

//        new LoginRequest(this, "", phone, pwd, LoginRequest.TYPE_PWD).schedule(true, new RequestListener<UserInfo>() {
//            @Override
//            public void onSuccess(UserInfo result) {
//                ToastUtil.toastLongMessage("登录成功");
//                loginSuccess(result);
//            }
//
//            @Override
//            public void onFailed(Throwable e) {
//                Log.e(TAG, "Login failed：" + e.getMessage());
//                ToastUtil.toastLongMessage(e.getMessage());
//
//            }
//        });
        startActivity(new Intent(this, FindRfidActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == mEyeIv) {
            setEyeVisible();
        } else if(view == mLoginTv) {
            doLogin();
        }
    }
}
