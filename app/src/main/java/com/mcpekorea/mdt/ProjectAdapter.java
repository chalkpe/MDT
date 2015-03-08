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

	public void addPatch(Patch project){
		this.patches.add(project);
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
			convertView = this.inflater.inflate(R.layout.list_item, parent, false);

			holder = new ProjectHolder();
			holder.offset = (TextView) convertView.findViewById(R.id.list_item_offset);
			holder.value = (TextView) convertView.findViewById(R.id.list_item_value);

			convertView.setTag(holder);
		}else{
			holder = (ProjectHolder) convertView.getTag();
		}
		holder.offset.setText(patch.getOffset().toString());

		return convertView;
	}

	class ProjectHolder {
		public TextView offset;
		public TextView value;
	}
}
