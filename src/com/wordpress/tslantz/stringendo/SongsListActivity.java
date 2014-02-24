package com.wordpress.tslantz.stringendo;

import android.app.Activity;
import android.os.Bundle;

public final class SongsListActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == savedInstanceState) {
			final SongsListFragment fragment = new SongsListFragment();
			this.getFragmentManager()
				.beginTransaction()
				.add(
					android.R.id.content, 
					fragment,
					fragment.getClass().getSimpleName())
				.commit();
		}
	}

}
