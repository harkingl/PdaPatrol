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
 * rfid列表adapter
 */
public class RfidDialogAdapter extends BaseListItemAdapter<RfidItem> {
    public RfidDialogAdapter(Context context, List<RfidItem> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.rfid_dialog_item, null);
            holder.iv = convertView.findViewById(R.id.rfid_item_img_iv);
            holder.idTv = convertView.findViewById(R.id.rfid_item_id_tv);
            holder.typeTv = convertView.findViewById(R.id.rfid_item_type_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RfidItem item = items.get(position);
        GlideUtil.loadImage(holder.iv, item.img, null);
        holder.idTv.setText("RFID 编号. " + item.id);
        holder.typeTv.setText("设备类型：" + item.type);

        return convertView;
    }

    class ViewHolder {
        public ImageView iv;
        public TextView idTv;
        public TextView typeTv;
    }
}
