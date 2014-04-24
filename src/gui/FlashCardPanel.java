package gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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
	JLabel cardName;
	JButton PlayAndStopQ, PlayAndStopA;
	JComboBox<Integer> incrementInterval;
	List<JLabel> tags;
	List<JLabel> sets;
	private JSpinner spinner;
	private FlashCard _card;
	private String playText = "Play";
	private String stopText = "Stop";
	private JPanel panel;

	FlashCardPanel(FlashCard card) {
		super(new GridLayout(0, 1, 0, 0));
		_card = card;
		setBackground(Color.red);
		Border border = new CompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createEmptyBorder(0, 20, 0, 20));
		setBorder(border);

		System.out.println("Loading " + _card);

		//JLabel Displaying the Card's Name
		cardName = new JLabel(_card.getName());
		add(cardName);
		cardName.setHorizontalAlignment(SwingConstants.CENTER);

		PlayAndStopQ = new ImageToggleButton(new ImageIcon("./res/img/Play Button.png"),
				new ImageIcon("./res/img/Stop Button.png"), playText +  " Q", stopText);
		PlayAndStopQ.setHorizontalAlignment(SwingConstants.CENTER);
		add(PlayAndStopQ);
		PlayAndStopQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Controller.playAudio(_card.getQuestionAudio());
			}
		});

		panel = new JPanel();
		panel.setOpaque(false);
		add(panel);

		JLabel lblInterval = new JLabel("Interval");
		panel.add(lblInterval);
		lblInterval.setHorizontalAlignment(SwingConstants.CENTER);
		spinner = new JSpinner(new SpinnerNumberModel(_card.getInterval(), 0, 10, 1));
		panel.add(spinner);
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setColumns(2);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = (int) spinner.getValue();
				try {
					_card.setInterval(newValue);
				} catch (IOException e1) {
					Writer.err("ERROR: Could not write change to card - reverting to original interval");
					spinner.setValue(_card.getInterval());
				}
			}
		});
		PlayAndStopA = new ImageToggleButton(new ImageIcon("./res/img/Play Button.png"),
				new ImageIcon("./res/img/Stop Button.png"), playText + " A", stopText);
		PlayAndStopA.setHorizontalAlignment(SwingConstants.CENTER);
		add(PlayAndStopA);
		PlayAndStopA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Controller.playAudio(_card.getAnswerAudio());
			}
		});

		populateSets(_card.getSets());
		populateTags(_card.getTags());
		revalidate();
		//Etc, etc/
	}

	private void populateSets(Collection<FlashCardSet> sets) {
	}

	private void populateTags(Collection<String> tags) {		
	}
}
