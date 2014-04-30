package gui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import audio.AudioFile;
import audio.DiscAudioFile;

public class WaveForm extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private int[] data;
	
	public WaveForm(AudioFile file) throws IOException {
		super();
		this.data = byteToInt(file.getRawBytes());
	}
	
	public WaveForm() {
		super();
	}
	
	private static int[] byteToInt(byte[] bytes) {
		int[] output = new int[bytes.length/2];
		ByteBuffer b = ByteBuffer.allocate(2);
		b.order(ByteOrder.LITTLE_ENDIAN);
		int j = 0;
		for (int i = 0; i < bytes.length; i++) {
			b.put(bytes[i]);
			i++;
			b.put(bytes[i]);
			System.out.println(b.position());
			output[j] = b.getShort(0);
			j++;
			b.clear();
			System.out.println("line");
		}
		
		return output;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (data == null)
			return;
		synchronized (this) {
			double xScale = ((double) this.getWidth()) / ((double) data.length);
			double yScale = (double) this.getHeight() / 65536;
			for (int i = 0; i < data.length - 1; i++) {
				g.drawLine((int) (i * xScale),
						(int) ((data[i] + 32768) * yScale),
						(int) ((i + 1) * xScale),
						(int) ((data[i + 1] + 32768) * yScale));
			}
		}
	}
	
	public void changeAudio(AudioFile newFile) throws IOException {
		synchronized (this) {
			this.data = byteToInt(newFile.getRawBytes());
		}
		repaint();
	}
	
	public void clearAudio() {
		synchronized (this) {
			data = null;
		}
			repaint();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		final JFrame frame = new JFrame("test");
		frame.setBounds(100, 100, 1000, 500);
		final WaveForm test = new WaveForm(new DiscAudioFile("acronym.wav"));
		JPanel all = new JPanel();
		all.setLayout(new BoxLayout(all, BoxLayout.Y_AXIS));
		frame.add(all);
		JPanel panel = new JPanel();
		all.add(panel);
		all.add(test);
		
		JButton clear = new JButton("Clear");
		panel.add(clear);
		clear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				test.clearAudio();
				System.out.println("clear");
			}
			
		});
		
		JButton a = new JButton("Audio A");
		panel.add(a);
		a.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					test.changeAudio(new DiscAudioFile("files/a.wav"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		JButton b = new JButton("Audio B");
		panel.add(b);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					test.changeAudio(new DiscAudioFile("files/q.wav"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		frame.setVisible(true);	
	}
	
}
