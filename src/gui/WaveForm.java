package gui;

import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import audio.AudioFile;
import audio.DiscAudioFile;

public class WaveForm extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] bytes;
	
	public WaveForm(AudioFile file) throws IOException {
		super();
		this.bytes = file.getRawBytes();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		double xScale = ((double) this.getWidth()) / ((double) bytes.length);
		double yScale = (double) this.getHeight() / 260;
		for (int i = 0; i < bytes.length - 1; i++) {
			g.drawLine((int) (i * xScale),
					(int) ((bytes[i] + 130) * yScale),
					(int) ((i + 1) * xScale),
					(int) ((bytes[i + 1] + 130) * yScale));
		}
	}
	
	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame("test");
		frame.setBounds(100, 100, 1000, 500);
		WaveForm test = new WaveForm(new DiscAudioFile("acronym.wav"));
		frame.add(test);
		frame.setVisible(true);	
	}
	
}
