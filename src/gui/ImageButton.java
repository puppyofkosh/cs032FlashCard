package gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;

public class ImageButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;

	ImageButton() {
		this(null, null);
	}

	ImageButton(String text, Icon icon) {		
		super(text, icon);
		setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		setMargin(new Insets(0, 0, 0, 0));		
		setContentAreaFilled(false);		
		setBorderPainted(false);
		setBorder(null);
		setFocusable(false);
		addMouseListener(this);
	}

	ImageButton(String text) {
		this(text, null);
	}

	ImageButton(Icon icon) {
		this(null, icon);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Color parentColor = getParent().getBackground();
		setBackground(parentColor.darker());
		setOpaque(true);
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setOpaque(false);
		repaint();
	}	
}