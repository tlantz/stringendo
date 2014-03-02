package com.wordpress.tslantz.stringendo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		Toast.makeText(this, new SoundPlayer().getMyData(), Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				this.startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_create:
				this.startActivity(new Intent(this, SongsListActivity.class));
				return true;
			default:
				return false;
		}
	}

}
