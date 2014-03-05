package com.wordpress.tslantz.stringendo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

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
	private static final class Track extends Thread
		implements SoundPlayer.Track {
		
		private final int bufferSize;
		private final int endMSec;
		private final File file;
		private final byte[] pcmBuffer;
		private final int startMSec;
		private final AudioTrack track;
		
		private PlayState playState;
		private int position;
		
		public Track(String path, int startMSec, int endMSec) 
				throws PlaybackException, IOException {
			this.startMSec = startMSec;
			this.endMSec = endMSec;
			this.file = new File(path);
			this.pcmBuffer = this.readToPCM();
			final int sampleRateHz = 44100;
			bufferSize = AudioTrack.getMinBufferSize(
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
		
		private static enum PlayState {
			PAUSED,
			PLAYING,
			DISPOSED
		}
			
		@Override
		public void run() {
			this.setPriority(Thread.MAX_PRIORITY);
			track.play();
			
			while (PlayState.DISPOSED != playState) {
				if (PlayState.PLAYING == playState) {
					final int sizeLeft = (pcmBuffer.length - position);
					if (0 == sizeLeft) {
						playState = PlayState.PAUSED;
					}
					final int writeSize = sizeLeft >= bufferSize ?
						bufferSize : sizeLeft;
					track.write(
						pcmBuffer,
						position,
						writeSize
					);
					position += bufferSize;
				}
			}
		}

		@Override
		public void loop(float speed, int gapMSec) {
			this.playState = PlayState.PLAYING;
		}

		@Override
		public void pause() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
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
			final InputStream is = new BufferedInputStream(
				new FileInputStream(this.file),
				1024 << 4
			);
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
							locMSec += frame.ms_per_frame();
							if (this.startMSec > locMSec) {
								continue;
							} else {
								try {
									final SampleBuffer sb = (SampleBuffer)dec
										.decodeFrame(frame, bs);
									final short[] codes = sb.getBuffer();
									for (final short code : codes) {
										os.write(0xff & code); // first half of read
										os.write(0xff & (code >> 8)); // second half
									}
								} catch (DecoderException e) {
									throw new PlaybackException(
										"Decoding failed: " + e, e);
								}
								if (this.endMSec < locMSec) {
									break;
								}
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
