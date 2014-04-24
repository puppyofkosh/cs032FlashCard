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
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TagPanel extends JPanel {
	
	List<TagBox> _tags;
	
	TagPanel(List<String> tags) {
		_tags = new LinkedList<>();
		for(String tag :  tags) 
			addTag(tag);
	}
	
	public void addTag(String tag) {
		add(new TagBox(tag, _tags));
	}
}
