package gui;

import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * This class is used by the GUI to represent flashCards, as
 * well as in the FlashBoard.																	
 * @author samkortchmar
 */

public class FlashCardPanel extends JPanel {
	private static final long serialVersionUID = 2666411116428918076L;
	JLabel cardName;
	JButton PlayAndStopQ, PlayAndStopA;
	JComboBox<Integer> incrementInterval;
	List<JLabel> tags;
	List<JLabel> sets;
	
	FlashCardPanel(FlashCard f) {
		cardName = new JLabel(f.getName());
		JPanel JPanel = new JPanel();
		populateSets(f.getSets());
		populateTags(f.getTags());
		//Etc, etc/
	}
	
	private void populateSets(Collection<FlashCardSet> sets) {
	}
	
	private void populateTags(Collection<String> tags) {		
	}
	
	
}
