package ian_gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
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
		setBounds(100, 100, 653, 608);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		
		JPanel sidePanelContainer = new JPanel();
		sidePanelContainer.setPreferredSize(new Dimension(getWidth()/3, getHeight()));
		contentPane.add(sidePanelContainer);
		sidePanelContainer.setLayout(new GridLayout(0, 1, 0, 0));
		SidePanel sidePanel = new SidePanel();
		sidePanelContainer.add(sidePanel);
		
		JPanel mainPanelContainer = new JPanel();
		mainPanelContainer.setPreferredSize(new Dimension(2*getWidth()/3, getHeight()));
		mainPanelContainer.setLayout(new BoxLayout(mainPanelContainer, BoxLayout.X_AXIS));
		mainPanelContainer.add(new ImportPanel());
		contentPane.add(mainPanelContainer);
	}

}
