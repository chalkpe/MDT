package com.mcpekorea.mdt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
	private View.OnClickListener listener;

	public ProjectAdapter(Context context, List<Patch> patches, View.OnClickListener listener){
		if(context == null){
			throw new NullPointerException("context must not be null");
		}
        if(patches == null){
            throw new NullPointerException("patches must not be null");
        }

		this.context = context;
		this.patches = patches;
		this.listener = listener;

		this.inflater = LayoutInflater.from(context);
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
			holder.offset = (TextView) convertView.findViewById(R.id.list_item_title);
			holder.value = (TextView) convertView.findViewById(R.id.list_item_subtitle);
			holder.image = (ImageView) convertView.findViewById(R.id.list_item_image);

            holder.offset.setTypeface(WorkspaceActivity.inconsolataBold);
            holder.value.setTypeface(WorkspaceActivity.inconsolata);
			holder.image.setTag(position);
			holder.image.setOnClickListener(listener);

			convertView.setTag(holder);
		}else{
			holder = (ProjectHolder) convertView.getTag();
		}

		holder.offset.setText(patch.getOffset().toString());
		holder.value.setText(patch.getValue().toString());

		if(patch.isOverlapped()){
			holder.image.setImageResource(R.drawable.ic_error_grey600_36dp);
		}else if(patch.isExcluded()){
			holder.image.setImageResource(R.drawable.ic_radio_button_off_grey600_36dp);
		}else{
            holder.image.setImageResource(R.drawable.ic_radio_button_on_grey600_36dp);
        }

		return convertView;
	}

	class ProjectHolder {
		public TextView offset;
		public TextView value;
		public ImageView image;
	}
}