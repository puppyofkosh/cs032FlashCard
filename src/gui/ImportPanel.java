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
	ServerConnectionPanel database;
	JPanel panelQuizlet, panelDatabase, panelTSV;
	JButton btnQuizlet, btnDatabase, btnTSV; 

	/**
	 * Create the panel.
	 */
	public ImportPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		panelQuizlet = new JPanel();
		panelQuizlet.setLayout(new GridLayout(0, 1, 0, 0));
		panelQuizlet.setBackground(GuiConstants.CARD_BACKGROUND.darker());
		panelQuizlet.setOpaque(false);
		panelQuizlet.addMouseListener(this);
		add(panelQuizlet);

		btnQuizlet = IconFactory.createImageButton("From Quizlet", IconType.QUIZLET, 64, true);
		btnQuizlet.setOpaque(false);
		btnQuizlet.addMouseListener(this);
		btnQuizlet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, GuiConstants.QUIZLET_PANEL_NAME);
			}
		});
		panelQuizlet.add(btnQuizlet);

		panelDatabase = new JPanel();
		add(panelDatabase);
		panelDatabase.setLayout(new GridLayout(0, 1, 0, 0));
		panelDatabase.setBackground(GuiConstants.CARD_BACKGROUND.darker());
		panelDatabase.setOpaque(false);
		panelDatabase.addMouseListener(this);


		btnDatabase = IconFactory.createImageButton("From Database", IconType.DATABASE, 64, true);
		btnDatabase.setOpaque(false);
		btnDatabase.addMouseListener(this);
		btnDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, GuiConstants.DATABASE_PANEL_NAME);
			}
		});

		panelDatabase.add(btnDatabase);


		panelTSV = new JPanel();
		add(panelTSV);		
		panelTSV.setLayout(new GridLayout(0, 1, 0, 0));
		panelTSV.setBackground(GuiConstants.CARD_BACKGROUND.darker());
		panelTSV.addMouseListener(this);
		panelTSV.setOpaque(false);

		btnTSV = IconFactory.createImageButton("From TSV File", IconType.IMPORT, 64, true);
		btnTSV.setOpaque(false);
		btnTSV.addMouseListener(this);
		btnTSV.addActionListener(new ActionListener() {
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
					Controller.importCardsFromFile(tsvPath);
				}

			}
		});
		panelTSV.add(btnTSV);
	}

	@Override
	public void setControlledPanel(JPanel panel)
	{
		super.setControlledPanel(panel);
		database = new ServerConnectionPanel();
		panel.add(database, GuiConstants.DATABASE_PANEL_NAME);

	}

	@Override
	public void setControlledLayout(CardLayout layout) {
		super.setControlledLayout(layout);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == panelQuizlet || e.getSource() == btnQuizlet) {
			panelQuizlet.setOpaque(true);
			panelQuizlet.repaint();
		} else if (e.getSource() == panelDatabase || e.getSource() == btnDatabase) {
			panelDatabase.setOpaque(true);
			panelDatabase.repaint();
		} else if (e.getSource() == panelTSV || e.getSource() == btnTSV) {
			panelTSV.setOpaque(true);
			panelTSV.repaint();
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() == panelQuizlet || e.getSource() == btnQuizlet) {
			panelQuizlet.setOpaque(false);
			panelQuizlet.repaint();
		} else if (e.getSource() == panelDatabase || e.getSource() == btnDatabase) {
			panelDatabase.setOpaque(false);
			panelDatabase.repaint();
		} else if (e.getSource() == panelTSV || e.getSource() == btnTSV) {
			panelTSV.setOpaque(false);
			panelTSV.repaint();
		}
	}

	private class TSVFilter extends FileFilter {

		@Override
		public boolean accept(File pathname) {
			String path = pathname.getPath();
			if (pathname.isDirectory())
				return true;

			return path.substring(path.lastIndexOf(".") + 1).equals("tsv");
		}

		@Override
		public String getDescription() {
			return "TSV Files";
		}

	}
}
