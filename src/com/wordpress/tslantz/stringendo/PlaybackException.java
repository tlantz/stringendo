package com.wordpress.tslantz.stringendo;

public final class PlaybackException extends Exception {

	public static final long serialVersionUID = 42L;
	
	public PlaybackException(String message, Throwable t) {
		super(message, t);
	}
	
}
