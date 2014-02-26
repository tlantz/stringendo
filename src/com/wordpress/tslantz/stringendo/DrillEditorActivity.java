package com.wordpress.tslantz.stringendo;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public final class DrillEditorActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.drill_editor);
		final Button cancelButton = (Button)this.findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				DrillEditorActivity.this.finish();
			}

		});
		final Button saveButton = (Button)this.findViewById(R.id.button_save);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				final long songId = getIntent().getExtras().getLong(
					MediaStore.Audio.Media._ID);
				final Cursor c = getContentResolver().query(
					ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
						songId
					),
					new String[] {
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DATA
					}, 
					null,
					null,
					null
				);
				if (1 > c.getCount()) {
					Toast.makeText(
						DrillEditorActivity.this, 
						"song with id " + songId + " was not found in MediaStore", 
						Toast.LENGTH_SHORT
					).show();
				} else if (1 < c.getCount()) {
					Toast.makeText(
						DrillEditorActivity.this,
						"song with id " + songId + " was found multiple times in MediaStore", 
						Toast.LENGTH_LONG
					).show();
				}
				c.moveToFirst();
				final String title = c.getString(c.getColumnIndex(
					MediaStore.Audio.Media.TITLE));
				final String path = c.getString(c.getColumnIndex(
					MediaStore.Audio.Media.DATA));
				final EditText nameBox = (EditText)findViewById(
					R.id.text_edit_drill_name);
				final ContentValues content = new ContentValues();
				content.put(DrillTrackContract.Column.SONG_NAME, title);
				content.put(DrillTrackContract.Column.SONG_PATH, path);
				content.put(DrillTrackContract.Column.DRILL_NAME,
					nameBox.getText().toString());
				content.put(DrillTrackContract.Column.SLOW_FACTOR, 0.10f);
				content.put(DrillTrackContract.Column.FAST_FACTOR, 1.25f);
				getContentResolver().insert(
					DrillTrackContract.CONTENT_URI,
					content
				);
				finish();
			}
			
		});
	}
	
}
