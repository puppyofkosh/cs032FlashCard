package gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	private ImportPanel importPanel;
	private ExportPanel exportPanel;
	private CreatePanel createPanel;
	private CardSelectionPanel exportCardSelectionPanel;
	private FlashboardPanel flashboardPanel;
	
	private CardLayout mainPanelLayout;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public final String IMPORT_PANEL_NAME = "import panel";
	public final String EXPORT_SELECTION_PANEL_NAME = "export selection panel";
	public final String EXPORT_PANEL_NAME = "export panel";
	public final String CREATE_PANEL_NAME = "create panel";
	public final String FLASHBOARD_PANEL_NAME = "flashboard panel";
	private int WIDTH = 1000;
	private int HEIGHT = 500;
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1000, 500));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		// The card layout that we'll use to switch between views
		mainPanelLayout = new CardLayout(0,0);
		
		JPanel sidePanelContainer = new JPanel();
		sidePanelContainer.setPreferredSize(new Dimension(WIDTH/5, HEIGHT));
		contentPane.add(sidePanelContainer);
		sidePanelContainer.setLayout(new GridLayout(0, 1, 0, 0));
		SidePanel sidePanel = new SidePanel();
		sidePanelContainer.add(sidePanel);
		
		JPanel mainPanelContainer = new JPanel();
		mainPanelContainer.setPreferredSize(new Dimension(4*WIDTH/5, HEIGHT));
		mainPanelContainer.setLayout(mainPanelLayout);
		
		// set up the side panel so pressing the "home" button will bring us home and such
		sidePanel.setControlledLayout(mainPanelLayout);
		sidePanel.setControlledPanel(mainPanelContainer);		
		
		importPanel = new ImportPanel();
		//importPanel.setControlledLayout(mainPanelLayout);
		//importPanel.setControlledPanel(mainPanelContainer);
		mainPanelContainer.add(importPanel, IMPORT_PANEL_NAME);
		
		exportPanel = new ExportPanel();
		//exportPanel.setControlledLayout(mainPanelLayout);
		//exportPanel.setControlledPanel(mainPanelContainer);
		mainPanelContainer.add(exportPanel, EXPORT_PANEL_NAME);
		
		createPanel = new CreatePanel();
		createPanel.setControlledLayout(mainPanelLayout);
		createPanel.setControlledPanel(mainPanelContainer);
		mainPanelContainer.add(createPanel, CREATE_PANEL_NAME);
		
		exportCardSelectionPanel = new CardSelectionPanel();
		exportCardSelectionPanel.setControlledLayout(mainPanelLayout);
		exportCardSelectionPanel.setControlledPanel(mainPanelContainer);
		exportCardSelectionPanel.setContinueDestination(EXPORT_PANEL_NAME);
		mainPanelContainer.add(exportCardSelectionPanel, EXPORT_SELECTION_PANEL_NAME);

		flashboardPanel = new FlashboardPanel(20);
		JScrollPane scroller = new JScrollPane(flashboardPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanelContainer.add(scroller, FLASHBOARD_PANEL_NAME);
		
		mainPanelLayout.show(mainPanelContainer, FLASHBOARD_PANEL_NAME);
		contentPane.add(mainPanelContainer);
	}
}