package gui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

public class TagPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<TagBox> _tags;
	
	TagPanel() {
		this(new LinkedList<String>());
	}

	TagPanel(List<String> tags) {
		super(new WrapLayout(WrapLayout.LEADING, 1, 1));
		setOpaque(false);
		_tags = new LinkedList<>();
		for(String tag :  tags) 
			addTag(tag);
	}

	public void addTag(String tag) {
		new TagBox(tag, this);
		update();
	}
	
	private void update() {
		setVisible(!_tags.isEmpty());
		revalidate();
		repaint();
	}
	
	public List<String> getTags() {
		LinkedList<String> strings = new LinkedList<>();
		for(TagBox tag : _tags) {
			strings.add(tag.getText());
		}
		return strings;
	}
	
	public void clear() {
		_tags = new LinkedList<>();
		update();
	}
	
	public void deleteTag(TagBox tag) {
		remove(tag);
		_tags.remove(tag);
		update();
	}
}
