package com.wordpress.tslantz.stringendo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

final class JLayerSoundPlayer implements SoundPlayer {

	@Override
	public SoundPlayer.Track load(String path,
			int startMSec, int endMSec) throws Exception {
		return new Track(path, startMSec, endMSec);
	}
	
	/**
	 * Simple container to decode an MP3 file given a path.
	 */
	private static final class Track
		implements SoundPlayer.Track {
		
		private final int mEndMSec;
		private final String mPath;
		private final int mStartMSec;
		private TrackThread mThread;
		
		private boolean mIsClosed;
		private State mPlayState = State.PAUSED;
		
		public Track(String path, int startMSec, int endMSec) 
				throws PlaybackException, IOException {
			this.mStartMSec = startMSec;
			this.mEndMSec = endMSec;
			this.mPath = path;
			this.mThread = new TrackThread();
			this.mThread.start();
		}
		
		@Override
		public void close() {
			this.mPlayState = State.PAUSED;
			this.mIsClosed = true;
			try {
				this.mThread.join();
			} catch (InterruptedException e) {
				Log.e("JLayerSoundPlayer", 
					"Playback thread join interuppted: " + e.getMessage());
			}
		}
		
		@Override
		public State getState() {
			return this.mPlayState;
		}

		@Override
		public void loop(float speed, int gapMSec) {
			this.mPlayState = State.PLAYING;
		}

		@Override
		public void pause() {
			this.mPlayState = State.PAUSED;
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}
		
		private final class TrackThread extends Thread {
			
			private final Decoder mDecoder;
			private final FileInputStream mInputStream;
			private final int mMinBufferSize;
			private final AudioTrack mTrack;
			
			private Bitstream mBitstream;
			private short[] mCurrentBuffer;
			private int mCurrentPos;
			private long mFirstFramePosition = -1L;
			private float mLocMSec;
			private short[] mNextBuffer;
			
			public TrackThread() throws IOException {
				super();
				final int sampleRateHz = 44100;
				this.mMinBufferSize = AudioTrack.getMinBufferSize(
					sampleRateHz, 
					AudioFormat.CHANNEL_OUT_STEREO, 
					AudioFormat.ENCODING_PCM_16BIT
				);
				this.mTrack = new AudioTrack(
					AudioManager.STREAM_MUSIC,
					sampleRateHz,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT,
					mMinBufferSize,
					AudioTrack.MODE_STREAM
				);
				final File file = new File(mPath); 
				this.mInputStream = new FileInputStream(file);
				this.mBitstream = new Bitstream(this.mInputStream);
				this.mDecoder = new Decoder();
			}
		
			@Override
			public void run() {
				this.setName("Playback [" + mPath + "]");
				this.mCurrentBuffer = this.readNextFrame();
				this.mNextBuffer = this.readNextFrame();
				this.setPriority(Thread.MAX_PRIORITY);
				while (!mIsClosed) {
					if (Track.State.PLAYING == mPlayState) {
						if (AudioTrack.PLAYSTATE_PLAYING != mTrack.getPlayState()) {
							mTrack.play();
						}
						final int size = this.mCurrentBuffer.length - this.mCurrentPos;
						final int written = this.mTrack.write(
							this.mCurrentBuffer, 
							this.mCurrentPos,
							size
						);
						this.mCurrentPos += size;
						if (0 == (this.mCurrentPos - written)) {
							this.mCurrentBuffer = this.mNextBuffer;
							this.mNextBuffer = this.readNextFrame();
						}
					} else if (Track.State.PAUSED == mPlayState) {
						if (AudioTrack.PLAYSTATE_PLAYING == mTrack.getPlayState()) {
							mTrack.pause();
						}
						Thread.yield();
					}
				}
				this.mTrack.stop();
				this.mTrack.release();
				try {
					this.mBitstream.close();
				} catch (BitstreamException e) {
					Log.e("JLayerSoundPlayer",
						"error closing bit stream: " + e.getMessage());
				}
				try {
					this.mInputStream.close();
				} catch (IOException e) {
					Log.e("JLayerSoundPlayer",
						"error closing input stream: " + e.getMessage());
				}
			}
			
			private short[] readNextFrame() {
				Header frame;
				try {
					frame = this.seekFrame();
				} catch (BitstreamException e) {
					Log.e("JLayerSoundPlayer",
						"JLayer error reading frame: " + e.getMessage());
					return null;
				} catch (IOException e) {
					Log.e("JLayerSoundPlayer",
						"input error reading frame: " + e.getMessage());
					return null;
				}
				try {
					final SampleBuffer sb = (SampleBuffer)this.mDecoder
						.decodeFrame(frame, this.mBitstream);
					final short[] frameData = sb.getBuffer().clone();
					this.mBitstream.closeFrame();
					return frameData;
				} catch (DecoderException e) {
					Log.e("JLayerSoundPlayer",
						"failed to decode frame: " + e.getMessage());
					return null;
				}
			}
			
			private Header seekFrame() throws BitstreamException, IOException {
				if (null == this.mBitstream || this.mLocMSec > mEndMSec) {
					// we're already past the section, need to rewind the bit stream and
					// start over
					final Bitstream old = this.mBitstream;
					if (0 <= this.mFirstFramePosition) {
						this.mInputStream.getChannel().position(
							this.mFirstFramePosition);
					}
					this.mBitstream = new Bitstream(this.mInputStream);
					if (null != old) {
						old.close();
					}
				}
				Header frame;
				while (true) {
					frame = this.mBitstream.readFrame();
					this.mLocMSec += frame.ms_per_frame();
					if (mStartMSec > this.mLocMSec) {
						break;
					} else {
						if (0 > this.mFirstFramePosition) {
							this.mFirstFramePosition 
								= this.mInputStream.getChannel().position();
						}
						this.mBitstream.closeFrame();
					}
				}
				return frame;
			}
		}
	}
}
