package com.pda.patrol.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollGridView;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.InspectionDetail;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.entity.TypeInfo;
import com.pda.patrol.entity.UserInfo;
import com.pda.patrol.request.GetTypeListRequest;
import com.pda.patrol.request.TaskDoneRequest;
import com.pda.patrol.request.UploadImgRequest;
import com.pda.patrol.server.okhttp.RequestListener;
import com.pda.patrol.util.FileUtil;
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * 执行巡检
 */
public class ExecuteInspectActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ExecuteInspectActivity";
    private static final int REQ_TAKE_PHOTO = 111;
    private static final int MAX_COUNT = 3;
    private TitleBarLayout mTitlebarLayout;
    private TextView mIdTv;
    private ImageView mRfidImg;
    private TextView mRfidIdTv;
    private TextView mRfidTypeTv;
    private NoScrollGridView mImgsGv;
    private CheckBox mNormalCb;
    private CheckBox mAbnormalCb;
    private LinearLayout mAbnormalLayout;
    private LinearLayout mSelectTypeLayout;
    private TextView mAbnormalTypeTv;
    private CheckBox mSolvedCb;
    private CheckBox mUnresolvedCb;
    private EditText mAbnormalInfoEt;
    private TextView mCommitTv;
    private InspectionDetail mDetail;
    private ImageSelectAdapter mImgAdapter;
    private ArrayList<String> mImgList;
    private List<TypeInfo> mTypeList = new ArrayList<>();
    private TypeInfo mSelectType;
    private List<String> mFileIds = new ArrayList<>();
    private String mTaskId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_execute_inspect);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.execute_title_bar);
        mIdTv = findViewById(R.id.execute_id_tv);
        mRfidImg = findViewById(R.id.rfid_item_img_iv);
        mRfidIdTv = findViewById(R.id.rfid_item_id_tv);
        mRfidTypeTv = findViewById(R.id.rfid_item_type_tv);
        mImgsGv = findViewById(R.id.execute_imgs_gv);
        mNormalCb = findViewById(R.id.execute_result_normal_cb);
        mAbnormalCb = findViewById(R.id.execute_result_abnormal_cb);
        mAbnormalLayout = findViewById(R.id.execute_result_abnormal_ll);
        mAbnormalTypeTv = findViewById(R.id.execute_abnormal_type_tv);
        mSelectTypeLayout = findViewById(R.id.execute_select_type_ll);
        mSolvedCb = findViewById(R.id.execute_deal_solved_cb);
        mUnresolvedCb = findViewById(R.id.execute_deal_unresolved_cb);
        mAbnormalInfoEt = findViewById(R.id.execute_abnormal_info_et);
        mCommitTv = findViewById(R.id.execute_commit_tv);

        mNormalCb.setOnCheckedChangeListener(mCheckedListener);
        mAbnormalCb.setOnCheckedChangeListener(mCheckedListener);
        mSolvedCb.setOnCheckedChangeListener(mCheckedListener);
        mUnresolvedCb.setOnCheckedChangeListener(mCheckedListener);
        mNormalCb.setChecked(true);
        mSolvedCb.setChecked(true);
        mSelectTypeLayout.setOnClickListener(this);
        mCommitTv.setOnClickListener(this);
    }

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("执行巡检", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    private void initRfidView(RfidItem item) {
        GlideUtil.loadImage(mRfidImg, item.img, null);
        mRfidIdTv.setText("RFID 编号. " + item.no);
        mRfidTypeTv.setText("设备类型：" + item.type);
    }

    private CompoundButton.OnCheckedChangeListener mCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(!b) {
                return;
            }
            if(compoundButton == mNormalCb) {
                mAbnormalCb.setChecked(false);
                mAbnormalLayout.setVisibility(View.GONE);
            } else if(compoundButton == mAbnormalCb) {
                mNormalCb.setChecked(false);
                mAbnormalLayout.setVisibility(View.VISIBLE);
            } else if(compoundButton == mSolvedCb) {
                mUnresolvedCb.setChecked(false);
            } else if(compoundButton == mUnresolvedCb) {
                mSolvedCb.setChecked(false);
            }
        }
    };

    private void initData() {
        mDetail = (InspectionDetail) getIntent().getSerializableExtra("inspect_detail");
        mTaskId = getIntent().getStringExtra("task_id");
        if(mDetail == null) {
            return;
        }
        mIdTv.setText("巡检点" + mDetail.name);
        if(mDetail.rfidList != null && mDetail.rfidList.size() > 0) {
            initRfidView(mDetail.rfidList.get(0));
        }

        mImgList = new ArrayList<>();
        mImgAdapter = new ImageSelectAdapter(this, mImgList, true);
        mImgsGv.setAdapter(mImgAdapter);
        mImgsGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == mImgAdapter.getCount()-1 && mImgList.size() < MAX_COUNT) {
                    if(checkTakePhotoPermission()) {
                        takePhoto();
                    }
                }
            }
        });

        getErrorTypeData();
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mSelectTypeLayout) {
            selectTypeDialog(this);
        } else if(view == mCommitTv) {
            doCommit();
        }
    }

    private void doCommit() {
        int isNormal = 1;
        String[] fileIds = null;
        String abnormalType = "";
        String abnormalResult = "";
        String abnormalInfo = "";
        String nickName = UserInfo.getInstance().getName();
        if(mAbnormalCb.isChecked()) {
            isNormal = 2;
            if(mSelectType == null) {
                ToastUtil.toastLongMessage("请选择异常类型");
                return;
            }
            abnormalType = mSelectType.id;
            abnormalResult = mSolvedCb.isChecked() ? "现场已解决" : "暂未解决";
        }
        if(mFileIds.size() > 0) {
            fileIds = new String[mFileIds.size()];
            mFileIds.toArray(fileIds);
        }

        new TaskDoneRequest(this, mTaskId, fileIds, isNormal, abnormalType, abnormalResult, abnormalInfo, nickName).schedule(true, new RequestListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
//                ToastUtil.toastLongMessage("提交成功");
                Intent i = new Intent(ExecuteInspectActivity.this, SuccessActivity.class);
                i.putExtra("from", 1);
                i.putExtra("success_tip", getString(R.string.inspect_success_tip, mRfidIdTv.getText().toString()));
                startActivity(i);
                finish();
            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.d(TAG, "onActivityResult：" + data);
        if(requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImgList.add(photoFile.getAbsolutePath());
            mImgAdapter.notifyDataSetChanged();
            uploadFile(photoFile);
        }
    }

    private void uploadFile(File file) {
        new UploadImgRequest(this, file).schedule(true, new RequestListener<String>() {
            @Override
            public void onSuccess(String result) {
                if(!TextUtils.isEmpty(result)) {
                    mFileIds.add(result);
                }
            }

            @Override
            public void onFailed(Throwable e) {

            }
        });
    }

    private void getErrorTypeData() {
        new GetTypeListRequest(this).schedule(true, new RequestListener<List<TypeInfo>>() {
            @Override
            public void onSuccess(List<TypeInfo> result) {
                if(result != null && result.size() > 0) {
                    mTypeList = result;
                }
            }

            @Override
            public void onFailed(Throwable e) {
                ToastUtil.toastLongMessage(e.getMessage());
            }
        });
    }

    private void selectTypeDialog(final Context context) {
        if(mTypeList == null || mTypeList.size() == 0) {
            getErrorTypeData();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.InputDialogStyle);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_select_abnormal_type, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_select_abnormal_type);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        ImageView closeIv = (ImageView) window.findViewById(R.id.dialog_common_view_close_btn);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        NoScrollListView lv = (NoScrollListView) window.findViewById(R.id.dialog_abnormal_type_lv);
        TypeDialogAdapter adapter = new TypeDialogAdapter(this, mTypeList);
        lv.setAdapter(adapter);
        TextView okTv = window.findViewById(R.id.dialog_ok_tv);
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TypeInfo info = adapter.getSelectedItem();
                if(info == null) {
                    ToastUtil.toastLongMessage("请选择异常类型");
                    return;
                }
                setTypeInfo(info);
                dialog.dismiss();
            }
        });
    }

    private void setTypeInfo(TypeInfo info) {
        mAbnormalTypeTv.setText(info.value);
        mSelectType = info;
    }

    private File photoFile;
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = FileUtil.createImageFile();
            LogUtil.d(TAG, "FileName：" + photoFile.getAbsolutePath());
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.pda.patrol.pda.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQ_TAKE_PHOTO);
        }
    }

    private boolean checkTakePhotoPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQ_TAKE_PHOTO);
        return false;
    }
}
