package audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Audio "file" represented as a file on disk
 * 
 * TODO: Turn this into a usable Stream somehow
 * 
 * @author puppyofkosh
 *
 */
public class DiskAudioFile implements AudioFile{

	
	private Path path;
	private byte[] fileData;
	
	public boolean good()
	{
		return fileData != null;
	}
	
	public byte[] getData()
	{
		return fileData;
	}
	
	public Path getPath()
	{
		return path;
	}
	
	public DiskAudioFile(Path path)
	{
		this.path = path;
		try {
			fileData = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO: Turn the file into a stream we can use for concatenation-probably involves turning
	 * fileData into an Audio-type object, then getting a "Stream" from that.
	 */
	@Override
	public AudioInputStream getStream() {
		return null;
	}

	@Override
	public byte[] getRawBytes() {
		return fileData;
	}

	@Override
	public AudioFormat getFormat() {
		// TODO Auto-generated method stub
		return null;
	}


}
