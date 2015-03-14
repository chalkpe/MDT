package com.mcpekorea.mdt;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;

/**
 * @author onebone <jyc0410@naver.com>
 * @since 2015-03-13
 */
public class SettingsActivity extends ActionBarActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
	}

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            final EditTextPreference defaultValuePreference = (EditTextPreference) getPreferenceScreen().findPreference("defaultValue");
            defaultValuePreference.setSummary(defaultValuePreference.getText());

            defaultValuePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue){
                    defaultValuePreference.setSummary((String) newValue);
                    return true;
                }
            });
        }
    }
}