package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import audio.AudioFile;
import audio.DiscRecorder;
import audio.Recorder;
import audio.TextToSpeechReader;
import controller.Controller;
import flashcard.LocallyStoredFlashCard;

public class CardCreationPanel extends GenericPanel implements ActionListener {

	//Instance variables that are not gui components or are used multiple times
	private static final long serialVersionUID = 1L;
	private AudioFile question;
	private AudioFile answer;
	private Recorder recorder;
	private TextToSpeechReader reader;
	private boolean recording;
	private ImageIcon recordImage = new ImageIcon("./res/img/Record Button.png");
	private String recordText = "Generate Audio";
	private String playText = "Play";
	private ImageIcon stopImage = new ImageIcon("./res/img/Stop Button.png");
	private String stopText = "Stop";

	//The following gui variables are arranged from top to bottom, like their
	//physical representations on the screen.

	private JPanel namePanel;
	private JTextField textFieldName;

	private JPanel qPanel;
	private boolean hasQuestion;
	private ImageToggleButton btnQuestionRecord, btnQuestionPlay;
	private JTextField textQuestion;

	private JPanel intervalPanel;
	private JSpinner spinnerInterval;
	private JLabel lblInterval;

	private JPanel aPanel;
	private boolean hasAnswer;
	private ImageToggleButton btnAnswerRecord, btnAnswerPlay;
	private JTextField textAnswer;
	private TagPanel tagPanel;

	private JButton btnFlash;
	private JPanel panel_1;


	/**
	 * Create the panel.
	 */
	public CardCreationPanel() {
		Controller.guiMessage("Initializing", false);
		initPanelComponents();
		initFunctionality();
	}

	private void initPanelComponents() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(GuiConstants.CARD_BACKGROUND);

		//A row for creating the name of the card.
		namePanel = new JPanel();
		add(namePanel);
		JLabel lblName = new JLabel("Name");
		namePanel.add(lblName);

		textFieldName = new JTextField();
		textFieldName.setColumns(10);
		textFieldName.addActionListener(this);
		namePanel.add(textFieldName);

		namePanel.setOpaque(false);
		removeEmptySpace(namePanel);


		qPanel = new JPanel();
		add(qPanel);

		JLabel lblQuestion = new JLabel("Q:");

		textQuestion = new JTextField();
		textQuestion.setColumns(10);
		textQuestion.addActionListener(this);

		btnQuestionRecord = new ImageToggleButton(recordImage, stopImage, recordText, stopText);
		btnQuestionRecord.addActionListener(this);

		btnQuestionPlay = ImageToggleButton.playStopButton(playText, stopText);
		qPanel.add(lblQuestion);
		qPanel.add(btnQuestionRecord);
		qPanel.add(textQuestion);
		qPanel.add(btnQuestionPlay);
		btnQuestionPlay.addActionListener(this);

		qPanel.setOpaque(false);
		removeEmptySpace(qPanel);

		intervalPanel = new JPanel();
		add(intervalPanel);

		lblInterval = new JLabel("Interval:");
		intervalPanel.add(lblInterval);

		spinnerInterval = new JSpinner();
		intervalPanel.add(spinnerInterval);

		intervalPanel.setOpaque(false);
		removeEmptySpace(intervalPanel);

		aPanel = new JPanel();
		add(aPanel);

		JLabel lblAnswer = new JLabel("A:");
		aPanel.add(lblAnswer);

		btnAnswerRecord =  new ImageToggleButton(recordImage, stopImage, recordText, stopText);
		btnAnswerRecord.addActionListener(this);
		aPanel.add(btnAnswerRecord);

		textAnswer = new JTextField();
		textAnswer.setColumns(10);
		textAnswer.addActionListener(this);
		aPanel.add(textAnswer);

		btnAnswerPlay = ImageToggleButton.playStopButton(playText, stopText);
		btnAnswerPlay.addActionListener(this);
		aPanel.add(btnAnswerPlay);

		aPanel.setOpaque(false);
		removeEmptySpace(aPanel);

		//This panel has an input field and a space for tags to be added.
		panel_1 = new JPanel(new BorderLayout());
		add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		panel_1.setOpaque(false);
		tagPanel = new TagPanel();
		tagPanel.setEmptyText("Add Tags:");
		JScrollPane scroller = new JScrollPane(tagPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);
		panel_1.add(scroller, BorderLayout.CENTER);
		panel_1.setMaximumSize(new Dimension(aPanel.getPreferredSize().width, 1000));

