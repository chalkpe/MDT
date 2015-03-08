package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @since 2015-03-08
 * @author onebone <jyc0410@naver.com>
 */
public class PatchEditorActivity extends ActionBarActivity {
	private Project project;
	private ListView patchesView;
	private ProjectAdapter adapter;
	private EditText authorArea;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patch_editor);

		Intent data = getIntent();
		project = (Project)data.getSerializableExtra("project");

		setTitle(project.getName());

		authorArea = (EditText)findViewById(R.id.author_area);
		authorArea.setText(project.getAuthor());

		patchesView = (ListView)findViewById(R.id.list_patches);
		patchesView.setAdapter((adapter = new ProjectAdapter(this, project.getPatches())));

		findViewById(R.id.add_patch_btn).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Patch patch = new Patch(new Offset(new byte[]{0x00, 0x00, 0x00, 0x00}), new Value(new byte[]{0x00, 0x00}));
				adapter.addPatch(patch);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_patch_editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id = item.getItemId();
		if(id == R.id.menu_export_project){
			if(this.project.getPatchesCount() == 0){
				Toast.makeText(this, R.string.project_empty, Toast.LENGTH_LONG).show();
				return true;
			}
			this.project.setAuthor(authorArea.getText().toString());
			ProjectExporter exporter = new ProjectExporter(this.project);
			try {
				byte[] buffer = exporter.create();
				FileOutputStream fos = new FileOutputStream(new File(MainActivity.EXPORT_DIRECTORY, project.getName()+".mod"));
				fos.write(buffer);
				fos.close();
				Toast.makeText(this, R.string.project_exported, Toast.LENGTH_LONG).show();
			}catch(IOException e){
				new AlertDialog.Builder(this)
				.setTitle(R.string.error_occurred)
				.setMessage(e.getMessage())
				.setPositiveButton(android.R.string.yes, null).show();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
