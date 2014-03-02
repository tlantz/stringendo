package com.wordpress.tslantz.stringendo;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public final class DrillPlayerFragment extends Fragment {

	private int mEndMSec;
	private ToggleButton mLoopButton;
	private MediaPlayer mPlayer;
	private int mStartMSec;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.drill_player, null, false);
		this.mLoopButton = (ToggleButton)view.findViewById(R.id.loop_button);
		this.mLoopButton.setOnCheckedChangeListener(
			new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton button, boolean isChecked) {
					if (isChecked) {
						mPlayer.seekTo(mStartMSec);
						mPlayer.start();
					} else {
						mPlayer.stop();
						mPlayer.reset();
					}
				}
				
			}
		);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final long id = this.getActivity().getIntent().getLongExtra(
			DrillTrackContract.Column.ID, -1L);
		this.updateView(id);
	}
	
	public void updateView(long id) {
		this.mLoopButton.setEnabled(false);
		final MediaPlayer oldmp = this.mPlayer;
		this.mPlayer = null;
		if (null != oldmp) {
			oldmp.release();
		}
		if (0 < id) {
			final Uri uri = ContentUris.withAppendedId(
				DrillTrackContract.CONTENT_URI, id);
			final Cursor c = this.getActivity().getContentResolver()
				.query(uri, null, null, null, null);
			if (c.moveToFirst()) {
				final String songPath = c.getString(c.getColumnIndex(
					DrillTrackContract.Column.SONG_PATH));
				this.mPlayer = MediaPlayer.create(
					this.getActivity(), 
					Uri.parse(songPath)
				);
				this.mPlayer.setLooping(true);
				this.mStartMSec = c.getInt(c.getColumnIndex(
					DrillTrackContract.Column.BEGIN_MSEC));
				this.mEndMSec = c.getInt(c.getColumnIndex(
					DrillTrackContract.Column.END_MSEC));
				this.mLoopButton.setEnabled(true);
			}
		}
	}
	
}
