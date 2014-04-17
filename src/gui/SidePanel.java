package gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SidePanel extends GenericPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final MainFrame _parent;
	
	/**
	 * Set up the swing components
	 */
	public void initialize()
	{

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel navigation = new JPanel();
		add(navigation);
		navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
		
		JPanel flashboardButtonPanel = new JPanel();
		navigation.add(flashboardButtonPanel);
		flashboardButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnFlashboard = new JButton("Flashboard");
		flashboardButtonPanel.add(btnFlashboard);
		btnFlashboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, MainFrame.FLASHBOARD_PANEL_NAME);
				update();

			}
		});
		
		JPanel exportButtonPanel = new JPanel();
		navigation.add(exportButtonPanel);
		exportButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, MainFrame.EXPORT_PANEL_NAME);
				update();
			}
		});
		exportButtonPanel.add(btnExport);
		
		JPanel importButtonPanel = new JPanel();
		navigation.add(importButtonPanel);
		importButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnImport = new JButton("Import");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, MainFrame.IMPORT_PANEL_NAME);
				update();

			}
		});
		importButtonPanel.add(btnImport);
		
		JPanel createButtonPanel = new JPanel();
		navigation.add(createButtonPanel);
		createButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controlledLayout.show(controlledPanel, MainFrame.CREATE_PANEL_NAME);
				update();
			}
		});
		createButtonPanel.add(btnCreate);
		
		JPanel panel = new JPanel();
		add(panel);
	}
	
	private void update() {
		if (_parent != null) _parent.updateAll();
	}
	
	/**
	 * Create the panel.
	 */
	public SidePanel(MainFrame parent) {
		_parent = parent;
		initialize();
		
	}


}
