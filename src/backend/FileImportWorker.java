package backend;

import javax.swing.SwingWorker;

/**
 * Run when we want to import from TSV.
 * 
 * Performs the TSV import in a worker thread, and in the event dispatch thread,
 * runs the given runnables back in the event dispatch thread
 * 
 * @author puppyofkosh
 * 
 */
public class FileImportWorker extends SwingWorker<Void, Void> {

	Runnable[] toRun;
	FileImporter importer;

	public FileImportWorker(FileImporter importer, Runnable... toRun) {
		this.toRun = toRun;
		this.importer = importer;
	}

	@Override
	public void done() {
		for (Runnable r : toRun)
			r.run();
	}

	@Override
	protected Void doInBackground() throws Exception {
		importer.importCards();
		
		return null;
	}
}
