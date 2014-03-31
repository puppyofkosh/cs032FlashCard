package ian_gui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JRadioButton;

public class ExportPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ExportPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JScrollPane cardListPanel = new JScrollPane();
		add(cardListPanel);
		
		JPanel exportPanel = new JPanel();
		add(exportPanel);
		exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.X_AXIS));
		
		JPanel chooseMethodPanel = new JPanel();
		exportPanel.add(chooseMethodPanel);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("MP3");
		chooseMethodPanel.add(rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Network");
		chooseMethodPanel.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("iTunes");
		chooseMethodPanel.add(rdbtnNewRadioButton);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNewRadioButton_1);
		group.add(rdbtnNewRadioButton_2);
		group.add(rdbtnNewRadioButton);
		
		JPanel drawThingPanel = new JPanel();
		exportPanel.add(drawThingPanel);
		
		JButton btnExport = new JButton("Export!");
		drawThingPanel.add(btnExport);
		
	}

}
