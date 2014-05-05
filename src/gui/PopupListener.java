package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 * This can be constructed with varargs JMenuItems and added as a MouseListener
 * to any component to give it a right-click menu
 * @author Peter
 *
 */
public class PopupListener implements MouseListener {

	JMenuItem[] items;

	public PopupListener(JMenuItem...items) {
		this.items = items;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		popup(arg0);

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		popup(arg0);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		popup(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		popup(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		popup(arg0);
	}

	private void popup(MouseEvent e) {
		if (!e.isPopupTrigger())
			return;
		JPopupMenu menu = new JPopupMenu();
		for (JMenuItem item: items) {
			menu.add(item);
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}
