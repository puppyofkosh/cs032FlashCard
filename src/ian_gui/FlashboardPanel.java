package ian_gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FlashboardPanel extends JPanel{
	
	public FlashboardPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel defaultText = new JLabel("Maybe show some statistics about the users usage here or something");
		add(defaultText);	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
