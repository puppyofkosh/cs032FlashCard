package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import protocol.NetworkedFlashCard;
import search.SearchParameters;
import settings.Settings;
import utils.Writer;
import audio.WavFileConcatenator;
import backend.Exporter;
import backend.ItunesExporter;
import client.Client;
import controller.Controller;
import database.DatabaseFactory;
import database.SetAddWorker;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

public class ExportPanel extends JPanel implements ClientFrontend,
		ActionListener, PropertyChangeListener, Browsable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CardTablePanel _cardTable;
	private JRadioButton rdbtnWav;
	private JRadioButton rdbtnNetwork;
	private JRadioButton rdbtnItunes;
	private JRadioButton rdbtnSet;
	private Exporter itunesExporter;
	private JPanel panel;
	private JButton btnExport;
	private Client _client;
	private SetBrowser _setBrowser;
	private JTextField searchBox;

	// Worker used for when we "export" to set
	private SetAddWorker setAdditionWorker;

	private ProgressMonitor progressMonitor;

	/**
	 * Create the panel.
	 * 
	 * @throws IOException
	 */
	public ExportPanel() {
		super(new BorderLayout(0, 0));
		setOpaque(false);

		JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
		mainPanel.setOpaque(false);
		add(mainPanel);
		JPanel headerPanel = new JPanel(new BorderLayout(0, 0));
		headerPanel.setOpaque(false);
		mainPanel.add(headerPanel, BorderLayout.NORTH);

		// Will call addSetBrowser whenever this panel is shown
		addComponentListener(new SetBrowserComponentListener(this));

		JPanel chooseMethodPanel = new JPanel();
		chooseMethodPanel.setOpaque(false);

		headerPanel.add(chooseMethodPanel, BorderLayout.EAST);

		searchBox = new JTextField("Search Here");
		searchBox.addActionListener(this);
		headerPanel.add(searchBox, BorderLayout.CENTER);
		
		// FIXME: Until we get search wokring
		searchBox.setVisible(false);

		rdbtnWav = new JRadioButton("Wav");
		chooseMethodPanel.add(rdbtnWav);

		rdbtnNetwork = new JRadioButton("Network");
		chooseMethodPanel.add(rdbtnNetwork);

		rdbtnItunes = new JRadioButton("iTunes");
		chooseMethodPanel.add(rdbtnItunes);

		rdbtnSet = new JRadioButton("Set");
		chooseMethodPanel.add(rdbtnSet);

		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnWav);
		group.add(rdbtnNetwork);
		group.add(rdbtnItunes);
		group.add(rdbtnSet);

		mainPanel.add(headerPanel, BorderLayout.NORTH);

		JPanel continuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,
				0));
		continuePanel.setBackground(GuiConstants.SET_TAG_COLOR);
		btnExport = new JButton("Export");
		btnExport.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		btnExport.setBackground(Color.BLACK);
		btnExport.addActionListener(this);
		continuePanel.add(btnExport);
		mainPanel.add(continuePanel, BorderLayout.SOUTH);

		_cardTable = new CardTablePanel("Add Cards for Export");
		mainPanel.add(_cardTable, BorderLayout.CENTER);
	}

	public void connectAndExport() {
		_client = new Client(Settings.getHost(), Integer.parseInt(Settings
				.getPortNumber()), this);
		Writer.out("Starting client");

		progressMonitor = new ProgressMonitor(ExportPanel.this, "Uploading",
				"", 0, 100);
		progressMonitor.setProgress(0);
		progressMonitor.setNote("Uploading");
		progressMonitor.setMillisToDecideToPopup(progressMonitor
				.getMillisToDecideToPopup() / 4);

		_client.addPropertyChangeListener(this);
		_client.execute();
		_client.uploadCards(_cardTable.getAllCards());
	}

	@Override
	public void updateLocallyStoredCards(List<FlashCard> cards) {
		_cardTable.updateDisplayedCards(cards);
	}

	@Override
	public void clientMessage(String msg, int duration) {
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
	public void clientMessage(String msg) {
		clientMessage(msg, 3);

	}

	@Override
	public void displayConnectionStatus(boolean connected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.searchBox) {
			SearchParameters search = new SearchParameters(searchBox.getText());
			List<FlashCard> results = search.search(DatabaseFactory
					.getResources());
			updateLocallyStoredCards(results);
		} else if (e.getSource() == btnExport) {
			List<FlashCard> cards = _cardTable.getAllCards();

			if (cards.isEmpty()) {
				Controller.guiMessage("You must pick some cards to export!",
						true);
				return;
			}

			if (rdbtnWav.isSelected()) {
				try {
					WavFileConcatenator.concatenate(cards);
					JOptionPane.showMessageDialog(panel,
							"Audio has been created");
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

			else if (rdbtnItunes.isSelected()) {
				try {
					String playlist = JOptionPane.showInputDialog(panel,
							"Choose name of playlist");
					if (playlist == null || playlist.equals("")) {
						JOptionPane
								.showMessageDialog(panel,
										"Playlist not created because no name was selected");
						return;
					}
					itunesExporter = new ItunesExporter(new File(playlist
							+ ".m3u"));

					itunesExporter.export(cards);
					JOptionPane
							.showMessageDialog(
									panel,
									"Playlist has been created!\nTo use this playlist, open Itunes and click Import Playlist, and choose the file \'"
											+ playlist
											+ ".m3u\' in the directory this program is located in.");
				} catch (IOException ioe) {
					Controller.guiMessage("Could not export to itunes "
							+ ioe.getMessage());
				}

			}

			else if (rdbtnNetwork.isSelected()) {
				connectAndExport();
			}

			else if (rdbtnSet.isSelected()) {
				FlashCardSet set = Controller
						.createNewSet("Merged set",
								Settings.getDefaultAuthor(),
								new ArrayList<String>(), 0);
				setAdditionWorker = new SetAddWorker(set, cards);
				setAdditionWorker.addPropertyChangeListener(this);
				setAdditionWorker.execute();

			}

			else {
				Controller
						.guiMessage(
								"You must choose what kind of export to perform!",
								true);
			}
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
		if ((_client != null && _client.getState() == SwingWorker.StateValue.DONE)
				|| (setAdditionWorker != null && setAdditionWorker.getState() == SwingWorker.StateValue.DONE)) {
			Controller.updateGUI(Controller.getCurrentTab());
			if (progressMonitor != null)
				progressMonitor.close();
		}

		if ("progress".equals(evt.getPropertyName()) && progressMonitor != null) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message = String.format("Completed %d%%.\n", progress);
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
