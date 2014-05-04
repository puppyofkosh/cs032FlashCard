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
import javax.swing.JLabel;

import controller.Controller;

public class SidePanel extends GenericPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel btnFlashboard, btnExport, btnImport, btnCreate, btnSelected;

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

		btnFlashboard = new JLabel("Flashboard", IconFactory.loadIcon(IconType.FLASHBOARD, 32, true), JLabel.LEFT);
		initMenuItem(btnFlashboard);
		add(btnFlashboard);

		btnExport = new JLabel("Export    ", IconFactory.loadIcon(IconType.EXPORT, 32, true), JLabel.LEFT);
		initMenuItem(btnExport);
		add(btnExport);

		add(Box.createVerticalStrut(10));
		btnImport = new JLabel("Import    ", IconFactory.loadIcon(IconType.IMPORT, 32, true), JLabel.LEFT);
		initMenuItem(btnImport);
		add(btnImport);

		add(Box.createVerticalStrut(10));
		btnCreate = new JLabel("Create    ", IconFactory.loadIcon(IconType.CREATE, 32, true), JLabel.LEFT);
		initMenuItem(btnCreate);
		add(btnCreate);

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
			setSelected(btnFlashboard);
		} else if (e.getSource() == btnExport) {
			Controller.switchTabs(TabType.EXPORT);
			setSelected(btnExport);
		} else if (e.getSource() == btnImport) {
			Controller.switchTabs(TabType.IMPORT);
			setSelected(btnImport);
		} else if (e.getSource() == btnCreate) {
			Controller.switchTabs(TabType.CREATE);
			setSelected(btnCreate);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
