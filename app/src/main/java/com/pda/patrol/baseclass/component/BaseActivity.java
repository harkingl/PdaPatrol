package com.pda.patrol.baseclass.component;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.LoginTimeoutBroadcast;


public class BaseActivity extends AppCompatActivity {
    private LoginTimeoutBroadcast mLoginTimeoutBroadcast;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            int vis = getWindow().getDecorView().getSystemUiVisibility();
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(vis);
        }
    }

    @Override
    public void finish() {
        hideSoftInput();
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mLoginTimeoutBroadcast = new LoginTimeoutBroadcast(this);
        mLoginTimeoutBroadcast.registerReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mLoginTimeoutBroadcast != null) {
            mLoginTimeoutBroadcast.unregisterReceiver();
        }
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Window window = getWindow();
        if (window != null) {
            imm.hideSoftInputFromWindow(window.getDecorView().getWindowToken(), 0);
        }
    }
}
