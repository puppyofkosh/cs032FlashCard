package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.Controller;
import utils.Writer;
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
	ImageToggleButton _playAndStop;
	JComboBox<Integer> _incrementInterval;
	List<JLabel> _tags, _sets;
	private JSpinner _spinner;
	private FlashCard _card;
	private JPanel _panel;
	private TagPanel _tagPanel;

	FlashCardPanel(FlashCard card) {
		setPreferredSize(new Dimension(250, 200));
		_card = card;
		setBackground(GuiConstants.CARD_BACKGROUND);
		setBorder(BorderFactory.createRaisedBevelBorder());

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		_cardName = new JLabel(_card.getName(), SwingConstants.CENTER);
		_cardName.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(_cardName);

		_panel = new JPanel();
		_panel.setOpaque(false);

		JLabel lblInterval = new JLabel("Interval");
		_panel.add(lblInterval);
		_spinner = new JSpinner(new SpinnerNumberModel(_card.getInterval(), 0, 10, 1));
		_panel.add(_spinner);
		_playAndStop = ImageToggleButton.playStopButton();
		_panel.add(_playAndStop);
		_playAndStop.addActionListener(new PlayStopListener(_playAndStop));

		((JSpinner.DefaultEditor) _spinner.getEditor()).getTextField().setColumns(2);
		_spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					_spinner.commitEdit();
					int newValue = (int) _spinner.getValue();
					_card.setInterval(newValue);
				} catch (ParseException | IOException e1) {
					Writer.err("ERROR: Could not write change to card - reverting to original interval");
					_spinner.setValue(_card.getInterval());
				}
			}
		});
		
		_panel.setMaximumSize(_panel.getPreferredSize());
		add(_panel);

		_tagPanel = new TagPanel();
		JScrollPane js = new JScrollPane(_tagPanel);
		js.setOpaque(false);
		js.getViewport().setOpaque(false);
		js.setBorder(BorderFactory.createEmptyBorder());
		add(js);

		populateSets(_card.getSets());
		populateTags(_card.getTags());
		revalidate();
	}

	private void populateSets(Collection<FlashCardSet> sets) {
	}

	private void populateTags(Collection<String> tags) {
		//TODO we probably shouldn't be handling this here.
		boolean hasTag = false;
		for(String tag : tags) {
			if (tag.equalsIgnoreCase("null") || (tag.equals("")))
				continue;
			else {
				_tagPanel.addTag(tag);
				hasTag = true;
			}
		}
	}

	private class PlayStopListener implements ActionListener {
		boolean _play;
		ImageToggleButton _button;

		PlayStopListener(ImageToggleButton button) {
			_button = button;
			_play = false;
		}

		@Override
		public void actionPerformed(ActionEvent e) {			
			_play = !_play;
			try {
				if (_play)
					Controller.playFlashCard(_card);
				else
					Controller.stopAudio();
			} catch (IOException e1) {
				_button.toggle();
				Controller.guiMessage("ERROR: could not play audio", true);
			}
		}
	}
}
