package gui;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ControlScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	/**
	 * A JScrollPane that will bubble a mouse wheel scroll event to the parent 
	 * JScrollPane if one exists when this scrollpane either tops out or bottoms out.
	 */
	public ControlScrollPane() {
		super();
		addMouseWheelListener(new PDMouseWheelListener());
	}
	
	public ControlScrollPane(JComponent component) {
		super(component);
		addMouseWheelListener(new PDMouseWheelListener());
	}

	class PDMouseWheelListener implements MouseWheelListener {

		private JScrollBar bar;
		private int previousValue = 0;
		private JScrollPane parentScrollPane; 

		private JScrollPane getParentScrollPane() {
			if (parentScrollPane == null) {
				Component parent = getParent();
				while (!(parent instanceof JScrollPane) && parent != null) {
					parent = parent.getParent();
				}
				parentScrollPane = (JScrollPane)parent;
			}
			return parentScrollPane;
		}

		public PDMouseWheelListener() {
			bar = ControlScrollPane.this.getVerticalScrollBar();
		}
		public void mouseWheelMoved(MouseWheelEvent e) {
			JScrollPane parent = getParentScrollPane();
			if (parent != null) {
				/*
				 * Only dispatch if we have reached top/bottom on previous scroll
				 */
				 if (e.getWheelRotation() < 0) {
					 if (bar.getValue() == 0 && previousValue == 0) {
						 parent.dispatchEvent(cloneEvent(e));
					 }
				 } else {
					 if (bar.getValue() == getMax() && previousValue == getMax()) {
						 parent.dispatchEvent(cloneEvent(e));
					 }
				 }
				 previousValue = bar.getValue();
			}
			/* 
			 * If parent scrollpane doesn't exist, remove this as a listener.
			 * We have to defer this till now (vs doing it in constructor) 
			 * because in the constructor this item has no parent yet.
			 */
			else {
				ControlScrollPane.this.removeMouseWheelListener(this);
			}
		}
		private int getMax() {
			return bar.getMaximum() - bar.getVisibleAmount();
		}
		private MouseWheelEvent cloneEvent(MouseWheelEvent e) {
			return new MouseWheelEvent(getParentScrollPane(), e.getID(), e
					.getWhen(), e.getModifiers(), 1, 1, e
					.getClickCount(), false, e.getScrollType(), e
					.getScrollAmount(), e.getWheelRotation());
		}
	}
}