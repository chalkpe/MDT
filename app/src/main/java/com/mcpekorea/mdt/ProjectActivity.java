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
import android.widget.Toast;

import com.mcpekorea.ptpatch.Offset;
import com.mcpekorea.ptpatch.Patch;
import com.mcpekorea.ptpatch.Project;
import com.mcpekorea.ptpatch.Value;

/**
 * @since 2015-03-08
 * @author onebone <jyc0410@naver.com>
 */
public class ProjectActivity extends ActionBarActivity implements View.OnClickListener{
	private Project project;
    private int projectIndex;
	private ProjectAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		Bundle bundle = getIntent().getExtras();
        projectIndex = bundle.getInt("projectIndex", -1);
        if(projectIndex == -1){
            setResult(RESULT_CANCELED);
            finish();
        }
		project = WorkspaceActivity.projects.get(projectIndex);

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
            Intent intent = new Intent(this, ExportProjectActivity.class);
            intent.putExtra("projectIndex", this.projectIndex);
            startActivity(intent);
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

                    WorkspaceActivity.toast(R.string.toast_patch_deleted, Toast.LENGTH_LONG);
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
        if(keyCode == KeyEvent.KEYCODE_BACK){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case  R.id.list_item_image:
                int position = (Integer) v.getTag();
                Patch patch = project.getPatches().get(position);

                patch.setExcluded(!patch.isExcluded());
                if(patch.isExcluded()){
                    patch.setOverlapped(false);
                }

                findOverlappedPatches();
                adapter.notifyDataSetChanged();
                break;
        }
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
}