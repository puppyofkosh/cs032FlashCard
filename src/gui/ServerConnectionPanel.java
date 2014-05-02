package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import protocol.NetworkedFlashCard;

import client.Client;
import database.DatabaseFactory;
import flashcard.FlashCard;

@SuppressWarnings("serial")
public class ServerConnectionPanel extends JPanel implements ClientFrontend, ActionListener {

	CardTablePanel _cards;
	Client _client;
	JTextField host;
	JTextField portNumber;
	JTextPane status;
	JButton btnConnect;
	private JPanel searchPanel;
	private JTextField searchField;
	private JButton btnImportSelectedCards;
	
	// A CardTablePanel can only store cards, and we need to store NetworkedCards, so we keep track of the cards here.
	private List<NetworkedFlashCard> availableCards = new ArrayList<>();

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

		_cards = new CardTablePanel();
		add(_cards);

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
		//_cards.updateCards(cards);
		System.out.println("Downloaded " + cards);
		for (FlashCard f : cards)
		{
			DatabaseFactory.writeCard(f);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			String hostName = host.getText();
			int port = -1;
			try {
				//portNumber.commitEdit();
				port = (Integer.parseInt(portNumber.getText()));
				if (!hostName.isEmpty() && port > 0)
					attemptConnection(hostName, port);
			} catch (NumberFormatException e1) {
				guiMessage("Could not parse port number");
				portNumber.setText("");
			}
		} else if (e.getSource() == btnImportSelectedCards) {
			for(FlashCard f : _cards.getSelectedCards())
			{
				if (availableCards.contains(f))
				{
					NetworkedFlashCard nc = (NetworkedFlashCard)f;
					
					_client.requestFullCard(nc);
				}
			}
		} else if (e.getSource() == searchField) {
			if (_client != null) {
				_client.requestCard(searchField.getText());
			}
		}
	}

	@Override
	public void updateCardsForImport(List<NetworkedFlashCard> flashcards) {
		_cards.updateCards(new ArrayList<FlashCard>(flashcards));
		availableCards = flashcards;
	}
}
