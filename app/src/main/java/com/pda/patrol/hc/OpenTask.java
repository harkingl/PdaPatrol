package com.pda.patrol.hc;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.pda.patrol.HcPreferences;
import com.pda.patrol.MainApplication;
import com.pda.patrol.baseclass.component.BaseActivity;
import com.pda.patrol.util.LogUtil;
import com.pda.patrol.util.ToastUtil;
import com.xlzn.hcpda.uhf.UHFReader;
import com.xlzn.hcpda.uhf.entity.UHFReaderResult;
import com.xlzn.hcpda.uhf.enums.InventoryModeForPower;
import com.xlzn.hcpda.uhf.module.UHFReaderSLR;

public class OpenTask extends AsyncTask<String, Integer, UHFReaderResult> {
    private static final String TAG = OpenTask.class.getSimpleName();
        ProgressDialog progressDialog;
        private BaseActivity mContent;

        public OpenTask(BaseActivity activity) {
            this.mContent = activity;
        }

        @Override
        protected UHFReaderResult doInBackground(String... params) {
            return UHFReader.getInstance().connect(mContent);
        }

        @Override
        protected void onPostExecute(UHFReaderResult result) {
            super.onPostExecute(result);
            if (result.getResultCode() == UHFReaderResult.ResultCode.CODE_SUCCESS) {
//                UHFReader.getInstance().setSession(UHFSession.S1);
                if (UHFReaderSLR.getInstance().is5300) {
                    LogUtil.d(TAG, "onPostExecute: 省电模式");
                    UHFReader.getInstance().setInventoryModeForPower(InventoryModeForPower.POWER_SAVING_MODE);
                }
                UHFReader.getInstance().setPower(HcPreferences.getInstance().getInt(MainApplication.getInstance(), "pda", "power"));
//                ToastUtil.toastLongMessage("连接成功");
            } else {
                ToastUtil.toastLongMessage("连接失败");
            }
//            progressDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            progressDialog = new ProgressDialog(mContent);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setMessage(mContent.getString(R.string.start_connect));
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();
        }
    }