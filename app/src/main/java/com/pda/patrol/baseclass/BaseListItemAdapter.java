package com.pda.patrol.baseclass;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListItemAdapter<E> extends BaseAdapter {

	protected Context context;

	protected List<E> items;

	protected LayoutInflater inflater;

	public BaseListItemAdapter(Context context, List<E> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.items = list;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}
	
	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<E> getItems() {
		return items;
	}

	public void setItems(List<E> e) {
		this.items = e;
		notifyDataSetChanged();
	}
}
