package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.hc.OpenTask;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;
import com.xlzn.hcpda.uhf.UHFReader;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;

import java.util.ArrayList;
import java.util.List;

public class FindRfidActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = FindRfidActivity.class.getSimpleName();

    private TitleBarLayout mTitlebarLayout;
    private TextView mDownCountTv;
    private TextView mTipTv;
    private NoScrollListView mRfidLv;
    private ArrayList<RfidItem> mList;

    private int mCount = 3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_rfid);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.find_rfid_title_bar);
        mDownCountTv = findViewById(R.id.find_rfid_down_count_tv);
        mTipTv = findViewById(R.id.find_rfid_tip_tv);
        mRfidLv = findViewById(R.id.find_rfid_lv);
    }

    private void initData() {
        mDownCountTv.setText(mCount + "");
        mHandler.sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);

        mList = new ArrayList<>();
        RfidItem item = new RfidItem();
        item.img = R.drawable.ic_rfid_img1;
        item.id = "YYbox-00001";
        item.type = "智能BOX";
        mList.add(item);

        RfidItem item1 = new RfidItem();
        item1.img = R.drawable.ic_rfid_img2;
        item1.id = "YYsign-00001";
        item1.type = "标识牌Sign";
        mList.add(item1);

        mRfidLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemSelect(i);
            }
        });
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("查找RFID", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    private void onItemSelect(int position) {
        Intent i = new Intent(this, InstallActivity.class);
        i.putExtra("select_item", mList.get(position));
        i.putExtra("frid_list", mList);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new OpenTask(this).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    private void close() {
        UHFReader.getInstance().disConnect();
    }

//    private boolean initdata(){
//        UHFConfigure.DEBUG = true;
//
//        manager =  UHFManager.getIntance(this);
//        if(manager == null)
//            return false;
//
//        manager.setResultCallback(this);
//        manager.initUHFDevice();
//
//        return true;
//    }
//
//    private boolean uninitdata(){
//        if(manager == null)
//            return true;
//        manager.unInitUHFDevice();
//        manager = null;
//        return true;
//    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "onKeyDown:  " + keyCode);
        if (event.getRepeatCount() == 0 && keyCode == 293 || keyCode == 290 || keyCode == 287|| keyCode == 286) {
            read();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void read() {
//        UHFReaderResult<String> readerResult = UHFReader.getInstance().read("00000000", 1, 2, 6, null);;
//
//        if (readerResult.getResultCode() != UHFReaderResult.ResultCode.CODE_SUCCESS) {
//            ToastUtil.toastLongMessage("读取失败");
//            return;
//        }
//        String data = readerResult.getData();
//        ToastUtil.toastLongMessage("Result：" + data);

        mRfidLv.setAdapter(new RfidListAdapter(this, mList));
    }

    private static final int WHAT_DOWN_COUNT = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WHAT_DOWN_COUNT:
                    mCount--;
                    mDownCountTv.setText(mCount + "");
                    if(mCount <= 0) {
                        scanFinish();
                    } else {
                        if(mCount == 1) {
                            read();
                        }
                        sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
                    }
                   break;
            }
        }
    };

    private void scanFinish() {
        mTipTv.setText("RFID扫描结束");
        close();
    }
}
