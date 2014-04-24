package gui;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

public class TagPanel extends JPanel {
	
	List<TagBox> _tags;
	
	TagPanel() {
		this(new LinkedList<String>());
	}

	TagPanel(List<String> tags) {
		super(new WrapLayout(WrapLayout.LEADING, 3, 5));
		setBackground(Color.WHITE);
		_tags = new LinkedList<>();
		for(String tag :  tags) 
			addTag(tag);
	}

	public void addTag(String tag) {
		add(new TagBox(tag, _tags));
		revalidate();
		repaint();
	}
}
