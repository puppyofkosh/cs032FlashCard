package gui;

import gui.GuiConstants.TabType;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ImportPanel importPanel;
	private QuizletPanel quizletPanel;
	private ExportPanel exportPanel;
	private SetCreationPanel setCreationPanel;
	private FlashboardPanel flashboardPanel;
	private CardLayout mainPanelLayout;
	private JPanel mainPanelContainer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Controller.launchGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(GuiConstants.WIDTH, GuiConstants.HEIGHT));
		contentPane = new JPanel(new BorderLayout(0,0));
		setContentPane(contentPane);
		contentPane.setBackground(GuiConstants.CARD_BACKGROUND);
		contentPane.setBorder(BorderFactory.createEmptyBorder());

		// The card layout that we'll use to switch between views
		mainPanelLayout = new CardLayout(0,0);

		JPanel sidePanelContainer = new JPanel();
		contentPane.add(sidePanelContainer, BorderLayout.WEST);
		sidePanelContainer.setLayout(new GridLayout(0, 1, 0, 0));
		SidePanel sidePanel = new SidePanel(this);
		sidePanel.setBorder(BorderFactory.createEmptyBorder());
		sidePanelContainer.add(sidePanel);

		mainPanelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		mainPanelContainer.setOpaque(false);
		mainPanelContainer.setLayout(mainPanelLayout);

		// set up the side panel so pressing the "home" button will bring us home and such
		sidePanel.setControlledLayout(mainPanelLayout);
		sidePanel.setControlledPanel(mainPanelContainer);		

		importPanel = new ImportPanel();
		importPanel.setControlledLayout(mainPanelLayout);
		importPanel.setControlledPanel(mainPanelContainer);

		quizletPanel = new QuizletPanel();
		mainPanelContainer.add(quizletPanel, GuiConstants.QUIZLET_PANEL_NAME);

		mainPanelContainer.add(importPanel, GuiConstants.IMPORT_PANEL_NAME);

		exportPanel = new ExportPanel();
		mainPanelContainer.add(exportPanel, GuiConstants.EXPORT_PANEL_NAME);

		setCreationPanel = new SetCreationPanel();
		setCreationPanel.setControlledLayout(mainPanelLayout);
		setCreationPanel.setControlledPanel(mainPanelContainer);
		mainPanelContainer.add(setCreationPanel, GuiConstants.CREATE_PANEL_NAME);

		flashboardPanel = new FlashboardPanel();
		mainPanelContainer.add(flashboardPanel, GuiConstants.FLASHBOARD_PANEL_NAME);

		showTab(TabType.FLASHBOARD);
		contentPane.add(mainPanelContainer, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public void update(TabType tab) {
		switch (tab) {
		case CREATE:
			setCreationPanel.update();
			break;
		case EXPORT:
			break;
		case FLASHBOARD:
			flashboardPanel.update();
			break;
		case IMPORT:
			break;
		default:
			break;

		}
	}

	public void showTab(TabType tab) {
		switch (tab) {
		case CREATE:
			mainPanelLayout.show(mainPanelContainer, GuiConstants.CREATE_PANEL_NAME);
			break;
		case EXPORT:
			mainPanelLayout.show(mainPanelContainer, GuiConstants.EXPORT_PANEL_NAME);
			break;
		case FLASHBOARD:
			mainPanelLayout.show(mainPanelContainer, GuiConstants.FLASHBOARD_PANEL_NAME);
			break;
		case IMPORT:
			mainPanelLayout.show(mainPanelContainer, GuiConstants.IMPORT_PANEL_NAME);
			break;
		default:
			break;
		}
		update(tab);
	}
}