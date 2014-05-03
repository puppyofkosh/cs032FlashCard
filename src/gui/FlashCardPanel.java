package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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
	private FlashCard _card;

	private JPanel _headerPanel;
	JLabel _cardName;
	private JLabel _delete;

	private JPanel _interactionPanel;
	ImageToggleButton _playAndStop;
	private JSpinner _spinner;
	private TagPanel _tagPanel;


	public void reinitialize(FlashCard card)
	{
		_card = card;
		_cardName.setText(card.getName());
		_spinner.setValue(card.getInterval());
		populateSets(card.getSets());
		_tagPanel.reinitialize(card);

		revalidate();
	}
	

	/**
	 * Constructs a FlashCardPanel from a given card.
	 * @param card
	 */
	public FlashCardPanel(FlashCard card) {
		String defaultName = card.getName();
		int defaultInterval = card.getInterval();
		Collection<FlashCardSet> defaultSets = card.getSets();
		
		setPreferredSize(new Dimension(225, 150));
		_card = card;
		setBackground(GuiConstants.CARD_BACKGROUND);
		setBorder(BorderFactory.createMatteBorder(0, 0, 2, 2, Color.WHITE));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


		//The header will contain the given card's name and a delete button.
		_headerPanel = new JPanel();
		_headerPanel.setOpaque(false);
		_headerPanel.setLayout(new BorderLayout(0, 0));
		_headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		//Add the card's name to the header panel.
		_cardName = new JLabel(defaultName, SwingConstants.CENTER);
		_cardName.setPreferredSize(new Dimension(255, _cardName.getMaximumSize().height));
		_headerPanel.add(_cardName, BorderLayout.CENTER);
		_cardName.setAlignmentX(Component.CENTER_ALIGNMENT);

		//Initialize the delete card button.
		ImageIcon current = new ImageIcon("./res/img/delete x.png");
		Image img = current.getImage() ;  
		Image newimg = img.getScaledInstance(GuiConstants.DEFAULT_BUTTON_SIZE, 
				GuiConstants.DEFAULT_BUTTON_SIZE, java.awt.Image.SCALE_SMOOTH);  
		current = new ImageIcon(newimg);
		_delete = new JLabel(current);
		_delete.setHorizontalAlignment(SwingConstants.TRAILING);
		_delete.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		//This mouse listener adds functionality to the X that appears next to.
		//the Card's name.
		_delete.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				_delete.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				_delete.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("Deleting " + _card);
				Controller.deleteCard(_card);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				_delete.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			}
			@Override
			public void mouseExited(MouseEvent e) {		
				_delete.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			}
		});

		_headerPanel.add(_delete, BorderLayout.EAST);
		_headerPanel.setMaximumSize(_headerPanel.getPreferredSize());
		add(_headerPanel);

		//This panel displays the interval as well as a play button which plays
		//the card as you might hear it after you export it, including the
		//assigned interval between the question and answer.
		_interactionPanel = new JPanel();
		_interactionPanel.setOpaque(false);

		JLabel lblInterval = new JLabel("Interval");
		_interactionPanel.add(lblInterval);
		_spinner = new JSpinner(new SpinnerNumberModel(defaultInterval, 0, 10, 1));
		_interactionPanel.add(_spinner);
		_playAndStop = ImageToggleButton.playStopButton();
		_interactionPanel.add(_playAndStop);
		_playAndStop.addActionListener(new PlayStopListener(_playAndStop));

		JSpinner.DefaultEditor editor = ((JSpinner.DefaultEditor) _spinner.getEditor());
		editor.getTextField().setColumns(2);
		editor.getTextField().setEditable(false);
		
		
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

		_interactionPanel.setMaximumSize(_interactionPanel.getPreferredSize());
		add(_interactionPanel);

		//This panel maintains all information about the tags and displays them
		//as bubbles in a scroll window. When you mouse over it, it will display
		//a text field where you can add more tags.
		_tagPanel = new TagPanel(_card);

		//A ControlScrollPane allows us to responsively scroll through cards as
		//well as tags.
		JScrollPane js = new ControlScrollPane(_tagPanel);
		js.setOpaque(false);
		js.getViewport().setOpaque(false);
		js.setBorder(BorderFactory.createEmptyBorder());
		add(js);
		
		populateSets(defaultSets);

		revalidate();
	}

	/**
	 * As yet unimplemented
	 * @param sets
	 */
	private void populateSets(Collection<FlashCardSet> sets) {}


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
