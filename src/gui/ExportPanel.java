package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import utils.FlashcardConstants;
import utils.Writer;
import audio.WavFileConcatenator;
import backend.Exporter;
import backend.ItunesExporter;
import client.Client;
import database.DatabaseFactory;
import flashcard.FlashCard;

public class ExportPanel extends JPanel implements ClientFrontend {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CardTablePanel _cardTable;
	private JRadioButton rdbtnWav;
	private JRadioButton rdbtnNetwork;
	private JRadioButton rdbtnItunes;
	private Exporter itunesExporter;
	private JPanel panel;
	private JButton btnChangeDestination;
	private Client _client;
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public ExportPanel()  {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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

				if (cards.isEmpty()) {
					JOptionPane.showMessageDialog(panel, "You must pick which cards to export!");
					return;
				}

				if (rdbtnWav.isSelected()) {
					try {
						WavFileConcatenator.concatenate(cards);
						JOptionPane.showMessageDialog(panel, "Audio has been created");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				else if (rdbtnItunes.isSelected()) {
					try {
						String playlist = JOptionPane.showInputDialog(panel, "Choose name of playlist");
						if (playlist == null || playlist.equals("")) {
							JOptionPane.showMessageDialog(panel, "Playlist not created because no name was selected");
							return;
						}
						itunesExporter = new ItunesExporter(new File(playlist + ".m3u"));

						itunesExporter.export(cards);
						JOptionPane.showMessageDialog(panel, "Playlist has been created! To use this playlist, open Itunes and click Import Playlist, and choose the file \'"
								+ playlist + ".m3u\' in the directory this program is located in.");
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				else if(rdbtnNetwork.isSelected()) {
					connectAndExport();
				}

				else
					JOptionPane.showMessageDialog(panel, "You must choose what kind of export to perform!");
			}

		});
		chooseMethodPanel.add(btnExport);

		panel = new JPanel();
		add(panel);

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

	public void connectAndExport() {
		_client = new Client(FlashcardConstants.DEFAULT_HOSTNAME, FlashcardConstants.DEFAULT_PORT, this);
		Writer.out("Starting client");
		_client.start();
		_client.uploadCards(_cardTable.getSelectedCards());
	}

	public void update() {
		_cardTable.updateCards(DatabaseFactory.getResources().getAllCards());
	}

	public void update(List<FlashCard> cards) {
		_cardTable.updateCards(cards);
	}


	@Override
	public void guiMessage(String msg, int duration) {
		if (msg.contains("Successful")) {
			JOptionPane.showMessageDialog(panel, msg + "Shutting down Client");
			if (_client != null) {
				JOptionPane.showMessageDialog(panel, msg + "Shutting down Client");
				_client.kill();
			}
		} else {
			JOptionPane.showMessageDialog(panel, msg);

		}

	}


	@Override
	public void guiMessage(String msg) {
		guiMessage(msg, 3);

	}

	@Override
	public void displayConnectionStatus(boolean connected) {
		// TODO Auto-generated method stub

	}

}
