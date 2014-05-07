package gui;

/***
 * Any pane that wants to use the set browser should implement this
 * @author puppyofkosh
 *
 */
public interface Browsable {

	public void showSetBrowser(SetBrowser browser);
	public void removeSetBrowser();
}
