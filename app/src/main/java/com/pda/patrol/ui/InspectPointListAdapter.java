package com.pda.patrol.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.InspectionDetail;

import java.util.List;

/***
 * 巡检点列表adapter
 */
public class InspectPointListAdapter extends BaseListItemAdapter<InspectionDetail> {
    public InspectPointListAdapter(Context context, List<InspectionDetail> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.inspect_point_item, null);
            holder.idTv = convertView.findViewById(R.id.inspect_point_item_id_tv);
            holder.rfidCountTv = convertView.findViewById(R.id.inspect_point_item_rfid_count_tv);
            holder.addressTv = convertView.findViewById(R.id.inspect_point_item_address_tv);
            holder.taskCountTv = convertView.findViewById(R.id.inspect_point_item_task_count_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InspectionDetail item = items.get(position);
        holder.idTv.setText("巡检点" + item.name);
        int rfidCount = 0;
        if(item.rfidList != null) {
            rfidCount = item.rfidList.size();
        }
        holder.rfidCountTv.setText(context.getString(R.string.inspect_rfid_count, rfidCount));
        holder.addressTv.setText(item.address);
        holder.taskCountTv.setText(item.tasksCount + "个");

        return convertView;
    }

    class ViewHolder {
        public TextView idTv;
        public TextView rfidCountTv;
        public TextView addressTv;
        public TextView taskCountTv;
    }
}
