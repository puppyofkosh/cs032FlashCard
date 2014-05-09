package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import settings.Settings;

public class SettingsPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField _defaultAuthorName;
	JButton _btnOutputChanger;
	JButton _mainColorButton;
	JButton _secondaryColorButton;
	JSpinner _timeoutSpinner;
	JLabel _lblNewLabel;
	JTextField _host, _portNumber;


	public SettingsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		JPanel timeoutPanel = new JPanel();
		timeoutPanel.setOpaque(false);
		add(timeoutPanel);

		JLabel timeoutLabel = new JLabel("Recording Timeout");
		timeoutPanel.add(timeoutLabel);
		timeoutLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);

		_timeoutSpinner = new JSpinner(new SpinnerNumberModel(Settings.getTimeout(), 0, 1000, 5));
		((JSpinner.DefaultEditor) _timeoutSpinner.getEditor()).getTextField().setEditable(false);
		timeoutPanel.add(_timeoutSpinner);
		_timeoutSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					_timeoutSpinner.commitEdit();
				} catch (ParseException e1) {
				}
				int newValue = (int) _timeoutSpinner.getValue();
				Settings.setTimeout(newValue);
			}
		});
		
		JPanel connectionButtons = new JPanel();
		connectionButtons.setOpaque(false);
		add(connectionButtons);
		JLabel lblHost = new JLabel("Database Hostname:");
		connectionButtons.add(lblHost);
		lblHost.setForeground(GuiConstants.PRIMARY_FONT_COLOR);

		_host = new JTextField(Settings.getHost());
		_host.setColumns(10);
		_host.addActionListener(this);
		connectionButtons.add(_host);

		JLabel lblPort = new JLabel("Port Number:");
		connectionButtons.add(lblPort);
		lblPort.setForeground(GuiConstants.PRIMARY_FONT_COLOR);

		_portNumber = new JTextField(Settings.getPortNumber());
		_portNumber.setColumns(5);
		_portNumber.addActionListener(this);
		connectionButtons.add(_portNumber);

		JPanel outputPanel = new JPanel();
		outputPanel.setOpaque(false);
		add(outputPanel);

		_lblNewLabel = new JLabel("Output Location: " + Settings.getOutputDestination());
		outputPanel.add(_lblNewLabel);
		_lblNewLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);

		_btnOutputChanger = new JButton("Change Output Location");
		_btnOutputChanger.setBackground(Color.BLACK);
		_btnOutputChanger.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		outputPanel.add(_btnOutputChanger);
		_btnOutputChanger.addActionListener(this);

		JPanel authorPanel = new JPanel();
		authorPanel.setOpaque(false);
		add(authorPanel);

		JLabel authorLabel = new JLabel("Default Author Name");
		authorPanel.add(authorLabel);
		authorLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);

		_defaultAuthorName = new JTextField();
		authorPanel.add(_defaultAuthorName);
		_defaultAuthorName.setColumns(10);
		_defaultAuthorName.setText(Settings.getDefaultAuthor());
		_defaultAuthorName.addActionListener(this);


		JLabel colorLabel = new JLabel("Change application colors for next launch.");
		JPanel colorPanel = new JPanel();
		colorPanel.setOpaque(false);
		colorPanel.add(colorLabel);
		add(colorPanel);
		colorLabel.setForeground(GuiConstants.PRIMARY_FONT_COLOR);


		_mainColorButton = new JButton("Select Color 1");
		colorPanel.add(_mainColorButton);
		_mainColorButton.setForeground(Settings.getMainColor());
		_mainColorButton.setBackground(Color.BLACK);
		_mainColorButton.addActionListener(this);

		_secondaryColorButton = new JButton("Select Color 2");
		colorPanel.add(_secondaryColorButton);
		_secondaryColorButton.setForeground(Settings.getSecondaryColor());
		_secondaryColorButton.setBackground(Color.BLACK);
		_secondaryColorButton.addActionListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _mainColorButton) {
			Color newColor = JColorChooser.showDialog(this, "Choose Main Color", Settings.getMainColor());
			if (newColor != null && !newColor.equals(Settings.getMainColor())) {
				Settings.setMainColor(newColor);
				_mainColorButton.setForeground(newColor);
			}
		} else if (e.getSource() == _secondaryColorButton) {
			Color newColor = JColorChooser.showDialog(this, "Choose Secondary Color", Settings.getSecondaryColor());
			if (newColor != null && !newColor.equals(Settings.getSecondaryColor())) {
				Settings.setSecondaryColor(newColor);
				_secondaryColorButton.setForeground(newColor);
			}
		} else if (e.getSource() == _btnOutputChanger) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnedValue = fileChooser.showDialog(this, "Select");
			if (returnedValue == JFileChooser.APPROVE_OPTION) {
				Settings.setDestination(fileChooser.getSelectedFile().getPath());
				_lblNewLabel.setText("Output Location: " + Settings.getOutputDestination());
			}
		} else if(e.getSource() == _defaultAuthorName) {
			String newName = _defaultAuthorName.getText();
			if (!newName.equals(Settings.getDefaultAuthor()))
				Settings.setDefaultAuthor(newName);
		} else if (e.getSource() == _portNumber) {
			Settings.setPortNumber(_portNumber.getText());
		} else if (e.getSource() == _host) {
			Settings.setHost(_host.getText());
		}
	}
}
