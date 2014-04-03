package audio;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * http://stackoverflow.com/questions/7728850/java-broadcast-voice-over-java-sockets
 * @author samkortchmar
 *
 */
public class AudioFileStub implements AudioFile, Serializable {

	//These classes must be transient because no audio classes are serializable.
	transient AudioInputStream stream;
	transient AudioFormat format;
	byte[] rawBytes;

	public AudioFileStub(AudioInputStream stream) {
		this.stream = stream;
		this.format = getFormat();
		this.rawBytes = getRawBytes();
		if (AudioConstants.TTSREADER != null) {
			AudioConstants.TTSREADER = getFormat();
			System.out.println(String.format("Constructor should be:"
					+ " new AudioFormat(%s, %s, %s, %s, %s, %s, %s",
					format.getEncoding(),
					format.getSampleRate(),
					format.getSampleSizeInBits(),
					format.getChannels(),
					format.getFrameSize(),
					format.getFrameRate(),
					format.isBigEndian()));
		}
	}

	@Override
	public AudioInputStream getStream() {
		return stream;
	}

	@Override
	public byte[] getRawBytes() {
		if (rawBytes != null) {
			return rawBytes;
		} else {
			try {
				ByteArrayOutputStream baout = new ByteArrayOutputStream();
				AudioSystem.write(stream, AudioFileFormat.Type.WAVE, baout);

				baout.close();
				//FIXME - when do we close the audioInputStream? Ever?
				stream.close();
				return baout.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public AudioFormat getFormat() {
		return stream.getFormat();
	}

}
