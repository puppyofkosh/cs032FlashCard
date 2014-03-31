package ian_gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JTextPane;

public class RecordPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the panel.
	 */
	public RecordPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel QuestionPanels = new JPanel();
		add(QuestionPanels);
		GridBagLayout gbl_QuestionPanels = new GridBagLayout();
		gbl_QuestionPanels.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_QuestionPanels.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_QuestionPanels.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_QuestionPanels.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		QuestionPanels.setLayout(gbl_QuestionPanels);
		
		JLabel lblQuestion = new JLabel("Question");
		GridBagConstraints gbc_lblQuestion = new GridBagConstraints();
		gbc_lblQuestion.insets = new Insets(0, 0, 5, 5);
		gbc_lblQuestion.gridx = 0;
		gbc_lblQuestion.gridy = 3;
		QuestionPanels.add(lblQuestion, gbc_lblQuestion);
		
		JButton btnRecord = new JButton("Record!");
		GridBagConstraints gbc_btnRecord = new GridBagConstraints();
		gbc_btnRecord.insets = new Insets(0, 0, 5, 5);
		gbc_btnRecord.gridx = 3;
		gbc_btnRecord.gridy = 3;
		QuestionPanels.add(btnRecord, gbc_btnRecord);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 6;
		gbc_textField.gridy = 3;
		QuestionPanels.add(textField, gbc_textField);
		textField.setColumns(10);
		
		JButton btnPlay = new JButton("Play!");
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.insets = new Insets(0, 0, 5, 5);
		gbc_btnPlay.gridx = 3;
		gbc_btnPlay.gridy = 4;
		QuestionPanels.add(btnPlay, gbc_btnPlay);
		
		JButton btnUseTts = new JButton("Use TTS");
		GridBagConstraints gbc_btnUseTts = new GridBagConstraints();
		gbc_btnUseTts.insets = new Insets(0, 0, 5, 0);
		gbc_btnUseTts.gridx = 6;
		gbc_btnUseTts.gridy = 4;
		QuestionPanels.add(btnUseTts, gbc_btnUseTts);
		
		JLabel lblAnswer = new JLabel("Answer");
		GridBagConstraints gbc_lblAnswer = new GridBagConstraints();
		gbc_lblAnswer.insets = new Insets(0, 0, 5, 5);
		gbc_lblAnswer.gridx = 0;
		gbc_lblAnswer.gridy = 7;
		QuestionPanels.add(lblAnswer, gbc_lblAnswer);
		
		JButton btnAnswer = new JButton("Answer");
		GridBagConstraints gbc_btnAnswer = new GridBagConstraints();
		gbc_btnAnswer.insets = new Insets(0, 0, 5, 5);
		gbc_btnAnswer.gridx = 3;
		gbc_btnAnswer.gridy = 7;
		QuestionPanels.add(btnAnswer, gbc_btnAnswer);
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 6;
		gbc_textField_1.gridy = 7;
		QuestionPanels.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Play");

		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 8;
		QuestionPanels.add(btnNewButton, gbc_btnNewButton);
		
		JButton btnUseTts_1 = new JButton("Use TTS");
		GridBagConstraints gbc_btnUseTts_1 = new GridBagConstraints();
		gbc_btnUseTts_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnUseTts_1.gridx = 6;
		gbc_btnUseTts_1.gridy = 8;
		QuestionPanels.add(btnUseTts_1, gbc_btnUseTts_1);
		
		JLabel lblNewLabel = new JLabel("Tags");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 4;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 9;
		QuestionPanels.add(lblNewLabel, gbc_lblNewLabel);
		
		JTextPane textPane = new JTextPane();
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridheight = 2;
		gbc_textPane.insets = new Insets(0, 0, 5, 0);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 6;
		gbc_textPane.gridy = 9;
		QuestionPanels.add(textPane, gbc_textPane);
		
		JButton btnFlash = new JButton("Flash!");
		GridBagConstraints gbc_btnFlash = new GridBagConstraints();
		gbc_btnFlash.gridwidth = 3;
		gbc_btnFlash.insets = new Insets(0, 0, 0, 5);
		gbc_btnFlash.gridx = 0;
		gbc_btnFlash.gridy = 16;
		QuestionPanels.add(btnFlash, gbc_btnFlash);
		
		JPanel AnswerInformation = new JPanel();
		add(AnswerInformation);

	}

}
