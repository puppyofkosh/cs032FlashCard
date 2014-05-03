package gui;

import gui.IconFactory.IconType;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import controller.Controller;

public class ImportPanel extends GenericPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ServerConnectionPanel dbPanel;

	/**
	 * Create the panel.
	 */
	public ImportPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

		JButton btnNewButton = IconFactory.createImageButton("From Quizlet", IconType.QUIZLET, 64);
		btnNewButton.setOpaque(false);
		panel.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, GuiConstants.QUIZLET_PANEL_NAME);
			}

		});

		JPanel panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		panel_1.setOpaque(false);
		panel_1.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));




		JButton btnNewButton_1 = IconFactory.createImageButton("From Database", IconType.DATABASE, 64);
		btnNewButton_1.setOpaque(false);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, GuiConstants.DATABASE_PANEL_NAME);
			}
		});

		panel_1.add(btnNewButton_1);


		JPanel panel_2 = new JPanel();
		add(panel_2);		
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		panel_2.setOpaque(false);

		JButton btnNewButton_2 = IconFactory.createImageButton("From File", IconType.IMPORT, 64);
		btnNewButton_2.setOpaque(false);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				String tsvPath;
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnedValue = fileChooser.showDialog(null, "Select TSV");
				if (returnedValue == JFileChooser.APPROVE_OPTION) {
					tsvPath = fileChooser.getSelectedFile().getPath();
					Controller.importCardsToLibrary(tsvPath);
				}

			}
		});
		panel_2.add(btnNewButton_2);
	}

	@Override
	public void setControlledPanel(JPanel panel)
	{
		super.setControlledPanel(panel);
		dbPanel = new ServerConnectionPanel();
		panel.add(dbPanel, GuiConstants.DATABASE_PANEL_NAME);

	}

	@Override
	public void setControlledLayout(CardLayout layout) {
		super.setControlledLayout(layout);
	}
}
