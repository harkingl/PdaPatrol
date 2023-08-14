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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.baseclass.component.ITitleBarLayout;
import com.pda.patrol.baseclass.component.NoScrollGridView;
import com.pda.patrol.baseclass.component.NoScrollListView;
import com.pda.patrol.baseclass.component.TitleBarLayout;
import com.pda.patrol.entity.AddressInfo;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.util.FileUtil;
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstallActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = InstallActivity.class.getSimpleName();

    private static final int REQ_TAKE_PHOTO = 111;
    private static final int MAX_COUNT = 3;
    private TitleBarLayout mTitlebarLayout;
    private View mSelectItemLayout;
    private ImageView mSelectImgIv;
    private TextView mSelectIdTv;
    private View mSelectAddressLayout;
    private TextView mSelectAddressTv;
    private NoScrollGridView mImgGv;
    private EditText mNoteEt;
    private TextView mCommitTv;
    private RfidItem mSelectItem;
    private ArrayList<RfidItem> mRfidList;
    private AddressInfo mSelectAddressInfo;
    private ArrayList<AddressInfo> mAddressList;
    private ImageSelectAdapter mImgAdapter;
    private List<String> mImgList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        initView();
        configTitleBar();
        initData();
    }

    private void initView() {
        mTitlebarLayout = findViewById(R.id.install_title_bar);
        mSelectItemLayout = findViewById(R.id.install_select_item_ll);
        mSelectIdTv = findViewById(R.id.install_select_id_tv);
        mSelectImgIv = findViewById(R.id.install_select_img_iv);
        mSelectAddressLayout = findViewById(R.id.install_select_address_ll);
        mSelectAddressTv = findViewById(R.id.install_select_address_tv);
        mImgGv = findViewById(R.id.install_imgs_gv);
        mNoteEt = findViewById(R.id.install_note_et);
        mCommitTv = findViewById(R.id.install_commit_tv);

        mSelectItemLayout.setOnClickListener(this);
        mSelectAddressLayout.setOnClickListener(this);
        mCommitTv.setOnClickListener(this);
    }

    private void initData() {
        mSelectItem = (RfidItem) getIntent().getSerializableExtra("select_item");
        mRfidList = (ArrayList<RfidItem>) getIntent().getSerializableExtra("frid_list");

        setView(mSelectItem);
        mImgList = new ArrayList<>();
        mImgAdapter = new ImageSelectAdapter(this, mImgList);
        mImgGv.setAdapter(mImgAdapter);
        mImgGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == mImgAdapter.getCount()-1 && mImgList.size() < MAX_COUNT) {
                    if(checkTakePhotoPermission()) {
                        takePhoto();
                    }
                }
            }
        });

        mAddressList = new ArrayList<>();
        AddressInfo info1 = new AddressInfo();
        info1.address = "闵行区-春申路-莲花南路交叉口";
        mAddressList.add(info1);
        AddressInfo info2 = new AddressInfo();
        info2.address = "闵行区-春申路-虹梅南路交叉口";
        mAddressList.add(info2);

        setAddressInfo(mAddressList.get(0));
    }

    private void setView(RfidItem item) {
        if(item != null) {
            GlideUtil.loadImage(mSelectImgIv, item.img, null);
            mSelectIdTv.setText("RFID NO : " + item.id);
            mSelectItem = item;
        }
    }

    private void setAddressInfo(AddressInfo info) {
        if(info != null) {
            mSelectAddressTv.setText(info.address);
            mSelectAddressInfo = info;
        }
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

    private void configTitleBar() {
        mTitlebarLayout.getRightIcon().setVisibility(View.GONE);
        mTitlebarLayout.setTitle("安装巡检点", ITitleBarLayout.Position.LEFT);
        mTitlebarLayout.setOnLeftClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mTitlebarLayout.getLeftGroup()) {
            finish();
        } else if(view == mSelectItemLayout) {
            selectFridDialog(this);
        } else if(view == mSelectAddressLayout) {
            selectAddressDialog(this);
        } else if(view == mCommitTv) {
            doCommit();
        }
    }

    private void doCommit() {
        String address = mSelectAddressTv.getText().toString();
        if(TextUtils.isEmpty(address)) {
            ToastUtil.toastLongMessage("请选择安装网点");
            return;
        }
        mSelectItem.address = address;
        Intent i = new Intent(this, PatrolDetailActivity.class);
        i.putExtra("rfid_info", mSelectItem);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LogUtil.d(TAG, "onActivityResult：" + data);
        if(requestCode == REQ_TAKE_PHOTO && resultCode == RESULT_OK) {
            mImgList.add(photoFile.getAbsolutePath());
            mImgAdapter.notifyDataSetChanged();
        }
    }

    private void selectFridDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.InputDialogStyle);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_select_frid, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_select_frid);
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
        TextView dialogCount = (TextView) window.findViewById(R.id.dialog_count_tv);
        dialogCount.setText(getString(R.string.frid_count, mRfidList.size()));
        NoScrollListView lv = (NoScrollListView) window.findViewById(R.id.dialog_frid_lv);
        lv.setAdapter(new RfidDialogAdapter(this, mRfidList));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setView(mRfidList.get(i));
                dialog.dismiss();
            }
        });
    }

    private void selectAddressDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.InputDialogStyle);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_select_address, null);
        final AlertDialog dialog = builder.create();
        dialog.setView(layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.dialog_select_address);
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
        NoScrollListView lv = (NoScrollListView) window.findViewById(R.id.dialog_address_lv);
        AddressDialogAdapter adapter = new AddressDialogAdapter(this, mAddressList);
        lv.setAdapter(adapter);
        TextView okTv = window.findViewById(R.id.dialog_ok_tv);
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressInfo info = adapter.getSelectedItem();
                if(info == null) {
                    ToastUtil.toastLongMessage("请选择网点");
                    return;
                }
                setAddressInfo(info);
                dialog.dismiss();
            }
        });

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        LogUtil.d(TAG, "#######onRequestPermissionsResult######" + grantResults.length);
        if(requestCode == REQ_TAKE_PHOTO) {
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            takePhoto();
        }
    }
}
