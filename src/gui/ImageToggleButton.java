package gui;

import gui.IconFactory.IconType;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

/**
 * Image button with two states
 * @author samkortchmar
 *
 */
public class ImageToggleButton extends ImageButton implements ActionListener, Runnable {

	private static final long serialVersionUID = -3150533535670093580L;
	ImageIcon _onImage, _offImage;
	String _onString, _offString;
	boolean _on;
	int _size = 32;

	ImageToggleButton(ImageIcon onImage, ImageIcon offImage, String onString, String offString) {
		super();
		_onImage = onImage;
		_offImage = offImage;
		_onString = onString;
		_offString = offString;
		_on = true;
		addActionListener(this);
		setHorizontalAlignment(SwingConstants.LEFT);
		setVerticalAlignment(SwingConstants.CENTER);
		setIcon(getImage());
		setText(getString());
	}

	ImageToggleButton(ImageIcon onImage, ImageIcon offImage, String onString, String offString, int size) {
		this(onImage, offImage, onString, offString);
		setSize(size);
	}

	/**
	 * Convenience method for retrieving a Play/Stop button - the most common
	 * use of this class.
	 * @param playText
	 * @param stopText
	 * @return
	 */
	public static ImageToggleButton playStopButton(String playText, String stopText) {
		return new ImageToggleButton(IconFactory.loadIcon(IconType.PLAY, true),
				IconFactory.loadIcon(IconType.STOP, true), playText, stopText);
	}

	public static ImageToggleButton playStopButton() {
		return playStopButton("","");
	}

	public void setSize(int size) {
		_size = size;
	}

	private ImageIcon getImage() {
		ImageIcon current = _on ? _onImage : _offImage;
		Image img = current.getImage() ;  
		Image newimg = img.getScaledInstance(_size, _size,  java.awt.Image.SCALE_SMOOTH ) ;  
		current = new ImageIcon(newimg);
		return current;
	}

	private String getString() {
		return _on ? _onString : _offString;
	}

	public void toggle() {
		_on = !_on;
		setIcon(getImage());
		setText(getString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this)
			toggle();
	}

	public boolean isOn() {
		return _on;
	}
	
	@Override
	public void run() {
		if (!isOn())
			toggle();
	}
}

