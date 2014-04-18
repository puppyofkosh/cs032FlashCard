package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import controller.Controller;

public class ImportPanel extends GenericPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ImportPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnNewButton = new JButton("From Quizlet");
		panel.add(btnNewButton);
		
		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnNewButton_1 = new JButton("From DB");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, "record panel");
			}
		});

		panel_1.add(btnNewButton_1);
		
		
		JPanel panel_2 = new JPanel();
		add(panel_2);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnNewButton_2 = new JButton("From File");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Controller.importCardsToLibrary("test-tsv.tsv");
			}
		});
		panel_2.add(btnNewButton_2);
	}
/*
	@Override
	public void setControlledPanel(JPanel panel)
	{
		super.setControlledPanel(panel);
		panel.add(, "client panel");
		
		l.setControlledPanel(panel);
	}
	
	@Override
	public void setControlledLayout(CardLayout layout)
	{
		super.setControlledLayout(layout);
		recordPanel.setControlledLayout(layout);
	}
*/
}
