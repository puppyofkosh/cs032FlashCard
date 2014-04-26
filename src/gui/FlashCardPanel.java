package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.Writer;
import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * This class is used by the GUI to represent flashCards, as
 * well as in the FlashBoard.																	
 * @author samkortchmar
 */

public class FlashCardPanel extends JPanel {
	private static final long serialVersionUID = 2666411116428918076L;
	JLabel _cardName;
	JButton _playAndStop;
	JComboBox<Integer> _incrementInterval;
	List<JLabel> _tags, _sets;
	private JSpinner _spinner;
	private FlashCard _card;
	private JPanel _panel;
	private TagPanel _tagPanel;

	FlashCardPanel(FlashCard card) {
		_card = card;
		setBackground(GuiConstants.CARD_BACKGROUND);
		Border border = new CompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(0, 20, 0, 20));
		setBorder(border);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		//JLabel Displaying the Card's Name

		_panel = new JPanel();
		_panel.setOpaque(false);
		add(_panel);
		
		_cardName = new JLabel(_card.getName());
		_panel.add(_cardName);


		JLabel lblInterval = new JLabel("Interval");
		_panel.add(lblInterval);
		_spinner = new JSpinner(new SpinnerNumberModel(_card.getInterval(), 0, 10, 1));
		_panel.add(_spinner);
		_playAndStop = ImageToggleButton.playStopButton("A: ", "A: ");
		_panel.add(_playAndStop);
		_playAndStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Controller.playAudio(_card.getAnswerAudio());
			}
		});
		((JSpinner.DefaultEditor)_spinner.getEditor()).getTextField().setColumns(2);
		_spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = (int) _spinner.getValue();
				try {
					_card.setInterval(newValue);
				} catch (IOException e1) {
					Writer.err("ERROR: Could not write change to card - reverting to original interval");
					_spinner.setValue(_card.getInterval());
				}
			}
		});

		_tagPanel = new TagPanel();
		add(_tagPanel);

		populateSets(_card.getSets());
		populateTags(_card.getTags());
		revalidate();
		//Etc, etc/
	}

	private void populateSets(Collection<FlashCardSet> sets) {
	}

	private void populateTags(Collection<String> tags) {
		//TODO we shouldn't be handling this here.
		boolean hasTag = false;
		for(String tag : tags) {
			if (tag.equalsIgnoreCase("null") || (tag.equals("")))
				continue;
			else {
			_tagPanel.addTag(tag);
			hasTag = true;
			}
		}
		if (!hasTag)
			_tagPanel.setVisible(false);
	}
}
