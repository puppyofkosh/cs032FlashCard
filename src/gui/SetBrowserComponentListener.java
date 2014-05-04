package gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import controller.Controller;

public class SetBrowserComponentListener extends ComponentAdapter{
	private Browsable browser;
	public SetBrowserComponentListener(Browsable b)
	{
		browser = b;
	}
	
	@Override
	public void componentShown(ComponentEvent arg0) {
		browser.showSetBrowser(Controller.requestSetBrowser());
	}
}
