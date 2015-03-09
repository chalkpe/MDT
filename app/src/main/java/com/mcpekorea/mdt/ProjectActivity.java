package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @since 2015-03-08
 * @author onebone <jyc0410@naver.com>
 */
public class ProjectActivity extends ActionBarActivity {
	private Project project;
	private ListView listView;
	private ProjectAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		Intent data = getIntent();
		project = (Project) data.getSerializableExtra("project");

		setTitle(project.getName());

        listView = (ListView) findViewById(R.id.project_list);
        listView.setAdapter((adapter = new ProjectAdapter(this, project.getPatches())));

		findViewById(R.id.project_fab_add).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Patch patch = new Patch(new Offset(new byte[]{0x00, 0x00, 0x00, 0x00}), new Value(new byte[]{0x00, 0x00}));
				adapter.addPatch(patch);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_project, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if(id == R.id.menu_export){
			if(this.project.getPatchesCount() == 0){
				Toast.makeText(this, R.string.toast_project_empty, Toast.LENGTH_LONG).show();
				return true;
			}

			ProjectExporter exporter = new ProjectExporter(this.project);
            FileOutputStream fos = null;

			try {
				byte[] bytes = exporter.create();
				fos = new FileOutputStream(new File(WorkspaceActivity.EXPORT_DIRECTORY, project.getName()+".mod"));
				fos.write(bytes);

				Toast.makeText(this, R.string.toast_project_exported, Toast.LENGTH_LONG).show();
			}catch(IOException e){
				new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title_error_occurred)
				        .setMessage(e.getMessage())
				        .setPositiveButton(android.R.string.ok, null)
                        .show();
			}finally{
                try{
                    if(fos != null){
                        fos.close();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
			return true;
		}else if(id == R.id.menu_change_author_name){
            final LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dialog_change_author_name, null);
            final EditText authorArea = (EditText) layout.findViewById(R.id.dialog_change_author_name_author_area);
            authorArea.setText(project.getAuthor());

            new AlertDialog.Builder(this)
                    .setTitle(R.string.action_change_author_name)
                    .setView(layout)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            project.setAuthor(authorArea.getText().toString());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
}