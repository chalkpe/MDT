package com.mcpekorea.mdt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 * @author onebone <jyc0410@naver.com>
 */

public class ProjectAdapter extends BaseAdapter{
	private Context context;
	private List<Patch> patches;
	private LayoutInflater inflater;

	public ProjectAdapter(Context context, List<Patch> patches){
		if(context == null){
			throw new NullPointerException("context must not be null");
		}
		if(patches == null){
			patches = new ArrayList<>();
		}

		this.context = context;
		this.patches = patches;

		this.inflater = LayoutInflater.from(context);
	}

	public void addPatch(Patch patch){
		addPatch(patch, true);
	}

	public void addPatch(Patch patch, boolean update){
		this.patches.add(patch);
		if(update) {
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount(){
		return this.patches.size();
	}

	@Override
	public Object getItem(int position){
		return this.patches.get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		Patch patch = this.patches.get(position);
		ProjectHolder holder;

		if(convertView == null){
			convertView = this.inflater.inflate(R.layout.list_patches, parent, false);

			holder = new ProjectHolder();
			holder.offset = (TextView) convertView.findViewById(R.id.list_item_offset);
			holder.value = (TextView) convertView.findViewById(R.id.list_item_value);

			convertView.setTag(holder);
		}else{
			holder = (ProjectHolder) convertView.getTag();
		}

		holder.offset.setText(patch.getOffset().toString());
		holder.value.setText(patch.getValue().toString());

		return convertView;
	}

	class ProjectHolder {
		public TextView offset;
		public TextView value;
	}
}
