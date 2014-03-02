package com.wordpress.tslantz.stringendo;

final class JLayerSoundPlayer implements SoundPlayer {

	@Override
	public com.wordpress.tslantz.stringendo.SoundPlayer.Track load(String path,
			int startMSec, int endMSec) {
		return new Track(path, startMSec, endMSec);
	}
	
	/**
	 * Simple container to decode an MP3 file given a path.
	 */
	private static final class Track implements SoundPlayer.Track {
		
		private final int endMSec;
		private final String path;
		private final int startMSec;
		
		public Track(String path, int startMSec, int endMSec) {
			this.path = path;
			this.startMSec = startMSec;
			this.endMSec = endMSec;
		}

		@Override
		public void loop(float speed, int gapMSec) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}
		
		
	}
}
