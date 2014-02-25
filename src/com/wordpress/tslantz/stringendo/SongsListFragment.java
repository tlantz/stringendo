package com.wordpress.tslantz.stringendo;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public final class SongsListFragment extends ListFragment {
	
	private final static int LOADER_ID = 21;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			this.getActivity(),
			R.layout.song_list_item,
			null,
			new String[] {
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.TITLE
			},
			new int[] {
				R.id.song_list_item_artist,
				R.id.song_list_item_song
			},
			0
		);
		this.setListAdapter(adapter);
		this.getLoaderManager().initLoader(LOADER_ID, null, 
			new LoaderCallbacks<Cursor>() {

				@Override
				public Loader<Cursor> onCreateLoader(int id, Bundle args) {
					if (LOADER_ID != id) {
						return null;
					} else {
						Log.d(SongsListFragment.class.getSimpleName(), 
							"onCreateLoader");
						final String[] proj = new String[] {
							MediaStore.Audio.Media._ID,
							MediaStore.Audio.Media.DATA,
							MediaStore.Audio.Media.ARTIST,
							MediaStore.Audio.Media.TITLE
						};
						return new CursorLoader(
							SongsListFragment.this.getActivity(),
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							proj,
							null,
							null,
							MediaStore.Audio.Media.TITLE
						);
					}
				}

				@Override
				public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
					Log.d(DrillsListFragment.class.getCanonicalName(),
						"loaded song count: " + cursor.getCount());
					adapter.swapCursor(cursor);
				}

				@Override
				public void onLoaderReset(Loader<Cursor> loader) {
					adapter.swapCursor(null);
				}
			
			}
		);
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		final DrillPlayerFragment player = (DrillPlayerFragment)this.getFragmentManager()
			.findFragmentById(R.id.fragment_drill_player);
		if (null != player && player.isVisible()) {
			player.updateView(id);
		} else {
			this.startActivity(new Intent(
				this.getActivity(),
				DrillEditorActivity.class
			).putExtra(DrillTrackContract.Column.ID, id));
		}
	}

}
