package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import utils.Writer;
import client.Client;
import flashcard.FlashCard;
import flashcard.SimpleFactory;

@SuppressWarnings("serial")
public class ServerConnectionPanel extends JPanel implements ClientFrontend, ActionListener {

	CardTablePanel _cards;
	Client _client;
	JTextField host;
	JFormattedTextField portNumber;
	JTextPane status;
	JButton btnConnect;
	private JPanel searchPanel;
	private JTextField searchField;
	private JButton btnImportSelectedCards;

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

		portNumber = new JFormattedTextField(NumberFormat.getNumberInstance());
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
		_client.start();
	}

	@Override
	public void update(List<FlashCard> cards) {
		_cards.updateCards(cards);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConnect) {
			String hostName = host.getText();
			int port = -1;
			try {
				portNumber.commitEdit();
				port = ((Long) portNumber.getValue()).intValue();
				if (!hostName.isEmpty() && port > 0)
					attemptConnection(hostName, port);
			} catch (ParseException e1) {
				guiMessage("Could not parse port number");
				portNumber.setText("");
			}
		} else if (e.getSource() == btnImportSelectedCards) {
			for(FlashCard f : _cards.getSelectedCards())
				SimpleFactory.writeCard(f);
		} else if (e.getSource() == searchField) {
			if (_client != null) {
				_client.requestCard(searchField.getText());
			}
		}
	}
}
