package gui;

import java.awt.Color;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import audio.WavFileConcatenator;
import backend.Exporter;
import backend.ItunesExporter;
import backend.WavFileExporter;
import controller.Controller;
import flashcard.FlashCard;
import flashcard.SimpleFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;

public class ExportPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CardTablePanel _cardTable;
	private JRadioButton rdbtnWav;
	private JRadioButton rdbtnNetwork;
	private JRadioButton rdbtnItunes;
	private Exporter wavExporter;
	private Exporter itunesExporter;
	private JLabel lblGroupName;
	private JTextField groupName;
	private JPanel panel;
	private JButton btnChangeDestination;
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public ExportPanel()  {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		try {
			wavExporter = new WavFileExporter();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JPanel chooseMethodPanel = new JPanel();
		add(chooseMethodPanel);
		
		JTextField searchBox = new JTextField(30);
		searchBox.setForeground(Color.LIGHT_GRAY);
		searchBox.setText("Search Here");
		searchBox.setForeground(Color.BLACK);
		chooseMethodPanel.add(searchBox);
		
		rdbtnWav = new JRadioButton("Wav");
		chooseMethodPanel.add(rdbtnWav);
		
		rdbtnNetwork = new JRadioButton("Network");
		chooseMethodPanel.add(rdbtnNetwork);
		
		rdbtnItunes = new JRadioButton("iTunes");
		chooseMethodPanel.add(rdbtnItunes);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnWav);
		group.add(rdbtnNetwork);
		group.add(rdbtnItunes);

		
		JButton btnExport = new JButton("Export!");
		//				Controller.exportCard(f, destFolder);
		btnExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				_cardTable.updateSelectedCards();
				List<FlashCard> cards = _cardTable.getSelectedCards();
				if (rdbtnWav.isSelected()) {
					try {
						wavExporter.export(cards);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (rdbtnItunes.isSelected()) {
					try {
						itunesExporter = new ItunesExporter(new File(groupName.getText() + ".m3u"));
					
					itunesExporter.export(cards);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		});
		chooseMethodPanel.add(btnExport);
		
		panel = new JPanel();
		add(panel);
		
		lblGroupName = new JLabel("Group Name: ");
		panel.add(lblGroupName);
		
		groupName = new JTextField();
		panel.add(groupName);
		groupName.setColumns(10);
		
		btnChangeDestination = new JButton("Change Audio Destination");
		panel.add(btnChangeDestination);
		btnChangeDestination.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnedValue = fileChooser.showDialog(panel, "Select");
				if (returnedValue == JFileChooser.APPROVE_OPTION)
					WavFileConcatenator.changeDestination(fileChooser.getSelectedFile().getPath());
				
			}
			
		});
		
		_cardTable = new CardTablePanel();
		add(_cardTable);
	}
	
	
	public void update() {
		_cardTable.updateCards(SimpleFactory.getResources().getAllCards());
	}
	
	public void update(List<FlashCard> cards) {
		_cardTable.updateCards(cards);
	}

}
