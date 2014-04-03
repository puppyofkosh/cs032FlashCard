package audio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A simple GUI for testing microphone recording and related features
 * @author Peter
 *
 */
public class BasicMicrophoneGUI extends JFrame {

	final JButton recordButton;
	final JButton stopButton;
	
	final JButton playAudioButton;
	final JButton stopAudioButton;
	
	final JButton createFile;
	final JTextField fileName;
	
	final BasicAudioPlayer player;
	
	Recorder recorder;
	AudioFile recordedAudio;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BasicMicrophoneGUI() {
		super("Basic Microphone Test");
		setBounds(500, 500, 250, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		JPanel panel = new JPanel(null);
		
		add(panel);
		
		recordButton = new JButton("Record");
		panel.add(recordButton);
		recordButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				player.stop();
				playAudioButton.setEnabled(false);
				recordButton.setEnabled(false);
				stopButton.setEnabled(true);
				createFile.setEnabled(false);
				recorder = new MemoryRecorder();
				recorder.startRecord();
			}
			
		});
		recordButton.setBounds(10, 10, 100, 50);
		
		stopButton = new JButton("Stop");
		panel.add(stopButton);
		stopButton.addActionListener(new ButActList());
		stopButton.setBounds(120, 10, 100, 50);
		stopButton.setEnabled(false);
		
		player = new BasicAudioPlayer();
		
		playAudioButton = new JButton("Play Audio");
		panel.add(playAudioButton);
		playAudioButton.setBounds(10, 70, 100, 50);
		playAudioButton.addActionListener(new AudPlayList());
		playAudioButton.setEnabled(false);
		
		stopAudioButton = new JButton("Stop Audio");
		panel.add(stopAudioButton);
		stopAudioButton.setBounds(120, 70, 100, 50);
		stopAudioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				player.stop();
				stopAudioButton.setEnabled(false);
				playAudioButton.setEnabled(true);
				recordButton.setEnabled(true);
			}
			
		});
		stopAudioButton.setEnabled(false);
		
		JLabel label = new JLabel("File name:");
		panel.add(label);
		label.setBounds(10, 130, 100, 20);
		
		fileName = new JTextField();
		panel.add(fileName);
		fileName.setBounds(10, 160, 100, 30);
		fileName.setEditable(true);
		
		createFile = new JButton("Create File");
		panel.add(createFile);
		createFile.addActionListener(new FileWriteListener());
		createFile.setEnabled(false);
		createFile.setBounds(120, 140, 100, 50);
		
	}

	public static void main(String[] args) {
		new BasicMicrophoneGUI();
	}
	
	private class ButActList implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			playAudioButton.setEnabled(true);
			stopButton.setEnabled(false);
			recordButton.setEnabled(true);
			recordedAudio = recorder.stopRecord();
			createFile.setEnabled(true);
		}
		
	}
	
	private class AudPlayList implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			playAudioButton.setEnabled(false);
			stopAudioButton.setEnabled(true);
			recordButton.setEnabled(false);
			try {
				player.play(recordedAudio);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new ClipThread().start();
		}
		
	}
	
	private class FileWriteListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			try {
				
				File output = new File(fileName.getText() + ".wav");
				int nameOverlapPreventer = 0;
				while (output.exists()) {
					output = new File(fileName.getText() + ++nameOverlapPreventer + ".wav");
				}
				
				AudioSystem.write(recordedAudio.getStream(), AudioFileFormat.Type.WAVE, output);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class ClipThread extends Thread {
		@Override
		public void run() {
			while (player.isPlaying()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			playAudioButton.setEnabled(true);
			stopAudioButton.setEnabled(false);
			recordButton.setEnabled(true);
		}
	}
	
}
