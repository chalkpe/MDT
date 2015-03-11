package com.mcpekorea.mdt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */

public class WorkspaceAdapter extends BaseAdapter{
    private Context context;
    private List<Project> projects;
    private LayoutInflater inflater;
    private View.OnClickListener buttonListener;

    public WorkspaceAdapter(Context context, List<Project> projects, View.OnClickListener buttonListener){
        if(context == null){
            throw new NullPointerException("context must not be null");
        }
        if(projects == null){
            throw new NullPointerException("projects must not be null");
        }

        this.context = context;
        this.projects = projects;
        this.buttonListener = buttonListener;

        this.inflater = LayoutInflater.from(context);
    }

	public void addProject(Project project){
		this.projects.add(project);
	}

	public void removeProject(Project project){
		this.projects.remove(project);
	}

	public void removeProject(int index){
		this.projects.remove(index);
	}

    @Override
    public int getCount(){
        return this.projects.size();
    }

    @Override
    public Object getItem(int position){
        return this.projects.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Project project = this.projects.get(position);
        WorkspaceHolder holder;

        if(convertView == null){
            convertView = this.inflater.inflate(R.layout.list_item, parent, false);

            holder = new WorkspaceHolder();
            holder.title = (TextView) convertView.findViewById(R.id.list_item_title);
            holder.subtitle = (TextView) convertView.findViewById(R.id.list_item_subtitle);
            holder.button = (ImageButton) convertView.findViewById(R.id.list_item_image);

            holder.button.setImageResource(R.drawable.ic_delete_black_36dp);
            holder.button.setTag(position);
            holder.button.setOnClickListener(this.buttonListener);

            convertView.setTag(holder);
        }else{
            holder = (WorkspaceHolder) convertView.getTag();
        }

        holder.title.setText(project.getName());

	    if(project.getAuthor().equals("")){
            project.setAuthor(context.getText(R.string.default_authorName).toString());
	    }
        holder.subtitle.setText(String.format(context.getText(R.string.workspace_project_information).toString(), project.getPatchesCount(), project.getAuthor()));

        return convertView;
    }

    class WorkspaceHolder {
        public TextView title;
        public TextView subtitle;
        public ImageButton button;
    }
}