package gui;

import gui.IconFactory.IconType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Writer;

public class TagLabel extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	JLabel _tag, _delete;
	int _size, _roundedness;
	TagPanel _tagPanel;
	String _tagText;
	boolean _global;
	boolean _deletable;

	TagLabel(String s, TagPanel tagPanel) {
		this(s, tagPanel, false);
	}

	TagLabel(String s, TagPanel tagPanel, boolean global) {
		this(s, tagPanel, global, true);
	}

	TagLabel(String s, TagPanel tagPanel, boolean global, boolean deletable) {
		this(s, GuiConstants.DEFAULT_BUTTON_SIZE, GuiConstants.DEFAULT_TAG_LABEL_ROUNDEDNESS, tagPanel, global, deletable);
	}

	TagLabel(String s, int size, int roundedness, TagPanel tagPanel, boolean global, boolean deletable) {
		super();
		_tagText = s;
		_size = size;
		_roundedness = roundedness;
		_tagPanel = tagPanel;
		_global = global;
		_deletable = deletable;
		_tag = new JLabel(Writer.shortenText(_tagText));
		_tag.setBorder(BorderFactory.createEmptyBorder(1, 1, 3, 1));
		_tag.setFont(new Font("Sans Serif", Font.PLAIN, _size + 2));
		_tag.setForeground(GuiConstants.PRIMARY_FONT_COLOR);

		if (_deletable) {
			ImageIcon current = IconFactory.loadIcon(IconType.DELETE, _size, true);
			_delete = new JLabel(current);
			_delete.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			_delete.addMouseListener(this);
			add(_delete);
		}
		add(_tag);
		setBackground(global ? GuiConstants.SET_TAG_COLOR : GuiConstants.CARD_TAG_COLOR);
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
	public void addMouseListener(MouseListener ml) {
		_tag.addMouseListener(ml);
		if (_deletable)
			_delete.addMouseListener(ml);
		_tagPanel.addMouseListener(ml);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (_deletable)
			_delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (_deletable)
			_delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (_deletable)
			_tagPanel.deleteTag(this);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (_deletable)
			_delete.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (_deletable)
			_delete.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	}
}