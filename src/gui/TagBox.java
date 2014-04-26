package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TagBox extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	JLabel _tag, _delete;
	int _size, _roundedness;
	TagPanel _tagPanel;
	String _tagText;
	
	TagBox(String s, TagPanel tagPanel) {
		this(s, 15, 10, tagPanel);
	}
	
	TagBox(String s, int size, int roundedness, TagPanel tagPanel) {
		super();
		_tagText = s;
		_size = size;
		_roundedness = roundedness;
		_tagPanel = tagPanel;
		_tag = new JLabel(_tagText);
		_tag.setBorder(BorderFactory.createEmptyBorder(1, 1, 3, 1));
		_tag.setFont(new Font("Sans Serif", Font.PLAIN, _size + 2));
		
		ImageIcon current = new ImageIcon("./res/img/delete x.png");
		Image img = current.getImage() ;  
		Image newimg = img.getScaledInstance(_size, _size,  java.awt.Image.SCALE_SMOOTH ) ;  
		current = new ImageIcon(newimg);
		_delete = new JLabel(current);
		_delete.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		_delete.addMouseListener(this);
		
		add(_tag);
		add(_delete);
		setBackground(Color.GREEN);
		setOpaque(false);
		
		_tagPanel._tags.add(this);
		_tagPanel.add(this);
	}
	
	public String getText() {
		return _tagText;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension arcs = new Dimension(_roundedness, _roundedness);
		int width = getWidth();
		int height = getHeight();
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//Draws the rounded opaque panel with borders.
		graphics.setColor(getBackground());
		graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint background
		graphics.setColor(getForeground());
		graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint border
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		_delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		_delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		_tagPanel.deleteTag(this);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		_delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		_delete.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	}
}