package com.mcpekorea.mdt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @since 2015-03-08
 * @author onebone <jyc0410@naver.com>
 */
public class ProjectActivity extends ActionBarActivity implements View.OnClickListener{
	private Project project;
	private ProjectAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		Bundle bundle = getIntent().getExtras();
		project = WorkspaceActivity.projects.get(bundle.getInt("projectIndex"));

		setTitle(getText(R.string.app_name).toString() + ": " + project.getName());
        findOverlappedPatches();

        findViewById(R.id.project_fab_add).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), CreatePatchActivity.class);
                intent.putExtra("patchIndex", -1);

                startActivityForResult(intent, 0);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.project_list);
        listView.setAdapter((adapter = new ProjectAdapter(this, project.getPatches(), this)));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Patch patch = project.getPatches().get(position);

                Intent intent = new Intent(view.getContext(), CreatePatchActivity.class);
                intent.putExtra("patchIndex", position);
                intent.putExtra("offsetString", patch.getOffset().toString());
                intent.putExtra("valueString", patch.getValue().toString());
                intent.putExtra("memo", patch.getMemo());
                intent.putExtra("isExcluded", patch.isExcluded());

                startActivityForResult(intent, 1);
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
            final LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dialog_project_export, null);
            final Spinner typeSpinner = (Spinner) layout.findViewById(R.id.dialog_project_export_type_spinner);

            new AlertDialog.Builder(this)
                    .setTitle(R.string.project_export_type)
                    .setView(layout)
                    .setPositiveButton(R.string.action_export, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            exportProject(typeSpinner.getSelectedItemPosition());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
			return true;
		}else if(id == R.id.menu_change_author_name){
            final LinearLayout layout = (LinearLayout) View.inflate(this, R.layout.dialog_project_change_author_name, null);
            final EditText authorArea = (EditText) layout.findViewById(R.id.dialog_project_change_author_name_author_area);
            authorArea.setText(project.getAuthor());

            new AlertDialog.Builder(this)
                    .setTitle(R.string.action_change_author_name)
                    .setView(layout)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            project.setAuthor(authorArea.getText().toString().trim());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK){
            project.addPatch(createPatchFromBundle(data.getExtras()));
            findOverlappedPatches();
            adapter.notifyDataSetChanged();
        }else if(requestCode == 1 && resultCode == RESULT_OK){
            int patchIndex = data.getIntExtra("patchIndex", -1);
            if(patchIndex >= 0){
                if(data.getBooleanExtra("deleted", false)){
                    project.removePatch(patchIndex);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, R.string.toast_patch_deleted, Toast.LENGTH_LONG).show();
                }else{
                    project.setPatch(patchIndex, createPatchFromBundle(data.getExtras()));
                    findOverlappedPatches();
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Patch createPatchFromBundle(Bundle bundle){
        byte[] offsetBytes = bundle.getByteArray("offsetBytes");
        byte[] valueBytes = bundle.getByteArray("valueBytes");

        String memo = bundle.getString("memo", "");
        boolean isExcluded = bundle.getBoolean("isExcluded", false);

        return new Patch(new Offset(offsetBytes), new Value(valueBytes), memo, isExcluded);
    }

	public boolean findOverlappedPatches(){
		for(int i = 0; i < this.project.getPatchesCount(); i++){
			Patch a = this.project.getPatches().get(i);
			if(a.isExcluded()) continue;
			for(int j = 0; j < this.project.getPatchesCount(); j++){
                if(i != j){
                    Patch b = this.project.getPatches().get(j);
	                if(b.isExcluded()) continue;
                    if(!(b.getPatchEnd() <= a.getPatchStart() || a.getPatchEnd() <= b.getPatchStart())){
                        a.setOverlapped(true);
                        b.setOverlapped(true);
                        return true;
                    }else{
                        a.setOverlapped(false);
                        b.setOverlapped(false);
                    }
                }
			}
		}
        return false;
	}

    public void exportProject(int exportType){
        if(this.project.getPatchesCount() == 0){
            Toast.makeText(this, R.string.toast_project_empty, Toast.LENGTH_LONG).show();
            return;
        }

        if(this.project.getIncludedPatchesCount() == 0){
            Toast.makeText(this, R.string.toast_project_nothing_to_export, Toast.LENGTH_LONG).show();
            return;
        }

        if(this.project.hasOverlappedPatches()){
            Toast.makeText(this, R.string.toast_project_has_overlapped_patches, Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(WorkspaceActivity.EXPORT_DIRECTORY, project.getName() + "-" + WorkspaceActivity.exportTypes[exportType].toLowerCase() + ".mod");
        ProjectExporter exporter = new ProjectExporter(this.project);
        FileOutputStream fos = null;

        try {
            byte[] bytes = exporter.create();
            fos = new FileOutputStream(file);
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
    }

	@Override
	public void onClick(View v){
		switch(v.getId()){
			case  R.id.list_item_image:
				int position = (int)v.getTag();
				Patch patch = project.getPatches().get(position);

				patch.setExcluded(!patch.isExcluded());
				adapter.notifyDataSetChanged();
				break;
		}
	}
}