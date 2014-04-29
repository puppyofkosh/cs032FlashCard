package gui;

import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Pretty much all panels in this program should extend this. Provides some extra stuff to avoid boilerplate
 * -gives access to the main layout and panel so we can easily switch screens/views
 * @author puppyofkosh
 *
 */
public abstract class GenericPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	// When the user presses a button, we use this layout to
	// show the right screen
	protected CardLayout controlledLayout;
	protected JPanel controlledPanel;
	
	GenericPanel() {
		super(new FlowLayout(FlowLayout.CENTER, 0, 0));
		setBorder(BorderFactory.createEmptyBorder());
	}
	

	public CardLayout getControlledLayout() {
		return controlledLayout;
	}

	public void setControlledLayout(CardLayout controlledLayout) {
		this.controlledLayout = controlledLayout;
	}

	public JPanel getControlledPanel() {
		return controlledPanel;
	}

	public void setControlledPanel(JPanel controlledPanel) {
		this.controlledPanel = controlledPanel;
	}
}
