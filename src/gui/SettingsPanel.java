package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import audio.WavFileConcatenator;
import settings.Settings;
import utils.Writer;

public class SettingsPanel extends JPanel implements ActionListener {
	public SettingsPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel TimeoutPanel = new JPanel();
		add(TimeoutPanel);
		
		JLabel lblNewLabel_2 = new JLabel("Recording Timeout");
		TimeoutPanel.add(lblNewLabel_2);
		
		spinner = new JSpinner();
		TimeoutPanel.add(spinner);
		spinner.setValue(Settings.getTimeout());
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				
				try {
					spinner.commitEdit();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int newValue = (int) spinner.getValue();
				Settings.setTimeout(newValue);
			}
		});
		
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setEditable(false);
		
		
		JPanel panel = new JPanel();
		add(panel);
		
		lblNewLabel = new JLabel(Settings.getOutputDestination());
		panel.add(lblNewLabel);
		
		btnOutputChanger = new JButton("change output location");
		panel.add(btnOutputChanger);
		btnOutputChanger.addActionListener(this);
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		
		JLabel lblNewLabel_1 = new JLabel("Default Author Name");
		panel_1.add(lblNewLabel_1);
		
		defaultAuthorName = new JTextField();
		panel_1.add(defaultAuthorName);
		defaultAuthorName.setColumns(10);
		defaultAuthorName.setText(Settings.getDefaultAuthor());
		defaultAuthorName.addActionListener(this);
		
		JPanel panel_3 = new JPanel();
		add(panel_3);
		
		mainColorButton = new JButton("Select Color 1");
		panel_3.add(mainColorButton);
		mainColorButton.setForeground(Settings.getMainColor());
		mainColorButton.addActionListener(this);
		
		secondaryColorButton = new JButton("Select Color 2");
		panel_3.add(secondaryColorButton);
		secondaryColorButton.setForeground(Settings.getSecondaryColor());
		secondaryColorButton.addActionListener(this);
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField defaultAuthorName;
	JButton btnOutputChanger;
	JButton mainColorButton;
	JButton secondaryColorButton;
	JSpinner spinner;
	JLabel lblNewLabel;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(mainColorButton)) {
			Color newColor = JColorChooser.showDialog(this, "Choose Main Color", Settings.getMainColor());
			if (newColor != null && !newColor.equals(Settings.getMainColor())) {
				Settings.setMainColor(newColor);
				mainColorButton.setForeground(newColor);
			}
		} else if (e.getSource().equals(secondaryColorButton)) {
			Color newColor = JColorChooser.showDialog(this, "Choose Secondary Color", Settings.getSecondaryColor());
			if (newColor != null && !newColor.equals(Settings.getSecondaryColor())) {
				Settings.setSecondaryColor(newColor);
				secondaryColorButton.setForeground(newColor);
			}
		} else if (e.getSource().equals(btnOutputChanger)) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnedValue = fileChooser.showDialog(this, "Select");
			if (returnedValue == JFileChooser.APPROVE_OPTION) {
				Settings.setDestination(fileChooser.getSelectedFile().getPath());
				lblNewLabel.setText(Settings.getOutputDestination());
			}
		} else if(e.getSource().equals(defaultAuthorName)) {
			String newName = defaultAuthorName.getText();
			if (!newName.equals(Settings.getDefaultAuthor()))
				Settings.setDefaultAuthor(newName);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.add(new SettingsPanel());
		frame.pack();
		frame.setVisible(true);
	}
	
}
