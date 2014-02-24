package com.wordpress.tslantz.stringendo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to drill track definitions.
 */
public final class DrillTrackProvider extends ContentProvider {
	
	private static final UriMatcher sMatcher = new UriMatcher(
		UriMatcher.NO_MATCH);
	
	private Database mDatabase;
	
	/**
	 * Set up URI matches for listing and fetching single resources.
	 */
	static {
		sMatcher.addURI(DrillTrackContract.AUTHORITY, DrillTrackContract.TABLE, 
			DrillTrackContract.MATCH_CODE_DIR);
		sMatcher.addURI(DrillTrackContract.AUTHORITY, DrillTrackContract.TABLE + "/#", 
			DrillTrackContract.MATCH_CODE_ITEM);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final String where = getWhere(uri, selection);
		final SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		final int retval = db.delete(
			DrillTrackContract.TABLE,
			where,
			selectionArgs
		);
		if (0 < retval) {
			this.getContext().getContentResolver().notifyChange(uri, null);
		}
		return retval;
	}

	/**
	 * Gets mime types for supported uris.
	 */
	@Override
	public String getType(Uri uri) {
		switch (sMatcher.match(uri)) {
			case DrillTrackContract.MATCH_CODE_DIR:
				return DrillTrackContract.MIME_DIR;
			case DrillTrackContract.MATCH_CODE_ITEM:
				return DrillTrackContract.MIME_ITEM;
			default:
				throw new IllegalArgumentException("bad URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (DrillTrackContract.MATCH_CODE_DIR != sMatcher.match(uri)) {
			throw new IllegalArgumentException("bad URI: " + uri);
		}
		final SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		final long insertId = db.insertWithOnConflict(
			DrillTrackContract.TABLE, 
			null, 
			values,
			SQLiteDatabase.CONFLICT_IGNORE
		);
		final Uri retval = (-1L == insertId) ?
			null :
			ContentUris.withAppendedId(uri, 
				values.getAsLong(DrillTrackContract.Column.ID));
		this.getContext().getContentResolver().notifyChange(uri, null);
		return retval;
	}

	@Override
	public boolean onCreate() {
		Log.d(DrillTrackProvider.class.getSimpleName(), "onCreated");
		this.mDatabase = new Database(this.getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
			String[] selectionArgs, String sortOrder) {
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sMatcher.match(uri)) {
		case DrillTrackContract.MATCH_CODE_DIR:
			break;
		case DrillTrackContract.MATCH_CODE_ITEM:
			qb.appendWhere(String.format(
				"%s = %s", DrillTrackContract.Column.ID, uri.getLastPathSegment()));
			break;
		default:
			throw new IllegalArgumentException("bad uri: " + uri);
		}
		final SQLiteDatabase db = this.mDatabase.getReadableDatabase();
		final Cursor c = qb.query(
			db, 
			projection, 
			selection, 
			selectionArgs, 
			null, 
			null, 
			sortOrder
		);
		c.setNotificationUri(this.getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, 
			String[] selectionArgs) {
		final String where = getWhere(uri, selection);
		final SQLiteDatabase db = this.mDatabase.getWritableDatabase();
		final int retval = db.update(
			DrillTrackContract.TABLE, 
			values, 
			where, 
			selectionArgs
		);
		if (0 < retval) {
			this.getContext().getContentResolver().notifyChange(uri, null);
		}
		return retval;
	}
	
	private static String getWhere(Uri uri, String selection) {
		switch (sMatcher.match(uri)) {
		case DrillTrackContract.MATCH_CODE_DIR:
			return (null == selection) ? "1" : selection;
		case DrillTrackContract.MATCH_CODE_ITEM:
			final long id = ContentUris.parseId(uri);
			return String.format(
				"%s = %s %s",
				DrillTrackContract.Column.ID,
				id,
				TextUtils.isEmpty(selection) ?
					"" :
					String.format(
						" and ( %s )",
						selection
					)
			);
		default:
			throw new IllegalArgumentException("bad uri: " + uri);
		}
	}

}
