package com.wordpress.tslantz.stringendo;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public final class DrillPlayerFragment extends Fragment {

	private final SoundPlayer mPlayer = new JLayerSoundPlayer();
	
	private int mEndMSec;
	private ToggleButton mLoopButton;
	private int mStartMSec;
	private SoundPlayer.Track mTrack;
	
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
						mTrack.loop(1.0f, 5000);
					} else {
						mTrack.pause();
					}
				}
				
			}
		);
		return view;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SoundPlayer.Track oldTrack = this.mTrack;
		this.mTrack = null;
		if (null != oldTrack) {
			oldTrack.close();
		}
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
		final SoundPlayer.Track oldTrack = this.mTrack;
		this.mTrack = null;
		if (null != oldTrack) {
			oldTrack.close();
		}
		if (0 < id) {
			final Uri uri = ContentUris.withAppendedId(
				DrillTrackContract.CONTENT_URI, id);
			final Cursor c = this.getActivity().getContentResolver()
				.query(uri, null, null, null, null);
			try {
				if (c.moveToFirst()) {
					final String songPath = c.getString(c.getColumnIndex(
						DrillTrackContract.Column.SONG_PATH));
					this.mStartMSec = c.getInt(c.getColumnIndex(
						DrillTrackContract.Column.BEGIN_MSEC));
					this.mEndMSec = c.getInt(c.getColumnIndex(
						DrillTrackContract.Column.END_MSEC));
					try {
						this.mTrack = this.mPlayer.load(songPath, this.mStartMSec, 
							this.mEndMSec);
						this.mLoopButton.setEnabled(true);
					} catch (Exception e) {
						Toast.makeText(
							this.getActivity(), 
							"Playback failed: " + e, 
							Toast.LENGTH_LONG
						).show();
					}
				}
			} finally {
				c.close();
			}
		}
	}
	
}