		btnFlash = new JButton("Flash!");
		add(btnFlash);
		btnFlash.addActionListener(this);
	}

	private void initFunctionality() {
		hasQuestion = false;
		question = null;
		hasAnswer = false;
		answer = null;
		recording = false;
		enablePlayback(Controller.hasPlayer());
		enableTTS(Controller.hasReader());
	}

	private void removeEmptySpace(JPanel panel) {
		panel.setMaximumSize(panel.getPreferredSize());
	}

	private void recordToggle(boolean isQuestion) {
		if (recording) {
			if (isQuestion) {
				question = recorder.stopRecord();
				Controller.guiMessage("has question");
				hasQuestion = true;
			} else {
				answer = recorder.stopRecord();
				Controller.guiMessage("has answer");
				hasAnswer = true;
			}
			recording = false;
		} else {
			recorder = new DiscRecorder();
			recorder.startRecord();
			recording = true;
		}
	}

	private void playToggle(boolean isQuestion, boolean play) {
		try {
			if (play)
				Controller.playAudio(isQuestion ? question : answer);
			else
				Controller.stopAudio();
		} catch (IOException e1) {
			if (isQuestion)
				btnQuestionPlay.toggle();
			else
				btnAnswerPlay.toggle();

			Controller.guiMessage("ERROR: could not play audio", true);
		}
	}

	/**
	 * Reads the value of one of the two tts fields and writes the WAV audio.
	 * @param isQuestion - the field can be the question or the answer field.
	 */
	private void readTTS(boolean isQuestion) {
		if (isQuestion) {
			question = reader.read(textQuestion.getText());
			hasQuestion = true;
		} else {
			answer = reader.read(textAnswer.getText());
			hasAnswer = true;
		}
	}

	/**
	 * Enables or disables user interaction with the text to speech fields
	 * and prints a useful message.
	 * @param enabled
	 */
	private void enableTTS(boolean enabled) {
		String disableText = "TTS unavailable.";
		textAnswer.setText(enabled ? "" : disableText);
		textAnswer.setEnabled(enabled);
		textQuestion.setText(enabled ? "" : disableText);
		textQuestion.setEnabled(enabled);
	}

	/**
	 * Enables or disables user interaction with playback buttons.
	 * @param enabled
	 */
	private void enablePlayback(boolean enabled) {
		btnQuestionPlay.setEnabled(enabled);
		btnAnswerPlay.setEnabled(enabled);
	}

	private void clear() {
		removeAll();
		initPanelComponents();
		initFunctionality();
	}

	/**
	 * Handles user interactions with this panel.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == textFieldName) {
			//Do nothing - we only want to hold on to this value for later.
		} else if (e.getSource() == btnQuestionRecord) {
			//This, and the following boolean values usually correspond to
			//isQuestion - whether or not the recording is being done for the
			//question or answer audio file.
			recordToggle(true);
		} else if (e.getSource() == btnQuestionPlay) {
			playToggle(true, btnQuestionPlay.isOn());
		} else if (e.getSource() == textQuestion) {
			readTTS(true);
		} else if (e.getSource() == btnAnswerRecord) {
			recordToggle(false);
		} else if (e.getSource() == btnAnswerPlay) {
			playToggle(false, btnAnswerPlay.isOn());
		} else if (e.getSource() == textAnswer) {
			readTTS(false);
		} else if (e.getSource() == btnFlash) {
			//The user wants to create the card and move on to the next one.
			if (question == null || answer == null || !hasQuestion || !hasAnswer) {
				Controller.guiMessage("Must record question and answer", true);
				return;
			}

			LocallyStoredFlashCard.Data data = new LocallyStoredFlashCard.Data();
			data.name = Controller.parseCardName(textFieldName.getText());

			data.question = question;
			data.answer = answer;

			try {
				spinnerInterval.commitEdit();
			} catch (ParseException e1) {
				Controller.guiMessage("ERROR: Could not commit spinner edit", true);
				e1.printStackTrace();
			}
			data.interval = (int) spinnerInterval.getValue();

			data.tags = tagPanel.getTags();
			data.pathToFile = LocallyStoredFlashCard.makeFlashCardPath(data);

			Controller.createCard(data);

			clear();
			// Move user to the next pane
			controlledLayout.show(controlledPanel, "create panel");

		}
	}
}
