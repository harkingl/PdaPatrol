package com.pda.patrol.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.util.GlideUtil;
import com.pda.patrol.util.ScreenUtil;

import java.util.List;

/***
 * 任务详情页面巡检设备列表adapter
 */
public class InspectRfidDialogAdapter extends BaseListItemAdapter<TaskInfo> {
    private AlertDialog mDialog;
    public InspectRfidDialogAdapter(Context context, List<TaskInfo> list, AlertDialog dialog) {
        super(context, list);

        this.mDialog = dialog;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.inspect_rfid_dialog_item, null);
            holder.iv = convertView.findViewById(R.id.rfid_item_img_iv);
            holder.idTv = convertView.findViewById(R.id.rfid_item_id_tv);
            holder.typeTv = convertView.findViewById(R.id.rfid_item_type_tv);
            holder.inspectTv = convertView.findViewById(R.id.rfid_goto_inspect_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskInfo info = items.get(position);
        GlideUtil.loadImageWithPlaceHolder(holder.iv, info.rfidUrl, R.drawable.ic_rfid_img1);
        holder.idTv.setText("RFID 编号. " + info.rfidNo);
        holder.typeTv.setText("设备类型：" + info.rfidTypeName);
        if(info.taskState == 0 || info.taskState == 2) {
            holder.inspectTv.setText("去巡检");
            holder.inspectTv.setBackgroundResource(R.drawable.border_blue_shape);
            holder.inspectTv.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(3), ScreenUtil.dip2px(12), ScreenUtil.dip2px(3));
            holder.inspectTv.setTextColor(context.getColor(R.color.common_blue_color));
            holder.inspectTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoInspectPage(info.inspectionId);
//                        if(mDialog != null) {
//                            mDialog.dismiss();
//                        }
                }
            });
        } else {
            if(info.isNormal == 1) {
                holder.inspectTv.setText("已巡检：正常");
                holder.inspectTv.setTextColor(context.getColor(R.color.common_green_color));
            } else {
                holder.inspectTv.setText("已巡检：异常");
                holder.inspectTv.setTextColor(context.getColor(R.color.common_red_color));
            }
            holder.inspectTv.setPadding(0, 0, 0, 0);
            holder.inspectTv.setBackgroundResource(0);
        }


        return convertView;
    }

    private void gotoInspectPage(String id) {
        Intent i = new Intent(context, InspectScanActivity.class);
        i.putExtra("inspect_id", id);
        context.startActivity(i);
    }

    class ViewHolder {
        public ImageView iv;
        public TextView idTv;
        public TextView typeTv;
        public TextView inspectTv;
    }
}
