package com.mcpekorea.mdt;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * @since 2015-03-08
 * @author onebone <jyc0410@naver.com>
 */
public class SettingsActivity extends ActionBarActivity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

        ((TextView) findViewById(R.id.settings_inconsolata_license)).setTypeface(WorkspaceActivity.inconsolata);
        ((TextView) findViewById(R.id.settings_material_design_icons_license)).setTypeface(WorkspaceActivity.inconsolata);
        ((TextView) findViewById(R.id.settings_floating_action_button_license)).setTypeface(WorkspaceActivity.inconsolata);
	}
}
