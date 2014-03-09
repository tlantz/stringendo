package com.wordpress.tslantz.stringendo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
		
		private final int endMSec;
		private final String path;
		private final int startMSec;
		private TrackThread thread;
		
		private boolean isClosed;
		private State playState = State.PAUSED;
		private int position;
		
		public Track(String path, int startMSec, int endMSec) 
				throws PlaybackException, IOException {
			this.startMSec = startMSec;
			this.endMSec = endMSec;
			this.path = path;
			this.thread = new TrackThread();
			this.thread.start();
		}
		
		@Override
		public void close() {
			this.playState = State.PAUSED;
			this.isClosed = true;
			try {
				this.thread.join();
			} catch (InterruptedException e) {
				Log.e("JLayerSoundPlayer", 
					"Playback thread join interuppted: " + e.getMessage());
			}
		}
		
		@Override
		public State getState() {
			return this.playState;
		}

		@Override
		public void loop(float speed, int gapMSec) {
			this.playState = State.PLAYING;
		}

		@Override
		public void pause() {
			this.playState = State.PAUSED;
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}
		
		private final class TrackThread extends Thread {
			
			private final int bufferSize;
			private final AudioTrack track;
			
			private byte[] pcmBuffer;
			
			public TrackThread() {
				super();
				final int sampleRateHz = 44100;
				this.bufferSize = AudioTrack.getMinBufferSize(
					sampleRateHz, 
					AudioFormat.CHANNEL_OUT_STEREO, 
					AudioFormat.ENCODING_PCM_16BIT
				);
				this.track = new AudioTrack(
					AudioManager.STREAM_MUSIC,
					sampleRateHz,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT,
					bufferSize,
					AudioTrack.MODE_STREAM
				);
			}
		
			@Override
			public void run() {
				this.setName("Playback [" + path + "]");
				boolean isOkay = true;
				try {
					this.pcmBuffer = this.readToPCM();
				} catch (PlaybackException e) {
					Log.e("JLayerSoundPlayer",
						"loading PCM data failed: " + e.getMessage());
					isOkay = false;
				} catch (IOException e) {
					Log.e("JLayerSoundPlayer",
						"IO exception while loading PCM: " + e.getMessage());
					isOkay = false;
				}
				if (isOkay) {
					this.setPriority(Thread.MAX_PRIORITY);
					while (!isClosed) {
						if (Track.State.PLAYING == playState) {
							if (AudioTrack.PLAYSTATE_PLAYING != track.getPlayState()) {
								track.play();
							}
							final int sizeLeft = (pcmBuffer.length - position);
							if (0 == sizeLeft) {
								position = 0;
							}
							final int writeSize = sizeLeft >= bufferSize ?
								bufferSize : sizeLeft;
							final int written = track.write(
								pcmBuffer,
								position,
								writeSize
							);
							position += written;
						} else if (Track.State.PAUSED == playState) {
							if (AudioTrack.PLAYSTATE_PLAYING == track.getPlayState()) {
								track.pause();
							}
							Thread.yield();
						}
					}
				}
				track.stop();
				track.release();
			}
			
			/** 
			 * Uses the JLayer decoder to read the range required from MP3 into a PCM
			 * buffer.
			 * 
			 * Based on example from: 
			 * 
			 *   http://mindtherobot.com/blog/624/android-audio-play-an-mp3-file-on-an-audiotrack/
			 * @throws IOException 
			 */
			private byte[] readToPCM() throws PlaybackException, IOException {
				final File file = new File(path); 
				final InputStream is = new FileInputStream(file);
				final ByteArrayOutputStream os = new ByteArrayOutputStream(1024 << 4);
				try {
					final Bitstream bs = new Bitstream(is);
					final Decoder dec = new Decoder();
					float locMSec = 0.0f;
					while (true) {
						try {
							final Header frame = bs.readFrame();
							if (null == frame) {
								break;
							} else {
								try {
									locMSec += frame.ms_per_frame();
									if (startMSec > locMSec) {
										continue;
									} else {
										try {
											final SampleBuffer sb = (SampleBuffer)dec
												.decodeFrame(frame, bs);
											final short[] codes = sb.getBuffer();
											for (final short code : codes) {
												// first half of read
												os.write(0xff & code);
												// second half
												os.write(0xff & (code >> 8));
											}
										} catch (DecoderException e) {
											throw new PlaybackException(
												"Decoding failed: " + e, e);
										}
										if (endMSec < locMSec) {
											break;
										}
									}
								} finally {
									bs.closeFrame();
								}
							}
						} catch (BitstreamException e) {
							throw new PlaybackException(String.format(
								"BitstreamException: %s", e), e);
						}
					}
					return os.toByteArray();
				} finally {
					is.close();
				}
			}
		}
	}
}
