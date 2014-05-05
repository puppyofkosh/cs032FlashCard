package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import controller.Controller;
import flashcard.FlashCard;

/**
 * This class is used by the GUI to represent flashCards, as
 * well as in the FlashBoard.																	
 * @author samkortchmar
 */

public class FlashCardPanel extends JPanel {
	private static final long serialVersionUID = 2666411116428918076L;
	private FlashCard _card;
	private JPanel _headerPanel;
	JLabel _cardName;
	ImageToggleButton _playAndStop;
	private TagPanel _tagPanel;

	/**
	 * This method is called so we can reuse FlashCardPanel.
	 * @param card
	 */
	public void reinitialize(FlashCard card) {
		_card = card;
		_cardName.setText(card.getName());
		_tagPanel.reinitialize(card);
		revalidate();
	}

	/**
	 * Constructs a FlashCardPanel from a given card.
	 * @param card
	 */
	public FlashCardPanel(FlashCard card) {

		String defaultName = card.getName();
		setPreferredSize(new Dimension(225, 150));
		_card = card;
		setBackground(GuiConstants.CARD_BACKGROUND);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, GuiConstants.PRIMARY_FONT_COLOR));

		//The header will contain the given card's name and a delete button.
		_headerPanel = new JPanel();
		_headerPanel.setOpaque(false);
		_headerPanel.setBackground(GuiConstants.CARD_BACKGROUND);
		_headerPanel.setLayout(new BoxLayout(_headerPanel, BoxLayout.X_AXIS));
		_headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		_cardName = new JLabel(defaultName, SwingConstants.CENTER);
		_headerPanel.add(_cardName);
		add(_headerPanel);

		//This panel displays the interval as well as a play button which plays
		//the card as you might hear it after you export it, including the
		//assigned interval between the question and answer.

		_playAndStop = ImageToggleButton.playStopButton();
		_playAndStop.addActionListener(new PlayStopListener(_playAndStop));
		_headerPanel.add(_playAndStop);

		//This panel maintains all information about the tags and displays them
		//as bubbles in a scroll window. When you mouse over it, it will display
		//a text field where you can add more tags.
		_tagPanel = new TagPanel(_card);

		//A ControlScrollPane allows us to responsively scroll through cards as
		//well as tags.
		JScrollPane js = new ControlScrollPane(_tagPanel);
		js.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
		js.setOpaque(false);
		js.getViewport().setOpaque(false);
		js.setBorder(BorderFactory.createEmptyBorder());
		js.setViewportBorder(null);
		add(js);
		addMouseListener(createRightClickMenu());
		_tagPanel.addMouseListener(createRightClickMenu());
		revalidate();
	}

	private PopupListener createRightClickMenu() {
		JMenuItem edit = new JMenuItem("Edit Card");
		edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.editCard(_card);
			}
		});
		JMenuItem delete = new JMenuItem("Delete Card");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.deleteCard(_card);
			}
		});
		return new PopupListener(edit, delete);
	}


	/**
	 * A special action listener for play/pause buttons.
	 * @author samkortchmar
	 *
	 */
	private class PlayStopListener implements ActionListener {
		ImageToggleButton _button;

		PlayStopListener(ImageToggleButton button) {
			_button = button;
		}

		@Override
		public void actionPerformed(ActionEvent e) {			
			try {
				if (_button.isOn())
					Controller.playFlashcardThenRun(_card, _button);
				else
					Controller.stopAudio();
			} catch (IOException e1) {
				_button.toggle();
				Controller.guiMessage("ERROR: could not play audio", true);
			}
		}
	}
}
