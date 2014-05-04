package gui;

import gui.IconFactory.IconType;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import controller.Controller;

public class ImportPanel extends GenericPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ServerConnectionPanel dbPanel;
	JPanel panel, panel_1, panel_2;
	JButton btnNewButton, btnNewButton_1, btnNewButton_2; 

	/**
	 * Create the panel.
	 */
	public ImportPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		panel.setBackground(GuiConstants.CARD_BACKGROUND.darker());
		panel.setOpaque(false);
		panel.addMouseListener(this);
		add(panel);

		btnNewButton = IconFactory.createImageButton("From Quizlet", IconType.QUIZLET, 64, true);
		btnNewButton.setOpaque(false);
		btnNewButton.addMouseListener(this);
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, GuiConstants.QUIZLET_PANEL_NAME);
			}
		});
		panel.add(btnNewButton);

		panel_1 = new JPanel();
		add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		panel_1.setBackground(GuiConstants.CARD_BACKGROUND.darker());
		panel_1.setOpaque(false);
		panel_1.addMouseListener(this);


		btnNewButton_1 = IconFactory.createImageButton("From Database", IconType.DATABASE, 64, true);
		btnNewButton_1.setOpaque(false);
		btnNewButton_1.addMouseListener(this);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, GuiConstants.DATABASE_PANEL_NAME);
			}
		});

		panel_1.add(btnNewButton_1);


		panel_2 = new JPanel();
		add(panel_2);		
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		panel_2.setBackground(GuiConstants.CARD_BACKGROUND.darker());
		panel_2.addMouseListener(this);
		panel_2.setOpaque(false);

		btnNewButton_2 = IconFactory.createImageButton("From File", IconType.IMPORT, 64, true);
		btnNewButton_2.setOpaque(false);
		btnNewButton_2.addMouseListener(this);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				String tsvPath;
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileFilter filter = new TSVFilter();
				fileChooser.setFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(false);
				int returnedValue = fileChooser.showDialog(null, "Select TSV");
				if (returnedValue == JFileChooser.APPROVE_OPTION) {
					if (!fileChooser.accept(fileChooser.getSelectedFile())) {
						Controller.guiMessage("That is not a valid File to import!", true);
						return;
					}
						
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == panel || e.getSource() == btnNewButton) {
			panel.setOpaque(true);
			panel.repaint();
		} else if (e.getSource() == panel_1 || e.getSource() == btnNewButton_1) {
			panel_1.setOpaque(true);
			panel_1.repaint();
		} else if (e.getSource() == panel_2 || e.getSource() == btnNewButton_2) {
			panel_2.setOpaque(true);
			panel_2.repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() == panel || e.getSource() == btnNewButton) {
			panel.setOpaque(false);
			panel.repaint();
		} else if (e.getSource() == panel_1 || e.getSource() == btnNewButton_1) {
			panel_1.setOpaque(false);
			panel_1.repaint();
		} else if (e.getSource() == panel_2 || e.getSource() == btnNewButton_2) {
			panel_2.setOpaque(false);
			panel_2.repaint();
		}
	}
	
	private class TSVFilter extends FileFilter {

		@Override
		public boolean accept(File pathname) {
			// TODO Auto-generated method stub
			String path = pathname.getPath();
			if (pathname.isDirectory())
				return true;
			
			return path.substring(path.lastIndexOf(".") + 1).equals("tsv");
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "tsv files";
		}
		
	}
}
