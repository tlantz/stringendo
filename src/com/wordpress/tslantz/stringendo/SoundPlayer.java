package com.wordpress.tslantz.stringendo;

public interface SoundPlayer {
	
	/**
	 * Loads a track from a file with a specified playable range.
	 * 
	 * @param path the file path
	 * @param startMSec the start millisecond of the clip to play
	 * @param endMSec the end millisecond of the clip to play
	 * @return a Track object that can be looped/paused/stopped
	 */
	Track load(String path, int startMSec, int endMSec) throws Exception;
	
	/**
	 * Simple sound clip wrapper that supports looping with a play back speed factor
	 * as well as a gap length between loops.
	 */
	public interface Track {
		
		/**
		 * Destroys a track and all of its resources. 
		 */
		void close();
		
		/**
		 * Gets the state of the track.
		 */
		State getState();
		
		/**
		 * Plays the track with a speed factor and a gap between loops.
		 * @param speed the speed factor (1.0 based).
		 * @param gapMSec the milliseconds between loops.
		 */
		void loop(float speed, int gapMSec);
		
		/**
		 * Pauses the track keeping place on the current loop to be resumed. A resume
		 * can take place with loop again. Changed speeds/gaps on resume is supported.
		 */
		void pause();
		
		/**
		 * Resets the position to the beginning of the loop. A stop would be a pause 
		 * and then a reset.
		 */
		void reset();
		
		/**
		 * Represents possible states for a track.
		 */
		public static enum State {
			/**
			 * The paused state. 
			 */
			PAUSED,
			/**
			 * The playing state.
			 */
			PLAYING
		}	
	}
}
