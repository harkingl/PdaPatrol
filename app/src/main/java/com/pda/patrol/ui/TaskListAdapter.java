package com.pda.patrol.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.TaskInfo;
import com.pda.patrol.util.DateUtil;
import com.pda.patrol.util.ScreenUtil;

import java.util.List;

/***
 * 任务列表adapter
 */
public class TaskListAdapter extends BaseListItemAdapter<TaskInfo> {
    private int mCurrentTabState = -1;
    public TaskListAdapter(Context context, List<TaskInfo> list, int tabState) {
        super(context, list);

        this.mCurrentTabState = tabState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.task_list_item, null);
            holder.idTv = convertView.findViewById(R.id.task_item_id_tv);
            holder.statusTv = convertView.findViewById(R.id.task_item_status_tv);
            holder.typeTv = convertView.findViewById(R.id.task_item_type_tv);
            holder.addressTv = convertView.findViewById(R.id.task_item_address_tv);
            holder.timeTv = convertView.findViewById(R.id.task_item_time_tv);
            holder.detailTv = convertView.findViewById(R.id.task_item_detail_tv);
            holder.inspectTv = convertView.findViewById(R.id.task_item_goto_inspect_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskInfo item = items.get(position);
        holder.idTv.setText(item.rfidNo);
        holder.statusTv.setVisibility(View.GONE);
        setState(holder.statusTv, item.taskState);
        holder.typeTv.setText(item.rfidTypeName);
        holder.addressTv.setText(item.address);
        if(item.taskState == 0 || item.taskState == 2) {
            String startTime = DateUtil.convertTimeFormat(item.crt, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
            String endTime = DateUtil.convertTimeFormat(item.endTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDD);
            holder.timeTv.setText("任务时间：" + startTime + "- " + endTime);
        } else {
            holder.timeTv.setText("巡检时间：" + DateUtil.convertTimeFormat(item.dealTime, DateUtil.FORMAT_YYYYMMDDTHHMMSSTZD, DateUtil.FORMAT_YYYYMMDDHHMM));
        }
        setInspectionBtn(holder.inspectTv, item.taskState, item.isNormal);
        holder.detailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = null;
                if(item.taskState == 0) {
                    i = new Intent(context, TaskTodoDetailActivity.class);
                } else if(item.taskState == 2) {
                    i = new Intent(context, TaskOverdueDetailActivity.class);
                } else {
                    i = new Intent(context, TaskFinishDetailActivity.class);
                }
                i.putExtra("task_info", item);
                context.startActivity(i);
            }
        });
        if(item.taskState == 0 || item.taskState == 2) {
            holder.inspectTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gotoInspectPage(item.inspectionId, item.id);
                }
            });
        } else {
            holder.inspectTv.setOnClickListener(null);
        }

        return convertView;
    }

    private void gotoInspectPage(String id, String taskId) {
        Intent i = new Intent(context, InspectScanActivity.class);
        i.putExtra("inspect_id", id);
        i.putExtra("task_id", taskId);
        context.startActivity(i);
    }

    private void setState(TextView tv, int taskState) {
        if(mCurrentTabState == -1) {
            tv.setVisibility(View.VISIBLE);
            switch (taskState) {
                case 0:
                    tv.setText("待执行");
                    tv.setBackgroundColor(context.getColor(R.color.common_blue_color));
                    break;
                case 1:
                    tv.setText("已完成");
                    tv.setBackgroundColor(context.getColor(R.color.common_green_color));
                    break;
                case 2:
                    tv.setText("已逾期");
                    tv.setBackgroundColor(context.getColor(R.color.common_red_color));
                    break;
                default:
                    tv.setVisibility(View.GONE);
                    break;
            }
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private void setInspectionBtn(TextView tv, int taskState, int isNormal) {
        switch (taskState) {
            case 0:
                tv.setText("去巡检");
                tv.setBackgroundResource(R.drawable.border_blue_shape);
                tv.setPadding(ScreenUtil.dip2px(12), ScreenUtil.dip2px(3), ScreenUtil.dip2px(12), ScreenUtil.dip2px(3));
                tv.setTextColor(context.getColor(R.color.common_blue_color));
                break;
            case 1:
                if(isNormal == 1) {
                    tv.setText("巡检正常");
                    tv.setTextColor(context.getColor(R.color.common_green_color));
                } else {
                    tv.setText("巡检异常");
                    tv.setTextColor(context.getColor(R.color.common_red_color));
                }
                tv.setPadding(0, 0, 0, 0);
                tv.setBackgroundResource(0);
                break;
            case 2:
                tv.setText("重新巡检");
                tv.setBackgroundResource(R.drawable.border_blue_shape);
                tv.setPadding(ScreenUtil.dip2px(9), ScreenUtil.dip2px(3), ScreenUtil.dip2px(9), ScreenUtil.dip2px(3));
                tv.setTextColor(context.getColor(R.color.common_blue_color));
                break;
            default:
                tv.setVisibility(View.GONE);
                break;
        }
    }

    class ViewHolder {
        public TextView idTv;
        public TextView statusTv;
        public TextView typeTv;
        public TextView addressTv;
        public TextView timeTv;
        public TextView detailTv;
        public TextView inspectTv;
    }
}
