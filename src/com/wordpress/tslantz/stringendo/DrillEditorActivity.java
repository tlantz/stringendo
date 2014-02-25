package com.wordpress.tslantz.stringendo;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
				final EditText nameBox = (EditText)findViewById(
					R.id.text_edit_drill_name);
				final ContentValues content = new ContentValues();
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
