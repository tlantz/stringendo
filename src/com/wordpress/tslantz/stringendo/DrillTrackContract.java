package com.wordpress.tslantz.stringendo;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines a database schema containing definitions of sections of songs and speed 
 * ranges to run them through.
 */
public final class DrillTrackContract {
	
	/**
	 * Authority for the drill track data provider.
	 */
	public static final String AUTHORITY = "com.wordpress.tslantz.stringendo.DrillTrackProvider";
	
	/**
	 * The drills table is the main list of defined drills.
	 */
	public static final String TABLE = "drill_tracks";

	/**
	 * DrillTrack content provider URI.
	 */
	public static final Uri CONTENT_URI = Uri.parse(String.format(
		"content://%s/%s", AUTHORITY, TABLE));
	
	/**
	 * Name of the drill tracks database file.
	 */
	public static final String DB_NAME = "stringendo.db";
	
	/**
	 * Database schema version.
	 */
	public static final int DB_VERSION = 1;
	
	/**
	 * Match code constant for a list URI.
	 */
	public static final int MATCH_CODE_DIR = 1;
	
	/**
	 * Match code constant for a single fetch URI.
	 */
	public static final int MATCH_CODE_ITEM = 2;
	
	/**
	 * Mime type for a list of drill tracks.
	 */
	public static final String MIME_DIR 
		= "vnd.android.cursor.dir/vnd.com.wordpress.tslantz.stringendo.provider.drilltrack";
	
	/**
	 * Mime type for a single drill track item.
	 */
	public static final String MIME_ITEM
		= "vnd.android.cursor.dir/vnd.com/wordpress.tslantz.stringendo.provider.drilltrack";
	
	/**
	 * Defines columns for the drills table.
	 */
	public static final class Column {
		
		/**
		 * The ID column for the table.
		 */
		public static final String ID = BaseColumns._ID;
		
		/**
		 * The path to the song this track is for.
		 */
		public static final String SONG_PATH = "song_path";
		
		/**
		 * The name of the song.
		 */
		public static final String SONG_NAME = "song_name";
		
		/**
		 * The name of the drill, like "solo1" or "intro riff".
		 */
		public static final String DRILL_NAME = "drill_name";
		
		/**
		 * The beginning of the practice section.
		 */
		public static final String BEGIN_MSEC = "begin_msec";
		
		/**
		 * The end of the practice section.
		 */
		public static final String END_MSEC = "end_msec";
		
		/**
		 * The slowest speed factor (slow end of the range).
		 */
		public static final String SLOW_FACTOR = "slow_factor";
		
		/**
		 * The fastest speed factor. Can be higher than 1.0. 
		 */
		public static final String FAST_FACTOR = "fast_factor";
	}

}
