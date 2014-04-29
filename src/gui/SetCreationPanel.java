package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SetCreationPanel extends GenericPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField setTextField, authorTextField;
	private JButton btnContinue;
	private TagPanel tags;
	private SetBrowser setBrowser;
	private CardCreationPanel recordPanel;


	@Override
	public void setControlledPanel(JPanel panel) {
		super.setControlledPanel(panel);
		panel.add(recordPanel, "record panel");

		recordPanel.setControlledPanel(panel);
	}

	@Override
	public void setControlledLayout(CardLayout layout)	{
		super.setControlledLayout(layout);
		recordPanel.setControlledLayout(layout);
	}

	/**
	 * Create the panel.
	 */
	public SetCreationPanel() {
		setLayout(new BorderLayout(0, 0));
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JLabel lblAssignSet = new JLabel("Assign Set", SwingConstants.CENTER);
		lblAssignSet.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(lblAssignSet);

		JPanel headerPanel = new JPanel();
		mainPanel.add(headerPanel);

		JLabel lblNewLabel = new JLabel("Set Name");
		headerPanel.add(lblNewLabel);

		setTextField = new JTextField(10);
		setTextField.addActionListener(this);
		headerPanel.add(setTextField);

		JLabel lblAuthor = new JLabel("Author");
		headerPanel.add(lblAuthor);

		authorTextField = new JTextField(10);
		authorTextField.addActionListener(this);
		headerPanel.add(authorTextField);

		JPanel tagPanel = new JPanel(new BorderLayout());
		mainPanel.add(tagPanel);

		JLabel lblGlobalTags = new JLabel("Global Tags");
		lblGlobalTags.setHorizontalAlignment(SwingConstants.CENTER);
		tagPanel.add(lblGlobalTags, BorderLayout.NORTH);

		tags = new TagPanel();
		tagPanel.add(new JScrollPane(tags), BorderLayout.CENTER);

		btnContinue = new JButton("CONTINUE");
		btnContinue.setHorizontalAlignment(SwingConstants.RIGHT);
		btnContinue.addActionListener(this);
		mainPanel.add(btnContinue);

		recordPanel = new CardCreationPanel();
		add(mainPanel, BorderLayout.CENTER);
		
		setBrowser = new SetBrowser();
		add(setBrowser, BorderLayout.EAST);
	}
	
	public void update() {
		setBrowser.updateSourceList();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == setTextField) {

		} else if (e.getSource() == authorTextField) {

		} else if (e.getSource() == btnContinue) {
			controlledLayout.show(controlledPanel, "record panel");
		}
	}
}
