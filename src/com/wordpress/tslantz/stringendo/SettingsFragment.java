package com.wordpress.tslantz.stringendo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Main settings activity fragment.
 */
public final class SettingsFragment extends PreferenceFragment {
	
	/**
	 * Loads settings from settings resource xml.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.settings);
	}
	
	/**
	 * Registers a preference change listener.
	 */
	@Override
	public void onStart() {
		super.onStart();
		final SharedPreferences prefs = PreferenceManager
			.getDefaultSharedPreferences(this.getActivity());
		prefs.registerOnSharedPreferenceChangeListener(
			new OnSharedPreferenceChangeListener() {

				@Override
				public void onSharedPreferenceChanged(
						SharedPreferences sharedPreferences, String key) {
					// TODO Auto-generated method stub
					
				}
				
			}
		);
	}
	
}
