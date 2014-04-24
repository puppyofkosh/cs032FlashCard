package gui;

import java.util.LinkedList;
import java.util.List;

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
		revalidate();
		repaint();
	}
}
