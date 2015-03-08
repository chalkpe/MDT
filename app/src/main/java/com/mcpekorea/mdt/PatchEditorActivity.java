package com.mcpekorea.mdt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

/**
 * @since 2015-03-08
 * @author onebone <jyc0410@naver.com>
 */
public class PatchEditorActivity extends ActionBarActivity {
	private Project project;
	private ListView patchesView;
	private ProjectAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patch_editor);

		Intent data = getIntent();
		project = (Project)data.getSerializableExtra("project");

		setTitle(project.getName());

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
}
