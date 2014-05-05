package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import protocol.NetworkedFlashCard;
import settings.Settings;
import client.Client;
import controller.Controller;
import database.SetAddWorker;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import gui.GuiConstants.TabType;

@SuppressWarnings("serial")
public class ServerConnectionPanel extends JPanel implements ClientFrontend, PropertyChangeListener,
ActionListener {

	CardTablePanel _serverCards;

	CardTablePanel _selectedCards;

	Client _client;
	JTextPane status;
	JButton btnConnect;
	boolean _connected;
	private JPanel searchPanel;
	private JTextField searchField;
	private JButton btnImportSelectedCards;
	private JButton btnBack;

	// keeps track of add worker
	private ProgressMonitor downloadProgress;


	// After we've downloaded the cards, this is the worker that we use to add them to their
	// set
	private SetAddWorker setAdditionWorker;

	// A CardTablePanel can only store cards, and we need to store
	// NetworkedCards, so we keep track of the cards here.
	private List<NetworkedFlashCard> _networkedCards = new ArrayList<>();

	ServerConnectionPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		searchPanel = new JPanel(new BorderLayout(10,0));
		searchPanel.setOpaque(false);
		add(searchPanel);

		searchField = new JTextField();
		searchField.addActionListener(this);
		searchPanel.add(searchField, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		searchPanel.add(buttonPanel, BorderLayout.EAST);


		btnImportSelectedCards = new JButton("Import Selected Cards");
		btnImportSelectedCards.addActionListener(this);
		buttonPanel.add(btnImportSelectedCards);

		btnBack = new JButton("Back");
		btnBack.addActionListener(this);
		buttonPanel.add(btnBack);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		buttonPanel.add(btnConnect);

		JPanel serverTables = new JPanel(new BorderLayout(10,0));
		serverTables.setOpaque(false);
		add(serverTables);

		_serverCards = new CardTablePanel("Server Cards");
		serverTables.add(_serverCards, BorderLayout.WEST);

		_selectedCards = new CardTablePanel("Import Queue");
		serverTables.add(_selectedCards, BorderLayout.CENTER);

		status = new JTextPane();
		status.setEditable(false);
		status.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		StyledDocument doc = status.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		displayConnectionStatus(false);
		add(status);

	}

	@Override
	public void displayConnectionStatus(boolean connected) {
		_connected = connected;
		if (status == null)
			return;
		if (_connected) {
			status.setText("Connected");
			status.setBackground(Color.GREEN);
			btnConnect.setText("Disconnect");
		} else {
			status.setText("Disconnected");
			status.setBackground(Color.RED);
			btnConnect.setText("Connect");
		}
	}

	@Override
	public void guiMessage(String msg, int duration) {
		status.setText(msg);
		repaint();
	}

	@Override
	public void guiMessage(String msg) {
		guiMessage(msg, 3);
	}

	private void attemptConnection(String hostname, int port) {
		guiMessage("Attempting connection", 1);
		_client = new Client(hostname, port, this);
		_client.execute();
	}

	@Override
	public void updateLocallyStoredCards(List<FlashCard> cards) {
		FlashCardSet set = Controller.generateNewSet("Downloaded", "", new ArrayList<String>(), 0);

		setAdditionWorker = new SetAddWorker(set, cards);
		setAdditionWorker.addPropertyChangeListener(this);

		downloadProgress = new ProgressMonitor(this,
				"Uploading",
				"", 0, 100);
		downloadProgress.setProgress(0);
		downloadProgress.setNote("Uploading");
		downloadProgress.setMillisToDecideToPopup(downloadProgress.getMillisToDecideToPopup()/4);

		setAdditionWorker.execute();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			if (!_connected) {
				String hostName = Settings.getHost();
				int port = -1;
				try {
					port = (Integer.parseInt(Settings.getPortNumber()));
					if (!hostName.isEmpty() && port > 0)
						attemptConnection(hostName, port);
				} catch (NumberFormatException e1) {
					guiMessage("Could not parse port number");
				}
			} else {
				_client.kill();
			}
		} else if (e.getSource() == btnImportSelectedCards) {
			if (_client == null)
				return;			

			List<NetworkedFlashCard> cardsToDownload = new ArrayList<>();
			for (FlashCard f : _selectedCards.getAllCards()) {
				if (_networkedCards.contains(f)) {
					NetworkedFlashCard nc = (NetworkedFlashCard) f;
					cardsToDownload.add(nc);
				}
			}
			_client.requestFullCard(cardsToDownload);

		} else if (e.getSource() == searchField) {
			if (_client != null) {
				_client.requestCard(searchField.getText());
			}
		} else if (e.getSource() == btnBack) {
			Controller.switchTabs(TabType.IMPORT);
		}
	}

	@Override
	public void updateCardsForImport(List<NetworkedFlashCard> flashcards) {
		_serverCards.updateCards(new ArrayList<FlashCard>(flashcards));
		_networkedCards = flashcards;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (setAdditionWorker.getState() == SwingWorker.StateValue.DONE && downloadProgress != null)
		{
			System.out.println("Done");
			downloadProgress.close();
		}

		if ("progress".equals(evt.getPropertyName()) && downloadProgress != null) {
			int progress = (Integer) evt.getNewValue();
			downloadProgress.setProgress(progress);
			String message =
					String.format("Completed %d%%.\n", progress);
			downloadProgress.setNote(message);
			if (downloadProgress.isCanceled() || setAdditionWorker.isDone()) {
				setAdditionWorker.cancel(false);
				downloadProgress.close();
			}
		}		
	}
}
