package gui;

import gui.GuiConstants.TabType;
import gui.IconFactory.IconType;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

import controller.Controller;

public class SidePanel extends GenericPanel implements MouseListener {

	private static final long serialVersionUID = 1L;

	private JLabel _btnFlashboard, _btnExport, _btnImport, _btnCreate, _btnSets, 
	_btnSettings, _btnSelected;

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

		_btnFlashboard = new JLabel("Flashboard", IconFactory.loadIcon(IconType.FLASHBOARD, 32, true), JLabel.LEFT);
		initMenuItem(_btnFlashboard);
		add(_btnFlashboard);

		add(Box.createVerticalStrut(struts));
		_btnExport = new JLabel("Export    ", IconFactory.loadIcon(IconType.EXPORT, 32, true), JLabel.LEFT);
		initMenuItem(_btnExport);
		add(_btnExport);

		add(Box.createVerticalStrut(struts));
		_btnImport = new JLabel("Import    ", IconFactory.loadIcon(IconType.IMPORT, 32, true), JLabel.LEFT);
		initMenuItem(_btnImport);
		add(_btnImport);

		add(Box.createVerticalStrut(struts));
		_btnCreate = new JLabel("Cards     ", IconFactory.loadIcon(IconType.CREATE, 32, true), JLabel.LEFT);
		initMenuItem(_btnCreate);
		add(_btnCreate);

		add(Box.createVerticalStrut(struts));
		_btnSets = new JLabel("Sets      ", IconFactory.loadIcon(IconType.SET, 32, true), JLabel.LEFT);
		initMenuItem(_btnSets);
		add(_btnSets);

		add(Box.createVerticalStrut(struts));
		//TODO make a real icon.
		_btnSettings = new JLabel("Settings  ", IconFactory.loadIcon(IconType.SETTINGS, 32, true), JLabel.LEFT);
		initMenuItem(_btnSettings);
		add(_btnSettings);

		setSelected(_btnFlashboard);
	}

	/**
	 * Convenience method for formatting side buttons.
	 * @param button
	 */
	private void initMenuItem(JLabel button) {
		button.setForeground(GuiConstants.PRIMARY_FONT_COLOR);
		button.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
		button.setBorder(BorderFactory.createEmptyBorder(11,4,15,10));
		button.addMouseListener(this);
	}

	
	private void setSelected(JLabel button) {
		if (_btnSelected != null) _btnSelected.setOpaque(false);
		_btnSelected = button;
		_btnSelected.setBackground(GuiConstants.CARD_BACKGROUND);
		_btnSelected.setOpaque(true);
		repaint();
	}

	public void setSelected(TabType tab) {
		switch (tab) {
		case CARD:
			setSelected(_btnCreate);
			break;
		case EXPORT:
			setSelected(_btnExport);
			break;
		case FLASHBOARD:
			setSelected(_btnFlashboard);
			break;
		case IMPORT:
			setSelected(_btnImport);
			break;
		case SET:
			setSelected(_btnSets);
			break;
		case SETTINGS:
			setSelected(_btnSettings);
			break;
		default:
			break;
		}
	}

	private void setHovered(JComponent button) {
		if (button == _btnSelected)
			return;
		button.setOpaque(true);
		button.setBackground(GuiConstants.CARD_TAG_COLOR);
	}

	private void setUnHovered(JComponent button) {
		button.setOpaque(false);
		button.setBackground(GuiConstants.CARD_BACKGROUND);
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == _btnFlashboard) {
			Controller.switchTabs(TabType.FLASHBOARD);
		} else if (e.getSource() == _btnExport) {
			Controller.switchTabs(TabType.EXPORT);
		} else if (e.getSource() == _btnImport) {
			Controller.switchTabs(TabType.IMPORT);
		} else if (e.getSource() == _btnCreate) {
			Controller.switchTabs(TabType.CARD);
		}  else if (e.getSource() == _btnSets) {
			Controller.switchTabs(TabType.SET);
		} else if (e.getSource() == _btnSettings) {
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
