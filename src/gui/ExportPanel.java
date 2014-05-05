package gui;

import flashcard.FlashCard;
import gui.IconFactory.IconType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import protocol.NetworkedFlashCard;
import search.SearchParameters;
import utils.FlashcardConstants;
import utils.Writer;
import audio.WavFileConcatenator;
import backend.Exporter;
import backend.ItunesExporter;
import client.Client;
import controller.Controller;
import database.DatabaseFactory;

public class ExportPanel extends JPanel implements ClientFrontend, ActionListener, PropertyChangeListener, Browsable {
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

	private ProgressMonitor progressMonitor;

	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public ExportPanel() {
		super(new BorderLayout(0,0));
		setOpaque(false);

		JPanel mainPanel = new JPanel(new BorderLayout(0,0));
		mainPanel.setOpaque(false);
		add(mainPanel);
		JPanel headerPanel = new JPanel();
		headerPanel.setOpaque(false);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		// Will call addSetBrowser whenever this panel is shown
		addComponentListener(new SetBrowserComponentListener(this));

		JPanel chooseMethodPanel = new JPanel();
		chooseMethodPanel.setOpaque(false);

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

		_cardTable = new CardTablePanel("Add Cards for Export");
		mainPanel.add(_cardTable, BorderLayout.CENTER);
	}


	public void connectAndExport() {
		_client = new Client(FlashcardConstants.DEFAULT_HOSTNAME, FlashcardConstants.DEFAULT_PORT, this);
		Writer.out("Starting client");

		progressMonitor = new ProgressMonitor(ExportPanel.this,
				"Uploading",
				"", 0, 100);
		progressMonitor.setProgress(0);
		progressMonitor.setNote("Uploading");
		progressMonitor.setMillisToDecideToPopup(progressMonitor.getMillisToDecideToPopup()/4);

		_client.addPropertyChangeListener(this);
		_client.execute();
		_client.uploadCards(_cardTable.getAllCards());
	}

	@Override
	public void updateLocallyStoredCards(List<FlashCard> cards) {
		_cardTable.updateCards(cards);
	}


	@Override
	public void guiMessage(String msg, int duration) {
		if (msg.contains("Successful")) {
			JOptionPane.showMessageDialog(panel, msg + " Shutting down Client");
			if (_client != null) {
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
			List<FlashCard> cards = _cardTable.getAllCards();

			if (cards.isEmpty()) {
				Controller.guiMessage("You must pick some cards to export!", true);
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
			} else
				Controller.guiMessage("You must choose what kind of export to perform!", true);
		} 
	}

	@Override
	public void updateCardsForImport(List<NetworkedFlashCard> flashcards) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Handles events from the client worker
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (_client.getState() == SwingWorker.StateValue.DONE)
		{
			progressMonitor.close();
		}

		if ("progress".equals(evt.getPropertyName()) && progressMonitor != null) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message =
					String.format("Completed %d%%.\n", progress);
			progressMonitor.setNote(message);
			if (progressMonitor.isCanceled() || _client.isDone()) {
				_client.cancel(true);
				progressMonitor.close();
			}
		}
	}

	@Override
	public void showSetBrowser(SetBrowser browser) {
		_setBrowser = browser;
		add(_setBrowser, BorderLayout.EAST);
		revalidate();
		repaint();
	}

	@Override
	public void removeSetBrowser() {
	}

}
