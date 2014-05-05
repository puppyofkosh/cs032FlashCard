package gui;

import gui.GuiConstants.TabType;
import gui.IconFactory.IconType;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import controller.Controller;

public class SidePanel extends GenericPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel btnFlashboard, btnExport, btnImport, btnCreate, btnSets, 
	btnSettings, btnSelected;

	/**
	 * Create the panel.
	 */
	public SidePanel() {
		initialize();
	}

	/**
	 * Set up the swing components
	 */
	public void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.BLACK);
		int struts = 0;

		btnFlashboard = new JLabel("Flashboard", IconFactory.loadIcon(IconType.FLASHBOARD, 32, true), JLabel.LEFT);
		initMenuItem(btnFlashboard);
		add(btnFlashboard);

		add(Box.createVerticalStrut(struts));
		btnExport = new JLabel("Export    ", IconFactory.loadIcon(IconType.EXPORT, 32, true), JLabel.LEFT);
		initMenuItem(btnExport);
		add(btnExport);

		add(Box.createVerticalStrut(struts));
		btnImport = new JLabel("Import    ", IconFactory.loadIcon(IconType.IMPORT, 32, true), JLabel.LEFT);
		initMenuItem(btnImport);
		add(btnImport);

		add(Box.createVerticalStrut(struts));
		btnCreate = new JLabel("Cards     ", IconFactory.loadIcon(IconType.CREATE, 32, true), JLabel.LEFT);
		initMenuItem(btnCreate);
		add(btnCreate);

		add(Box.createVerticalStrut(struts));
		btnSets = new JLabel("Sets      ", IconFactory.loadIcon(IconType.SET, 32, true), JLabel.LEFT);
		initMenuItem(btnSets);
		add(btnSets);

		add(Box.createVerticalStrut(struts));
		//TODO make a real icon.
		btnSettings = new JLabel("Settings  ", IconFactory.loadIcon(IconType.SETTINGS, 32, true), JLabel.LEFT);
		initMenuItem(btnSettings);
		add(btnSettings);

		setSelected(btnFlashboard);
	}

	private void initMenuItem(JLabel button) {
		button.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		button.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
		button.setBorder(BorderFactory.createEmptyBorder(11,4,15,10));
		button.addMouseListener(this);
	}

	private void setSelected(JLabel button) {
		if (btnSelected != null) btnSelected.setOpaque(false);
		btnSelected = button;
		btnSelected.setBackground(GuiConstants.CARD_BACKGROUND);
		btnSelected.setOpaque(true);
		repaint();
	}

	public void setSelected(TabType tab) {
		switch (tab) {
		case CARD:
			setSelected(btnCreate);
			break;
		case EXPORT:
			setSelected(btnExport);
			break;
		case FLASHBOARD:
			setSelected(btnFlashboard);
			break;
		case IMPORT:
			setSelected(btnImport);
			break;
		case SET:
			setSelected(btnSets);
			break;
		case SETTINGS:
			setSelected(btnSettings);
			break;
		default:
			break;
		}
	}

	private void setHovered(JComponent button) {
		if (button == btnSelected)
			return;
		button.setOpaque(true);
		button.setBackground(GuiConstants.CARD_TAG_COLOR);
	}

	private void setUnHovered(JComponent button) {
		button.setOpaque(false);
		button.setBackground(GuiConstants.CARD_BACKGROUND);
	}

	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == btnFlashboard) {
			Controller.switchTabs(TabType.FLASHBOARD);
		} else if (e.getSource() == btnExport) {
			Controller.switchTabs(TabType.EXPORT);
		} else if (e.getSource() == btnImport) {
			Controller.switchTabs(TabType.IMPORT);
		} else if (e.getSource() == btnCreate) {
			Controller.switchTabs(TabType.CARD);
		}  else if (e.getSource() == btnSets) {
			Controller.switchTabs(TabType.SET);
		} else if (e.getSource() == btnSettings) {
			Controller.switchTabs(TabType.SETTINGS);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		setHovered(((JComponent) e.getSource()));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setUnHovered(((JComponent) e.getSource()));
	}
}
