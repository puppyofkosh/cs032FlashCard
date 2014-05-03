package gui;

import flashcard.FlashCard;
import gui.IconFactory.IconType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

import protocol.NetworkedFlashCard;
import search.SearchParameters;
import utils.FlashcardConstants;
import utils.Writer;
import audio.WavFileConcatenator;
import backend.Exporter;
import backend.ItunesExporter;
import client.Client;
import database.DatabaseFactory;

public class ExportPanel extends JPanel implements ClientFrontend, ActionListener {
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
	private JButton btnExport;
	private Client _client;
	private SetBrowser _setBrowser;
	private JTextField searchBox;
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public ExportPanel() {
		super(new BorderLayout(0,0));

		JPanel mainPanel = new JPanel(new BorderLayout(0,0));
		add(mainPanel);
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		mainPanel.add(headerPanel, BorderLayout.NORTH);


		JPanel chooseMethodPanel = new JPanel();
		headerPanel.add(chooseMethodPanel);

		searchBox = new JTextField(20);
		searchBox.setForeground(Color.LIGHT_GRAY);
		searchBox.setText("Search Here");
		searchBox.setForeground(Color.BLACK);
		searchBox.addActionListener(this);
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

		mainPanel.add(headerPanel, BorderLayout.NORTH);


		JPanel continuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		continuePanel.setBackground(Color.BLACK);
		btnExport = new ImageButton("Export", IconFactory.loadIcon(IconType.EXPORT, 36, true));
		btnExport.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 20));
		btnExport.addActionListener(this);
		continuePanel.add(btnExport);
		mainPanel.add(continuePanel, BorderLayout.SOUTH);

		_cardTable = new CardTablePanel("Cards for Export");
		mainPanel.add(_cardTable, BorderLayout.CENTER);

		_setBrowser = new SetBrowser();
		add(_setBrowser, BorderLayout.EAST);
		
		addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {}

			@Override
			public void componentMoved(ComponentEvent arg0) {}

			@Override
			public void componentResized(ComponentEvent arg0) {}

			@Override
			public void componentShown(ComponentEvent arg0) {
				_setBrowser.updateSourceList();
			}
		});


	}

	public void connectAndExport() {
		_client = new Client(FlashcardConstants.DEFAULT_HOSTNAME, FlashcardConstants.DEFAULT_PORT, this);
		Writer.out("Starting client");
		_client.execute();
		_client.uploadCards(_cardTable.getSelectedCards());
	}

	@Override
	public void updateLocallyStoredCards(List<FlashCard> cards) {
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.searchBox) {
			SearchParameters search = new SearchParameters(searchBox.getText());
			List<FlashCard> results = search.search(DatabaseFactory.getResources());
			updateLocallyStoredCards(results);
		}
		else if (e.getSource() == btnExport) {
			_cardTable.updateSelectedCards();
			List<FlashCard> cards = _cardTable.getSelectedCards();

			if (cards.isEmpty()) {
				JOptionPane.showMessageDialog(panel, "You must pick which cards to export!");
				return;
			}

			if (rdbtnWav.isSelected()) {
				try {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnedValue = fileChooser.showDialog(panel, "Select");
					if (returnedValue == JFileChooser.APPROVE_OPTION) {
						WavFileConcatenator.changeDestination(fileChooser.getSelectedFile().getPath());
						WavFileConcatenator.concatenate(cards);
						JOptionPane.showMessageDialog(panel, "Audio has been created");
					}
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
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
				} catch (Throwable ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

			else if(rdbtnNetwork.isSelected()) {
				connectAndExport();
			}

			else
				JOptionPane.showMessageDialog(panel, "You must choose what kind of export to perform!");
		} 
	}

	@Override
	public void updateCardsForImport(List<NetworkedFlashCard> flashcards) {
		throw new UnsupportedOperationException();
	}
	
}
