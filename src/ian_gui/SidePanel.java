package ian_gui;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SidePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public SidePanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel navigation = new JPanel();
		add(navigation);
		navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
		
		JPanel flashboardButtonPanel = new JPanel();
		navigation.add(flashboardButtonPanel);
		flashboardButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnFlashboard = new JButton("Flashboard");
		flashboardButtonPanel.add(btnFlashboard);
		
		JPanel exportButtonPanel = new JPanel();
		navigation.add(exportButtonPanel);
		exportButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnExport = new JButton("Export");
		exportButtonPanel.add(btnExport);
		
		JPanel importButtonPanel = new JPanel();
		navigation.add(importButtonPanel);
		importButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnImport = new JButton("Import");
		importButtonPanel.add(btnImport);
		
		JPanel createButtonPanel = new JPanel();
		navigation.add(createButtonPanel);
		createButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnCreate = new JButton("Create");
		createButtonPanel.add(btnCreate);
		
		JPanel panel = new JPanel();
		add(panel);

	}

}
