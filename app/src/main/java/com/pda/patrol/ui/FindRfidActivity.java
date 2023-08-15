package com.pda.patrol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.pda.patrol.request.GetRfidListRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;
import com.xlzn.hcpda.uhf.UHFReader;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.entity.UHFTagEntity;
import com.xlzn.hcpda.uhf.interfaces.OnInventoryDataListener;

import java.util.ArrayList;
import java.util.List;

public class FindRfidActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = FindRfidActivity.class.getSimpleName();
    private static final int DEFAULT_DOWN_COUNT = 10;

    private TitleBarLayout mTitlebarLayout;
    private TextView mDownCountTv;
    private TextView mTipTv;
    private NoScrollListView mRfidLv;
    private View mDataLayout;
    private View mEmptyLayout;
    private TextView mRescanTv;
    private RfidListAdapter mAdapter;
    private ArrayList<RfidItem> mList;
    private List<String> epcs;

    private int mCount = DEFAULT_DOWN_COUNT;
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
        mDataLayout = findViewById(R.id.find_rfid_data_ll);
        mEmptyLayout = findViewById(R.id.find_rfid_empty_ll);
        mRescanTv = findViewById(R.id.find_rfid_rescan_tv);

        mRescanTv.setOnClickListener(this);
    }

    private void initData() {
        mDownCountTv.setText(mCount + "");
        mHandler.sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);

        epcs = new ArrayList<>();
        mList = new ArrayList<>();
        mAdapter = new RfidListAdapter(this, mList);
        mRfidLv.setAdapter(mAdapter);
//        RfidItem item = new RfidItem();
//        item.img = R.drawable.ic_rfid_img1;
//        item.id = "YYbox-00001";
//        item.type = "智能BOX";
//        mList.add(item);
//
//        RfidItem item1 = new RfidItem();
//        item1.img = R.drawable.ic_rfid_img2;
//        item1.id = "YYsign-00001";
//        item1.type = "标识牌Sign";
//        mList.add(item1);

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
        open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    private void open() {
        new OpenTask(this).execute();
    }

    private void close() {
        UHFReader.getInstance().disConnect();
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mRescanTv) {
            reScan();
        }
    }

    private void reScan() {
        mCount = DEFAULT_DOWN_COUNT;
        open();
        mDownCountTv.setText(mCount + "");
        mTipTv.setText("正在扫描附近RFID...");
        mDataLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        mHandler.sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
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
        UHFReaderResult<String> readerResult = UHFReader.getInstance().read("00000000", 1, 2, 6, null);;

        if (readerResult.getResultCode() != UHFReaderResult.ResultCode.CODE_SUCCESS) {
//            ToastUtil.toastLongMessage("读取失败");
            return;
        }
        String data = readerResult.getData();
        LogUtil.d(TAG, "Result data：" + data);
        if(!TextUtils.isEmpty(data) && !epcs.contains(data)) {
            // 通过handler去请求
            Message msg = mHandler.obtainMessage(WHAT_REQUEST_DATA);
            msg.obj = data;
           mHandler.sendMessage(msg);
           mDataLayout.setVisibility(View.VISIBLE);
           mEmptyLayout.setVisibility(View.GONE);
        }

//        RfidItem item = new RfidItem();
//        item.img = R.drawable.ic_rfid_img1;
//        item.id = data;
//        item.type = "智能BOX";
//        mList.add(item);

//        mAdapter.notifyDataSetChanged();
    }

    private static final int WHAT_DOWN_COUNT = 1;
    private static final int WHAT_REQUEST_DATA = 2;
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
                        sendEmptyMessageDelayed(WHAT_DOWN_COUNT, 1000);
                    }
                   break;
                case WHAT_REQUEST_DATA:
                    getData((String)msg.obj);
                    break;
            }
        }
    };

    private void getData(String epc) {
        if(TextUtils.isEmpty(epc)) {
            return;
        }
        new GetRfidListRequest(this, true, new String[]{epc}, true).schedule(false, new RequestListener<List<RfidItem>>() {
            @Override
            public void onSuccess(List<RfidItem> result) {
                if(result != null) {
//                    mList.addAll(result);
                    mList.add(result.get(0));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    private void scanFinish() {
        if(mList == null || mList.size() == 0) {
            mTipTv.setText("未发现附近有RFID");
            mEmptyLayout.setVisibility(View.VISIBLE);
            mDataLayout.setVisibility(View.GONE);
        } else {
            mTipTv.setText("RFID扫描结束");
            mEmptyLayout.setVisibility(View.GONE);
            mDataLayout.setVisibility(View.VISIBLE);
        }
        close();
    }

    private void inventory(boolean start) {
        if (start) {

            UHFReader.getInstance().setOnInventoryDataListener(new OnInventoryDataListener() {
                @Override
                public void onInventoryData(List<UHFTagEntity> tagEntityList) {
//                    Log.e("TAG", "onInventoryData:一次回调--------  " + tagEntityList.size());
                    if (tagEntityList != null && tagEntityList.size() > 0) {
                        for (int k = 0; k < tagEntityList.size(); k++) {
                            if (!TextUtils.isEmpty(tagEntityList.get(k).getEcpHex())) {
//                                Message message = new Message();
//                                message.what = 1;
//                                message.obj = tagEntityList.get(k);
//                                handler.sendMessage(message);
//                                Utils.play();
                                LogUtil.d(TAG, "onInventoryData：" + tagEntityList.get(k).getEcpHex());
                            }
                        }
                    }
                }
            });

            UHFReaderResult<Boolean> readerResult = UHFReader.getInstance().startInventory();
            if (readerResult.getData()) {

            } else {
                ToastUtil.toastLongMessage("盘点失败");
            }
        } else {
            UHFReaderResult<Boolean> booleanUHFReaderResult = UHFReader.getInstance().stopInventory();

//            handler.removeMessages(2);
        }
    }
}
