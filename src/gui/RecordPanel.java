package gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JTextPane;

import audio.AudioFile;
import audio.AudioPlayer;
import audio.BasicAudioPlayer;
import audio.ByteArrayAudioPlayer;
import audio.FreeTTSReader;
import audio.MemoryRecorder;
import audio.Recorder;
import audio.TextToSpeechReader;

public class RecordPanel extends GenericPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textQuestion;
	private JTextField textAnswer;
	private AudioFile question;
	private AudioFile answer;
	private JButton btnQuestionRecord;
	private JButton btnAnswerRecord;
	private JButton btnQuestionPlay;
	private JButton btnAnswerPlay;
	private JButton btnQuestionStop;
	private JButton btnAnswerStop;
	private Recorder recorder;
	private AudioPlayer player;
	private TextToSpeechReader reader;
	private boolean hasQuestion;
	private boolean hasAnswer;
	private boolean recording;
	

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public RecordPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		System.out.println("Initializing");
		player = new ByteArrayAudioPlayer();
		//player = new BasicAudioPlayer();
		try{
		reader = new FreeTTSReader();
		} catch (Throwable e) {
			System.out.println(e.getMessage());
		}
		hasQuestion = false;
		hasAnswer = false;
		
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
		
		btnQuestionRecord = new JButton("Record!");
		GridBagConstraints gbc_btnRecord = new GridBagConstraints();
		gbc_btnRecord.insets = new Insets(0, 0, 5, 5);
		gbc_btnRecord.gridx = 3;
		gbc_btnRecord.gridy = 3;
		QuestionPanels.add(btnQuestionRecord, gbc_btnRecord);
		btnQuestionRecord.addActionListener(new RecordListener(true));
		
		textQuestion = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.gridwidth = 2;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 5;
		gbc_textField.gridy = 3;
		QuestionPanels.add(textQuestion, gbc_textField);
		textQuestion.setColumns(10);
		
		btnQuestionPlay = new JButton("Play!");
		GridBagConstraints gbc_btnPlay = new GridBagConstraints();
		gbc_btnPlay.insets = new Insets(0, 0, 5, 5);
		gbc_btnPlay.gridx = 3;
		gbc_btnPlay.gridy = 4;
		QuestionPanels.add(btnQuestionPlay, gbc_btnPlay);
		btnQuestionPlay.addActionListener(new PlayListener(true));
		btnQuestionPlay.setVisible(false);
		
		btnQuestionStop = new JButton("Stop");
		GridBagConstraints gbc_btnStop = new GridBagConstraints();
		gbc_btnStop.insets = new Insets(0, 0, 5, 5);
		gbc_btnStop.gridx = 5;
		gbc_btnStop.gridy = 4;
		QuestionPanels.add(btnQuestionStop, gbc_btnStop);
		btnQuestionStop.addActionListener(new StopListener(true));
		
		JButton btnUseTts = new JButton("Use TTS");
		GridBagConstraints gbc_btnUseTts = new GridBagConstraints();
		gbc_btnUseTts.insets = new Insets(0, 0, 5, 0);
		gbc_btnUseTts.gridx = 6;
		gbc_btnUseTts.gridy = 4;
		QuestionPanels.add(btnUseTts, gbc_btnUseTts);
		btnUseTts.addActionListener(new TTSListener(true));
		
		JLabel lblAnswer = new JLabel("Answer");
		GridBagConstraints gbc_lblAnswer = new GridBagConstraints();
		gbc_lblAnswer.insets = new Insets(0, 0, 5, 5);
		gbc_lblAnswer.gridx = 0;
		gbc_lblAnswer.gridy = 7;
		QuestionPanels.add(lblAnswer, gbc_lblAnswer);
		
		btnAnswerRecord = new JButton("Record!");
		GridBagConstraints gbc_btnAnswer = new GridBagConstraints();
		gbc_btnAnswer.insets = new Insets(0, 0, 5, 5);
		gbc_btnAnswer.gridx = 3;
		gbc_btnAnswer.gridy = 7;
		QuestionPanels.add(btnAnswerRecord, gbc_btnAnswer);
		btnAnswerRecord.addActionListener(new RecordListener(false));
		
		textAnswer = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 5;
		gbc_textField_1.gridy = 7;
		QuestionPanels.add(textAnswer, gbc_textField_1);
		textAnswer.setColumns(10);
		
		btnAnswerPlay = new JButton("Play");
		GridBagConstraints gbc_btnAnswerPlay = new GridBagConstraints();
		gbc_btnAnswerPlay.insets = new Insets(0, 0, 5, 5);
		gbc_btnAnswerPlay.gridx = 3;
		gbc_btnAnswerPlay.gridy = 8;
		QuestionPanels.add(btnAnswerPlay, gbc_btnAnswerPlay);
		btnAnswerPlay.addActionListener(new PlayListener(false));
		btnAnswerPlay.setVisible(false);
		
		btnAnswerStop = new JButton("Stop");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 5;
		gbc_btnNewButton.gridy = 8;
		QuestionPanels.add(btnAnswerStop, gbc_btnNewButton);
		btnAnswerStop.addActionListener(new StopListener(false));
		
		JButton btnUseTts_1 = new JButton("Use TTS");
		GridBagConstraints gbc_btnUseTts_1 = new GridBagConstraints();
		gbc_btnUseTts_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnUseTts_1.gridx = 6;
		gbc_btnUseTts_1.gridy = 8;
		QuestionPanels.add(btnUseTts_1, gbc_btnUseTts_1);
		btnUseTts_1.addActionListener(new TTSListener(false));
		
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
		btnFlash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, "create panel");
			}
		});
		GridBagConstraints gbc_btnFlash = new GridBagConstraints();
		gbc_btnFlash.gridwidth = 3;
		gbc_btnFlash.insets = new Insets(0, 0, 0, 5);
		gbc_btnFlash.gridx = 0;
		gbc_btnFlash.gridy = 16;
		QuestionPanels.add(btnFlash, gbc_btnFlash);
		
		JPanel AnswerInformation = new JPanel();
		add(AnswerInformation);

		btnAnswerPlay.setEnabled(false);
		btnQuestionPlay.setEnabled(false);
		
	}
	
	private void enableButtons(boolean value) {
		btnQuestionRecord.setEnabled(value);
		btnAnswerRecord.setEnabled(value);
		btnQuestionPlay.setEnabled(value);
		btnAnswerPlay.setEnabled(value);
	}
	
	private class RecordListener implements ActionListener {

		private boolean isQuestion;
		
		RecordListener(boolean isQuestion) {
			this.isQuestion = isQuestion;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			enableButtons(false);
			recorder = new MemoryRecorder();
			recorder.startRecord();
			recording = true;
			if (isQuestion)
				btnQuestionStop.setEnabled(true);
			else
				btnAnswerStop.setEnabled(true);
		}
	}
	
	private class PlayListener implements ActionListener {

		private boolean isQuestion;
		
		PlayListener(boolean isQuestion) {
			this.isQuestion = isQuestion;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				enableButtons(false);
				if (isQuestion) {
					player.play(question);
					btnQuestionStop.setEnabled(true);
				}
				else {
					player.play(answer);
					btnQuestionStop.setEnabled(true);
				}
			} catch (IOException e1) {}
		}
	}
	
	private class TTSListener implements ActionListener {
	
		private boolean isQuestion;
		
		TTSListener(boolean isQuestion) {
			this.isQuestion = isQuestion;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isQuestion) {
				question = reader.read(textQuestion.getText());
				btnQuestionPlay.setVisible(true);
			}
			
			else {
				answer = reader.read(textAnswer.getText());
				hasAnswer = true;
				btnAnswerPlay.setVisible(true);
			}
		}
	}
	
	private class StopListener implements ActionListener {
		
		private boolean isQuestion;
		
		StopListener(boolean isQuestion) {
			this.isQuestion = isQuestion;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			enableButtons(true);
			btnQuestionStop.setEnabled(false);
			btnAnswerStop.setEnabled(false);
			
			if (!recording) {
				player.stop();
				return;
			}
			
			
			recording = false;
			
			if (isQuestion) { 
				question = recorder.stopRecord();
				System.out.println("has question");
				hasQuestion = true;
				btnQuestionPlay.setVisible(true);
			}
			
			else { 
				answer = recorder.stopRecord();
				hasAnswer = true;
				btnAnswerPlay.setVisible(true);
			}
		}
		
	}
}
