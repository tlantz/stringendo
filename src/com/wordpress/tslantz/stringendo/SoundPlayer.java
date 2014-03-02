package com.wordpress.tslantz.stringendo;

public final class SoundPlayer {
	
	public native String getMyData();
	
	static {
		System.loadLibrary("sndplyr");
	}

	
}
