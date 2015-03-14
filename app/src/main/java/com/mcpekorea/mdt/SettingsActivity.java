package com.mcpekorea.mdt;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

/**
 * @author onebone <jyc0410@naver.com>
 * @since 2015-03-13
 */
public class SettingsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.action_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_action_bar);
        toolbar.setTitle(R.string.title_activity_settings);
	}

	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference);
		}
	}
}