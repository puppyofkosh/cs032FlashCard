package audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

// taken mostly from :
// http://www.developer.com/java/other/article.php/2105421/Java-Sound-Capturing-Microphone-Data-into-an-Audio-File.htm
public class microphoneTest {
TargetDataLine line;
	AudioFormat format;
	DataLine.Info info;
	
	public microphoneTest() throws LineUnavailableException, InterruptedException {
		format = new AudioFormat(8000.0F, 8, 1, true, true);
		 info = new DataLine.Info(TargetDataLine.class, format); 
		 line = (TargetDataLine) AudioSystem.getLine(info);
		 CaptureThread thread = new CaptureThread();
		 thread.start();
		 Thread.sleep(11000);
		 line.stop();
		 line.close();
	}
	
	public static void main(String[] args) {
		
		try {
			new microphoneTest();
			System.out.println("done");
			
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	private class CaptureThread extends Thread {
		public void run() {
			AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
			File audioFile = new File("testnoise.wav");
			try {
				line.open(format);
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			line.start();
			try {
				AudioSystem.write(new AudioInputStream(line), fileType, audioFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
