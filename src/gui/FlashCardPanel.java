package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
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
	private JLabel lblQ;
	private JLabel lblA;
	private JSpinner spinner;
	private FlashCard _card;
	
	FlashCardPanel(FlashCard card) {
		super();
		_card = card;
		setBackground(Color.red);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				
				//JLabel Displaying the Card's Name
				cardName = new JLabel(_card.getName());
				cardName.setHorizontalAlignment(SwingConstants.CENTER);
				
				lblQ = new JLabel("Q");
				
				PlayAndStopQ = new JButton("Play Question");
				PlayAndStopQ.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Controller.playAudio(_card.getQuestionAudio());
					}
				});
		
		
		lblA = new JLabel("A");
		PlayAndStopA = new JButton("Play Answer");
		PlayAndStopA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Controller.playAudio(_card.getAnswerAudio());
			}
		});

		System.out.println("Loading " + _card);
		spinner = new JSpinner(new SpinnerNumberModel(_card.getInterval(), 0, 10, 1));
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
		
		JLabel lblInterval = new JLabel("Interval");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(cardName, GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblQ)
								.addComponent(lblA))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(PlayAndStopQ, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(PlayAndStopA, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)))))
					.addGap(3))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(70)
					.addComponent(lblInterval, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(79))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(cardName)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblQ)
						.addComponent(PlayAndStopQ))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblInterval))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(PlayAndStopA)
						.addComponent(lblA))
					.addGap(42))
		);
		setLayout(groupLayout);
		
		
		populateSets(_card.getSets());
		populateTags(_card.getTags());
		//Etc, etc/
	}
	
	private void populateSets(Collection<FlashCardSet> sets) {
	}
	
	private void populateTags(Collection<String> tags) {		
	}
}
