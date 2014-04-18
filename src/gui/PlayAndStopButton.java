package gui;

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class PlayAndStopButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	boolean _playing = false;
	private String _path = "./res/img/Play Button.png";
	
	PlayAndStopButton() {
		ImageIcon i = new ImageIcon(_path);
		setIcon(i);
		setBorderPainted(false);
	    setBorder(null);
	    setFocusable(false);
	    setMargin(new Insets(0, 0, 0, 0));
	    setContentAreaFilled(false);
	    setRolloverIcon((i));
	    setPressedIcon(i);
	    setDisabledIcon(i);
	}
	
	public void toggle() {
		if (_playing)
			stop();
		else
			play();
		_playing = !_playing;
	}
	
	private void play() {
		
	}
	
	private void stop() {
		
	}
}
