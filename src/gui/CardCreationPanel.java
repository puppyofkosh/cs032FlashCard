package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import audio.AudioFile;
import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import gui.IconFactory.IconType;

public class CardCreationPanel extends GenericPanel implements ActionListener, Runnable {

	//Instance variables that are not gui components or are used multiple times
	private static final long serialVersionUID = 1L;
	private AudioFile question;
	private AudioFile answer;
	private boolean recording;
	private ImageIcon recordImage = new ImageIcon("./res/img/Record Button.png");
	private String recordText = "Generate Audio";
	private String playText = "Play";
	private ImageIcon stopImage = new ImageIcon("./res/img/Stop Button.png");
	private String stopText = "Stop";
	private String inputHint = "Text To Speech";
	Set<FlashCardSet> setSet;

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


	/**
	 * Create the panel.
	 */
	public CardCreationPanel() {
		initPanelComponents();
		initFunctionality();
	}

	private void initPanelComponents() {
		setLayout(new BorderLayout(0,0));
		setOpaque(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setOpaque(false);
		add(mainPanel, BorderLayout.CENTER);

		//A row for creating the name of the card.
		namePanel = new JPanel();
		mainPanel.add(namePanel);
		JLabel lblName = new JLabel("Name");
		namePanel.add(lblName);

		textFieldName = new JTextField(10);
		textFieldName.addActionListener(this);
		namePanel.add(textFieldName);

		setSet = new HashSet<>();
		SetSelectionButton setSelectionButton = new SetSelectionButton("Select Sets", setSet);
		namePanel.add(setSelectionButton);

		namePanel.setOpaque(false);
		removeEmptySpace(namePanel);

		qPanel = new JPanel();
		mainPanel.add(qPanel);

		JLabel lblQuestion = new JLabel("Q:");

		textQuestion = new JTextField(20);
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
		mainPanel.add(intervalPanel);

		lblInterval = new JLabel("Interval:");
		intervalPanel.add(lblInterval);

		spinnerInterval = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		JSpinner.DefaultEditor editor = ((JSpinner.DefaultEditor) spinnerInterval.getEditor());
		editor.getTextField().setColumns(2);
		editor.getTextField().setEditable(false);


		intervalPanel.add(spinnerInterval);

		intervalPanel.setOpaque(false);
		removeEmptySpace(intervalPanel);

		aPanel = new JPanel();
		mainPanel.add(aPanel);

		JLabel lblAnswer = new JLabel("A:");
		aPanel.add(lblAnswer);

		btnAnswerRecord =  new ImageToggleButton(recordImage, stopImage, recordText, stopText);
		btnAnswerRecord.addActionListener(this);
		aPanel.add(btnAnswerRecord);

		textAnswer = new JTextField(20);		
		textAnswer.addActionListener(this);
		aPanel.add(textAnswer);

		btnAnswerPlay = ImageToggleButton.playStopButton(playText, stopText);
		btnAnswerPlay.addActionListener(this);
		aPanel.add(btnAnswerPlay);

		aPanel.setOpaque(false);
		removeEmptySpace(aPanel);

		//This panel has an input field and a space for tags to be added.
		tagPanel = new TagPanel("Add Tags:", false);
		JScrollPane scroller = new JScrollPane(tagPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		scroller.setOpaque(false);
		scroller.getViewport().setOpaque(false);
		mainPanel.add(scroller);


		JPanel flashPanel= new JPanel(new BorderLayout(0, 0));
		flashPanel.setOpaque(true);
		flashPanel.setBackground(GuiConstants.CARD_TAG_COLOR);

		btnFlash = new ImageButton("Create Card", IconFactory.loadIcon(IconType.FLASHBOARD, 36, true));
		btnFlash.setOpaque(false);
		btnFlash.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		btnFlash.setHorizontalAlignment(SwingConstants.CENTER);
		btnFlash.addActionListener(this);
		btnFlash.setBorder(BorderFactory.createEmptyBorder());
		flashPanel.add(btnFlash);
		flashPanel.setMaximumSize(flashPanel.getPreferredSize());
		add(flashPanel, BorderLayout.SOUTH);
		textFieldName.requestFocusInWindow();
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
		Controller.stopAudio();
		if (recording) {
			if (isQuestion) {
				question = Controller.finishRecording();
				Controller.guiMessage("has question");
				hasQuestion = true;
			} else {
				answer = Controller.finishRecording();
				Controller.guiMessage("has answer");
				hasAnswer = true;
			}
			recording = false;
		} else {
			recording = true;
			Controller.startRecord(btnQuestionRecord, btnAnswerRecord, this);
		}
	}
	
	public void populate(FlashCard card) {
		question = card.getQuestionAudio();
		hasQuestion = true;
		answer = card.getAnswerAudio();
		hasAnswer = true;
		textFieldName.setText(card.getName());
		spinnerInterval.setValue(card.getInterval());
		tagPanel.reinitialize(card);
	}
	

	private void playToggle(boolean isQuestion, ImageToggleButton button) {
		if (recording) {
			Controller.guiMessage("Can't play audio while recording", true);
			button.toggle();
			return;
		}

		try {
			if (button.isOn())
				Controller.playAudioThenRun(isQuestion ? question : answer, button);
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
			question = Controller.readTTS(textQuestion.getText());
			hasQuestion = true;
		} else {
			answer = Controller.readTTS(textAnswer.getText());
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
		textAnswer.setForeground(Color.GRAY);
		textAnswer.setText(enabled ? inputHint : disableText);
		textAnswer.setForeground(Color.BLACK);
		textAnswer.setEnabled(enabled);

		textQuestion.setForeground(Color.GRAY);
		textQuestion.setText(enabled ? inputHint : disableText);
		textQuestion.setForeground(Color.BLACK);
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
		System.out.println(e.paramString());
		if (e.getSource() == textFieldName) {
			//Do nothing - we only want to hold on to this value for later.
		} else if (e.getSource() == btnQuestionRecord) {
			//This, and the following boolean values usually correspond to
			//isQuestion - whether or not the recording is being done for the
			//question or answer audio file.
			recordToggle(true);
			btnAnswerRecord.toggle();
		} else if (e.getSource() == btnQuestionPlay) {
			playToggle(true, btnQuestionPlay);
		} else if (e.getSource() == textQuestion) {
			readTTS(true);
		} else if (e.getSource() == btnAnswerRecord) {
			recordToggle(false);
			btnQuestionRecord.toggle();
		} else if (e.getSource() == btnAnswerPlay) {
			playToggle(false, btnAnswerPlay);
		} else if (e.getSource() == textAnswer) {
			readTTS(false);
		} else if (e.getSource() == btnFlash) {
			//The user wants to create the card and move on to the next one.
			createCard();
		}
	}
	
	private void createCard() {
		if (question == null || answer == null || !hasQuestion || !hasAnswer) {
			Controller.guiMessage("Must record question and answer", true);
			return;
		}

		SerializableFlashCard.Data data = new SerializableFlashCard.Data();
		String parsedName;
		try {
			parsedName = Controller.parseInput(textFieldName.getText());
			data.name = Controller.parseCardName(parsedName);
		} catch (IOException iox) {
			try {
				data.name = Controller.parseInput(textQuestion.getText());
			} catch (IOException e1) {
				Controller.guiMessage("Invalid card name");
				return;
			}

		}
		data.setQuestion(question);
		data.setAnswer(answer);

		try {
			spinnerInterval.commitEdit();
		} catch (ParseException e1) {
			Controller.guiMessage("ERROR: Could not commit spinner edit", true);
			e1.printStackTrace();
		}
		data.interval = (int) spinnerInterval.getValue();

		data.tags = tagPanel.getTags(false);
		data.pathToFile = SerializableFlashCard.makeFlashCardPath(data);

		FlashCard newCard = Controller.createCard(data);

		for(FlashCardSet set : setSet) {
			try {
				set.addCard(newCard);
			} catch (IOException e1) {
				Controller.guiMessage("Could not add card to Set " + set.getName(), true);
			}
		}
		clear();
		Controller.updateAll();
	}
	
	
	@Override
	public void run() {
		recording = false;
	}
}
