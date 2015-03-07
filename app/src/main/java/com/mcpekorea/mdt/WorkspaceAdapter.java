package com.mcpekorea.mdt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 2015-03-06
 * @author ChalkPE <amato0617@gmail.com>
 */

public class WorkspaceAdapter extends BaseAdapter{
    private Context context;
    private List<Project> projects;
    private LayoutInflater inflater;

    public WorkspaceAdapter(Context context, List<Project> projects){
        if(context == null){
            throw new NullPointerException("context must not be null");
        }
        if(projects == null){
            projects = new LinkedList<Project>();
        }

        this.context = context;
        this.projects = projects;

        this.inflater = LayoutInflater.from(context);
    }

	public void addProject(Project project){
		this.projects.add(project);
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
            convertView.setTag(holder);
        }else{
            holder = (WorkspaceHolder) convertView.getTag();
        }
        holder.title.setText(project.getName());

	    String author = project.getAuthor();
	    if(author.equals("")){
		    author = "unknown";
	    }

        holder.subtitle.setText(String.format("%d patches, by %s", project.getPatchesCount(), author));

        return convertView;
    }

    class WorkspaceHolder {
        public TextView title;
        public TextView subtitle;
    }
}
