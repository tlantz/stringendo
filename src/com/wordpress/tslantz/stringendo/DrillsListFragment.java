package com.wordpress.tslantz.stringendo;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

public final class DrillsListFragment extends ListFragment {
	
	private static final int LOADER_ID = 14;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			this.getActivity(),
			R.layout.drill_list_item,
			null,
			new String[] {
				DrillTrackContract.Column.SONG_NAME,
				DrillTrackContract.Column.DRILL_NAME
			},
			new int[] {
				R.id.drill_list_item_song,
				R.id.drill_list_item_drill
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
						Log.d(DrillsListFragment.class.getSimpleName(), 
							"onCreateLoader");
						return new CursorLoader(
							DrillsListFragment.this.getActivity(),
							DrillTrackContract.CONTENT_URI,
							null,
							null,
							null,
							null
						);
					}
				}

				@Override
				public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
					Log.d(DrillsListFragment.class.getCanonicalName(),
						"loaded record count: " + cursor.getCount());
					adapter.swapCursor(cursor);
				}

				@Override
				public void onLoaderReset(Loader<Cursor> loader) {
					adapter.swapCursor(null);
				}
			
			}
		);
	}
	
}
