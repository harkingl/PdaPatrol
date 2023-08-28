package com.pda.patrol.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.entity.UserInfo;
import com.pda.patrol.request.LoginRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.LogUtil;
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

        String phone = UserInfo.getInstance().getPhone();
        if (!TextUtils.isEmpty(phone)) {
            mAccountEt.setText(phone);
        }

        mAccountEt.setText("sw");
        mPwdEt.setText("yytt2023");

//        getTaskList();
//        getLocation();
    }

    private void getLocation() {
        LocationManager lv = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    111);

            return;
        }
        Location location = lv.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Criteria mCriteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE 比较粗略， Criteria.ACCURACY_FINE 则比较精细
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否需要海拔信息 Altitude
        mCriteria.setAltitudeRequired(true);
        // 设置是否需要方位信息 Bearing
        mCriteria.setBearingRequired(true);
        // 设置是否允许运营商收费
        mCriteria.setCostAllowed(true);
        // 设置对电源的需求
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = lv.getBestProvider(mCriteria, true);
        if (provider == null) {
            provider = LocationManager.NETWORK_PROVIDER;
        }
        lv.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener01);
        lv.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener01);


//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        System.out.println("#############" + lat + " " + lng);
    }

    LocationListener mLocationListener01 = new LocationListener() {
        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("#######onProviderDisabled#######" + provider);
        }

        public void onProviderEnabled(String provider) {
            System.out.println("#######onProviderEnabled#######" + provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            System.out.println("#########111####" + lat + " " + lng);
            ToastUtil.toastLongMessage("#####onLocationChanged###" + location.toString());

        }

        @Override

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 111) {
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getLocation();
        }
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
            ToastUtil.toastLongMessage("请输入用户名");
            return;
        }
        if(TextUtils.isEmpty(pwd)) {
            ToastUtil.toastLongMessage("请输入密码");
            return;
        }

        new LoginRequest(this, phone, pwd).schedule(true, new RequestListener<UserInfo>() {
            @Override
            public void onSuccess(UserInfo result) {
                ToastUtil.toastLongMessage("登录成功");

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
//                startActivity(new Intent(LoginActivity.this, TaskListActivity.class));
            }

            @Override
            public void onFailed(Throwable e) {
                LogUtil.e(TAG, "Login failed：" + e.getMessage());
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == mEyeIv) {
            setEyeVisible();
        } else if(view == mLoginTv) {
            doLogin();
//            startActivity(new Intent(this, PatrolDetailActivity.class));
        }
    }
}
