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
	private JTextField defaultAuthorName;
	JButton btnOutputChanger;
	JButton mainColorButton;
	JButton secondaryColorButton;
	JSpinner timeoutSpinner;
	JLabel lblNewLabel;
	JTextField host, portNumber;


	public SettingsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		JPanel timeoutPanel = new JPanel();
		timeoutPanel.setOpaque(false);
		add(timeoutPanel);

		JLabel timeoutLabel = new JLabel("Recording Timeout");
		timeoutPanel.add(timeoutLabel);

		timeoutSpinner = new JSpinner(new SpinnerNumberModel(Settings.getTimeout(), 0, 1000, 5));
		((JSpinner.DefaultEditor) timeoutSpinner.getEditor()).getTextField().setEditable(false);
		timeoutPanel.add(timeoutSpinner);
		timeoutSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				try {
					timeoutSpinner.commitEdit();
				} catch (ParseException e1) {
				}
				int newValue = (int) timeoutSpinner.getValue();
				Settings.setTimeout(newValue);
			}
		});

		JPanel connectionButtons = new JPanel();
		connectionButtons.setOpaque(false);
		add(connectionButtons);
		JLabel lblHost = new JLabel("Database Hostname:");
		connectionButtons.add(lblHost);

		host = new JTextField(Settings.getHost());
		host.setColumns(10);
		host.addActionListener(this);
		connectionButtons.add(host);

		JLabel lblPort = new JLabel("Port Number:");
		connectionButtons.add(lblPort);

		portNumber = new JTextField(Settings.getPortNumber());
		portNumber.setColumns(5);
		portNumber.addActionListener(this);
		connectionButtons.add(portNumber);

		JPanel outputPanel = new JPanel();
		outputPanel.setOpaque(false);
		add(outputPanel);

		lblNewLabel = new JLabel("Output Location: " + Settings.getOutputDestination());
		outputPanel.add(lblNewLabel);

		btnOutputChanger = new JButton("Change Output Location");
		outputPanel.add(btnOutputChanger);
		btnOutputChanger.addActionListener(this);

		JPanel authorPanel = new JPanel();
		authorPanel.setOpaque(false);
		add(authorPanel);

		JLabel authorLabel = new JLabel("Default Author Name");
		authorPanel.add(authorLabel);

		defaultAuthorName = new JTextField();
		authorPanel.add(defaultAuthorName);
		defaultAuthorName.setColumns(10);
		defaultAuthorName.setText(Settings.getDefaultAuthor());
		defaultAuthorName.addActionListener(this);


		JLabel colorLabel = new JLabel("Change application colors for next launch.");
		JPanel colorPanel = new JPanel();
		colorPanel.setOpaque(false);
		colorPanel.add(colorLabel);
		add(colorPanel);
		
		
		mainColorButton = new JButton("Select Color 1");
		colorPanel.add(mainColorButton);
		mainColorButton.setForeground(Settings.getMainColor());
		mainColorButton.setBackground(Color.BLACK);
		mainColorButton.addActionListener(this);

		secondaryColorButton = new JButton("Select Color 2");
		colorPanel.add(secondaryColorButton);
		secondaryColorButton.setForeground(Settings.getSecondaryColor());
		secondaryColorButton.setBackground(Color.BLACK);
		secondaryColorButton.addActionListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mainColorButton) {
			Color newColor = JColorChooser.showDialog(this, "Choose Main Color", Settings.getMainColor());
			if (newColor != null && !newColor.equals(Settings.getMainColor())) {
				Settings.setMainColor(newColor);
				mainColorButton.setForeground(newColor);
			}
		} else if (e.getSource() == secondaryColorButton) {
			Color newColor = JColorChooser.showDialog(this, "Choose Secondary Color", Settings.getSecondaryColor());
			if (newColor != null && !newColor.equals(Settings.getSecondaryColor())) {
				Settings.setSecondaryColor(newColor);
				secondaryColorButton.setForeground(newColor);
			}
		} else if (e.getSource() == btnOutputChanger) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnedValue = fileChooser.showDialog(this, "Select");
			if (returnedValue == JFileChooser.APPROVE_OPTION) {
				Settings.setDestination(fileChooser.getSelectedFile().getPath());
				lblNewLabel.setText("Output Location: " + Settings.getOutputDestination());
			}
		} else if(e.getSource() == defaultAuthorName) {
			String newName = defaultAuthorName.getText();
			if (!newName.equals(Settings.getDefaultAuthor()))
				Settings.setDefaultAuthor(newName);
		} else if (e.getSource() == portNumber) {
			Settings.setPortNumber(portNumber.getText());
		} else if (e.getSource() == host) {
			Settings.setHost(host.getText());
		}
	}
}
