package com.wordpress.tslantz.stringendo;

import android.app.Activity;
import android.os.Bundle;

/**
 * Settings activity.
 */
public final class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == savedInstanceState) {
			final SettingsFragment fragment = new SettingsFragment();
			this.getFragmentManager()
				.beginTransaction()
				.add(
					android.R.id.content,
					fragment, 
					fragment.getClass().getSimpleName()
				)
				.commit();
		}
	}
	
}
