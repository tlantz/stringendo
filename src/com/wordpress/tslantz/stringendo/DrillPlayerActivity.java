package com.wordpress.tslantz.stringendo;

import android.app.Activity;
import android.os.Bundle;

public final class DrillPlayerActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == savedInstanceState) {
			final DrillPlayerFragment f = new DrillPlayerFragment();
			this.getFragmentManager()
				.beginTransaction()
				.add(
					android.R.id.content, 
					f,
					f.getClass().getSimpleName())
				.commit();
		}
	}

}
