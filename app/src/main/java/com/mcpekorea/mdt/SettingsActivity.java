package com.mcpekorea.mdt;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * @author onebone <jyc0410@naver.com>
 * @since 2015-03-13
 */
public class SettingsActivity extends Activity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}

	public static class PrefsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference);
		}
	}
}
