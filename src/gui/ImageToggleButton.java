package gui;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class ImageToggleButton extends JButton implements ActionListener{

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
		setMargin(new Insets(0, 0, 0, 0));
		setContentAreaFilled(false);
		setIcon(getImage());
		setText(getString());
	}

	ImageToggleButton(ImageIcon onImage, ImageIcon offImage, String onString, String offString, int size) {
		this(onImage, offImage, onString, offString);
		setSize(size);
	}

	public void setSize(int size) {
		_size = size;
	}

	public ImageIcon getImage() {
		ImageIcon current = _on ? _onImage : _offImage;
		Image img = current.getImage() ;  
		Image newimg = img.getScaledInstance(_size, _size,  java.awt.Image.SCALE_SMOOTH ) ;  
		current = new ImageIcon(newimg);
		return current;
	}
	
	public String getString() {
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
}

