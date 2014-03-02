package com.wordpress.tslantz.stringendo;

public interface SoundPlayer {
	
	Track load(String path);
	
	public interface Track {
		
		void loop(int startMSec, int endMSec, double speed);
		
		void pause();
		
		void stop();
		
	}
	
}
