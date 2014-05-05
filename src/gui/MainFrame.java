package gui;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
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
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import controller.Controller;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private SidePanel sidePanel;
	private FlashboardPanel flashboardPanel;
	private ImportPanel importPanel;
	private QuizletPanel quizletPanel;
	private ExportPanel exportPanel;
	private SetCreationPanel setCreationPanel;
	private CardCreationPanel cardCreationPanel;
	private SettingsPanel settingsPanel;
	private CardLayout mainPanelLayout;
	private JPanel mainPanelContainer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (Exception e) {
					// If Nimbus is not available, you can set the GUI to another look and feel.
				}
			try {
				Controller.launchGUI();
			} catch (Throwable e) {
				System.out.println("Unable to open FlashBoard. ");
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
	setMinimumSize(new Dimension(760, 400));
	setContentPane(contentPane);
	contentPane.setBackground(GuiConstants.CARD_BACKGROUND);
	contentPane.setBorder(BorderFactory.createEmptyBorder());

	// The card layout that we'll use to switch between views
	mainPanelLayout = new CardLayout(0,0);

	JPanel sidePanelContainer = new JPanel();
	contentPane.add(sidePanelContainer, BorderLayout.WEST);
	sidePanelContainer.setLayout(new GridLayout(0, 1, 0, 0));
	sidePanel = new SidePanel();
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
	mainPanelContainer.add(importPanel, GuiConstants.IMPORT_PANEL_NAME);

	//Is this okay?
	quizletPanel = new QuizletPanel();
	mainPanelContainer.add(quizletPanel, GuiConstants.QUIZLET_PANEL_NAME);

	exportPanel = new ExportPanel();
	mainPanelContainer.add(exportPanel, GuiConstants.EXPORT_PANEL_NAME);

	setCreationPanel = new SetCreationPanel();
	setCreationPanel.setControlledLayout(mainPanelLayout);
	setCreationPanel.setControlledPanel(mainPanelContainer);
	mainPanelContainer.add(setCreationPanel, GuiConstants.SET_CREATION_PANEL_NAME);

	cardCreationPanel = new CardCreationPanel();
	mainPanelContainer.add(cardCreationPanel, GuiConstants.CREATE_PANEL_NAME);

	settingsPanel = new SettingsPanel();
	mainPanelContainer.add(settingsPanel, GuiConstants.SETTINGS_PANEL_NAME);

	flashboardPanel = new FlashboardPanel();
	mainPanelContainer.add(flashboardPanel, GuiConstants.FLASHBOARD_PANEL_NAME);

	showTab(TabType.FLASHBOARD);
	contentPane.add(mainPanelContainer, BorderLayout.CENTER);
	pack();
	setVisible(true);
}

public void update(TabType tab) {
	switch (tab) {
	case CARD:
		setCreationPanel.update();
		break;
	case EXPORT:
		break;
	case FLASHBOARD:
		flashboardPanel.update();
		break;
	case IMPORT:
		break;
	case SET:
		break;
	case SETTINGS:
		break;
	default:
		break;
	}
}

public void editCard(FlashCard card) {
	cardCreationPanel.editCard(card);
	showTab(TabType.CARD);

}

public void editSet(FlashCardSet set) {
	setCreationPanel.populateFields(set);
	showTab(TabType.SET);
}

public void showTab(TabType tab) {
	switch (tab) {
	case CARD:
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
	case SET:
		mainPanelLayout.show(mainPanelContainer, GuiConstants.SET_CREATION_PANEL_NAME);
		break;
	case SETTINGS: 
		mainPanelLayout.show(mainPanelContainer, GuiConstants.SETTINGS_PANEL_NAME);
		break;
	default:
		break;
	}

	sidePanel.setSelected(tab);
	update(tab);
}
}