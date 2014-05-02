package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;

public class ImageButton extends JButton {

	private static final long serialVersionUID = 1L;

	ImageButton() {
		this(null, null);
	}

	ImageButton(String text, Icon icon) {		
		super(text, icon);
		setForeground(Color.WHITE);
		setMargin(new Insets(0, 0, 0, 0));		
		setContentAreaFilled(false);		
		setBorderPainted(false);
		setBorder(null);
		setFocusable(false);
	}

	ImageButton(String text) {
		this(text, null);
	}

	ImageButton(Icon icon) {
		this(null, icon);
	}	
}