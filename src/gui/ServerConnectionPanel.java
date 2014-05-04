package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.SwingWorker;

import controller.Controller;

import protocol.NetworkedFlashCard;
import client.Client;
import database.DatabaseFactory;
import database.SetAddWorker;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

@SuppressWarnings("serial")
public class ServerConnectionPanel extends JPanel implements ClientFrontend, PropertyChangeListener,
		ActionListener {

	CardTablePanel _serverCards;

	CardTablePanel _selectedCards;

	Client _client;
	JTextField host;
	JTextField portNumber;
	JTextPane status;
	JButton btnConnect;
	private JPanel searchPanel;
	private JTextField searchField;
	private JButton btnImportSelectedCards;
	private JButton btnAddCardToSelectedPanel;
	private JButton btnRemoveCardFromSelectedPanel;

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

		searchPanel = new JPanel();
		add(searchPanel);

		searchField = new JTextField();
		searchField.addActionListener(this);
		searchPanel.add(searchField);
		searchField.setColumns(10);

		btnImportSelectedCards = new JButton("Import Selected Cards");
		btnImportSelectedCards.addActionListener(this);
		searchPanel.add(btnImportSelectedCards);

		btnAddCardToSelectedPanel = new JButton("Add");
		btnAddCardToSelectedPanel.addActionListener(this);
		searchPanel.add(btnAddCardToSelectedPanel);

		btnRemoveCardFromSelectedPanel = new JButton("Remove");
		btnRemoveCardFromSelectedPanel.addActionListener(this);
		searchPanel.add(btnRemoveCardFromSelectedPanel);

		_serverCards = new CardTablePanel();
		_serverCards.setPreferredSize(new Dimension(_serverCards.getPreferredSize().width, _serverCards.getPreferredSize().height / 2));
		add(_serverCards);
				
		_selectedCards = new CardTablePanel();
		add(_selectedCards);

		status = new JTextPane();
		status.setEditable(false);
		status.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		StyledDocument doc = status.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		displayConnectionStatus(false);
		add(status);

		JPanel connectionButtons = new JPanel();
		add(connectionButtons);

		JLabel lblHost = new JLabel("Host:");
		connectionButtons.add(lblHost);

		host = new JTextField(14);
		connectionButtons.add(host);

		JLabel lblPort = new JLabel("PORT:");
		connectionButtons.add(lblPort);

		portNumber = new JTextField();
		portNumber.setColumns(5);
		connectionButtons.add(portNumber);

		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		connectionButtons.add(btnConnect);

	}

	@Override
	public void displayConnectionStatus(boolean connected) {
		if (status == null)
			return;
		if (connected) {
			status.setText("Connected");
			status.setBackground(Color.GREEN);
		} else {
			status.setText("Disconnected");
			status.setBackground(Color.RED);
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
		// _cards.updateCards(cards);
		System.out.println("Downloaded " + cards);

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
			String hostName = host.getText();
			int port = -1;
			try {
				// portNumber.commitEdit();
				port = (Integer.parseInt(portNumber.getText()));
				if (!hostName.isEmpty() && port > 0)
					attemptConnection(hostName, port);
			} catch (NumberFormatException e1) {
				guiMessage("Could not parse port number");
				portNumber.setText("");
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
		} else if (e.getSource() == btnAddCardToSelectedPanel) {
			// Add the selected cards in _availableCards to the _selectedCards
			// pane
			for (FlashCard f : _serverCards.getSelectedCards()) {
				if (!_selectedCards.getAllCards().contains(f)) {
					_selectedCards.addCard(f);
				}
			}
		} else if (e.getSource() == btnRemoveCardFromSelectedPanel) {
			for (FlashCard f : _selectedCards.getSelectedCards()) {
				_selectedCards.removeCard(f);
			}
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
