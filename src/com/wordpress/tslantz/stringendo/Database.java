package com.wordpress.tslantz.stringendo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite database access for data stored by stringendo.
 */
public final class Database extends SQLiteOpenHelper {
	
	/**
	 * Initializes a new Database object for stringendo.
	 * @param context the application context
	 */
	public Database(Context context) {
		super(context, DrillTrackContract.DB_NAME, null, DrillTrackContract.DB_VERSION);
	}

	/**
	 * Creates the drill tracks table.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		final StringBuilder ddl = new StringBuilder();
		ddl.append("create table ").append(DrillTrackContract.TABLE).append(" (")
			.append(DrillTrackContract.Column.ID).append(" int primary key, ")
			.append(DrillTrackContract.Column.SONG_PATH).append(" text, ")
			.append(DrillTrackContract.Column.BEGIN_MSEC).append(" int, ")
			.append(DrillTrackContract.Column.END_MSEC).append(" int, ")
			.append(DrillTrackContract.Column.SLOW_FACTOR).append(" real, ")
			.append(DrillTrackContract.Column.FAST_FACTOR).append(" real)");
		Log.d(Database.class.getSimpleName(), 
			String.format("creating %s table",  DrillTrackContract.TABLE));
		database.execSQL(ddl.toString());
	}

	/**
	 * Handles a schema upgrade/downgrade between versions of the application.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int fromVersion, int toVersion) {
		// for now we won't do any schema migrations, just dropping the table
		database.execSQL(String.format("drop table if exists %s", 
			DrillTrackContract.TABLE));
	}

}
