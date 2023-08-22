package com.pda.patrol.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pda.patrol.R;
import com.pda.patrol.baseclass.BaseListItemAdapter;
import com.pda.patrol.entity.RfidItem;
import com.pda.patrol.util.GlideUtil;

import java.util.List;

/***
 * 执行巡检扫描-rfid列表adapter
 */
public class InspectRfidListAdapter extends BaseListItemAdapter<RfidItem> {
    public InspectRfidListAdapter(Context context, List<RfidItem> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.inspect_rfid_list_item, null);
            holder.idTv = convertView.findViewById(R.id.rfid_item_id_tv);
            holder.scanTv = convertView.findViewById(R.id.rfid_item_scan_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RfidItem item = items.get(position);
        holder.idTv.setText("RFID 编号. " + item.id);
        holder.scanTv.setVisibility(item.isScan ? View.VISIBLE : View.GONE);

        return convertView;
    }

    class ViewHolder {
        public TextView idTv;
        public TextView scanTv;
    }
}
