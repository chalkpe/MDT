package com.mcpekorea.mdt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class FileChooserAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private File[] files;

    public FileChooserAdapter(Context context, File[] files){
        if(context == null){
            throw new NullPointerException("context must not be null");
        }
        this.files = files;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return this.files.length;
    }

    @Override
    public Object getItem(int position){
        return this.files[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        File file = this.files[position];
        FileHolder holder;

        if(convertView == null){
            convertView = this.inflater.inflate(R.layout.list_item_file_chooser, parent, false);

            holder = new FileHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.list_item_file_chooser_icon);
            holder.text = (TextView) convertView.findViewById(R.id.list_item_file_chooser_text);

            convertView.setTag(holder);
        }else{
            holder = (FileHolder) convertView.getTag();
        }

        holder.icon.setImageResource(file.isDirectory() ? R.drawable.ic_folder_grey600_48dp : R.drawable.ic_insert_drive_file_grey600_48dp);
        holder.text.setText(file.getName());

        return convertView;
    }

    class FileHolder {
        public ImageView icon;
        public TextView text;
    }
}
