package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.json.JSONException;

import quizlet.QuizletCard;
import quizlet.QuizletRequest;
import controller.Controller;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import gui.GuiConstants.TabType;

public class QuizletPanel extends JPanel implements PropertyChangeListener, ComponentListener {

	private ProgressMonitor progressMonitor;
	private ImportThread importThread;

	public QuizletPanel() {
		addComponentListener(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel searchPanel = new JPanel();
		add(searchPanel);

		JButton searchButton = new JButton("Search");
		searchPanel.add(searchButton);

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SearchThread().execute();
			}
		});


		searchTextField = new JTextField();
		searchPanel.add(searchTextField);
		searchTextField.setColumns(10);
		searchTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SearchThread().execute();
			}
		});

		JPanel setNamePanel = new JPanel();
		add(setNamePanel);

		JLabel setNameLabel = new JLabel("Set Name");
		setNamePanel.add(setNameLabel);

		setNameTextField = new JTextField();
		setNamePanel.add(setNameTextField);
		setNameTextField.setColumns(10);

		JLabel lblInterval = new JLabel("Interval");
		setNamePanel.add(lblInterval);

		spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		JSpinner.DefaultEditor editor = ((JSpinner.DefaultEditor) spinner.getEditor());
		editor.getTextField().setColumns(2);
		editor.getTextField().setEditable(false);

		setNamePanel.add(spinner);

		tagPanel = new TagPanel("Set tags: ", true);
		add(tagPanel);

		JPanel tablePanel = new JPanel();
		add(tablePanel);
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));

		setTable = new JTable();
		JScrollPane setScrollPane = new JScrollPane(setTable);
		tablePanel.add(setScrollPane);

		setTable.setDefaultRenderer(Long.class, new PreviewRenderer());
		setTable.setDefaultEditor(Long.class, new PreviewEditor(new JCheckBox()));

		cardTable = new JTable();
		JScrollPane cardScrollPane = new JScrollPane(cardTable);
		tablePanel.add(cardScrollPane);

		cardTable.setDefaultRenderer(QuizletCard.class, new PreviewRenderer());
		cardTable.setDefaultEditor(QuizletCard.class, new PreviewEditor(new JCheckBox()));
		cardTable.setRowSelectionAllowed(false);
		cardTable.setColumnSelectionAllowed(false);
		
		JPanel bottomPanel = new JPanel();
		add(bottomPanel);
		
		btnBack = new JButton("Back");
		bottomPanel.add(btnBack);
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.switchTabs(TabType.IMPORT);
			}
		});

		btnImport = new JButton("Import!");
		bottomPanel.add(btnImport);

		btnImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (setTable.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(btnImport, "You must select some sets to import");
					return;
				}

				System.out.println("Creating monitor");
				progressMonitor = new ProgressMonitor(QuizletPanel.this,
						"Running a Long Task",
						"", 0, 100);
				progressMonitor.setProgress(0);
				progressMonitor.setNote("Hello there");
				progressMonitor.setMillisToDecideToPopup(progressMonitor.getMillisToDecideToPopup()/4);

				//if (importThread != null)
				//	importThread.cancel(true);

				importThread = new ImportThread();
				importThread.addPropertyChangeListener(QuizletPanel.this);
				importThread.execute();

			}

		});
	}

	private static final long serialVersionUID = 1L;
	private JTextField searchTextField;
	private JTable setTable;
	private JTable cardTable;
	private JTextField setNameTextField;
	private JButton btnImport;
	private JButton btnBack;
	private TagPanel tagPanel;
	private JSpinner spinner;

	/*public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.getContentPane().add(new QuizletPanel());
		frame.pack();
		frame.setVisible(true);
	}*/

	class PreviewRenderer implements TableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			return new JButton("Preview");
		}
	}

	class PreviewEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected JButton button;

		public PreviewEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton("Preview");
			button.setOpaque(true);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {

			if (column == 3)  
				new PreviewThread(value.toString()).execute();
			else  
				try {
					Controller.playFlashcardThenRun(((QuizletCard) value).getFlashCard((int) spinner.getValue()));
				} catch (IOException e) {
					Controller.guiMessage("Cannot preview card", true);
				} 
			return button;
		}
	}

	private class SearchThread extends SwingWorker<TableModel, Void> {
		private String searchText;
		SearchThread() {
			this.searchText = searchTextField.getText();
		}

		@Override
		public TableModel doInBackground() {
			return new QuizletSetTableModel(QuizletRequest.search(searchText));
		}

		@Override
		public void done() {
			try {
				setTable.setModel(get());
			} catch (Throwable e) {
				Controller.guiMessage("Quizlet did not respond", true);
			}
		}
	}

	private class ImportThread extends SwingWorker<Void, Void> {

		private int interval;
		private String setName;
		private List<String> tags;
		private List<String> ids;


		ImportThread() {
			ids = new ArrayList<>();
			for (int row: setTable.getSelectedRows()) {
				ids.add(setTable.getValueAt(row, 3).toString());
			}
			tags = tagPanel.getTags();
			setName = "";
			try {
				setName = Controller.parseInput(setNameTextField.getText());
			} catch (Throwable e){};
			if (setName.equals(""))
				try {
					setName = Controller.parseInput((String) setTable.getValueAt(setTable.getSelectedRow(), 0)); 
				} catch (Exception e) {setName = "quizlet";}
			try {
				spinner.commitEdit();
			} catch (ParseException e1) {
				Controller.guiMessage("ERROR: Could not commit spinner edit", true);
				e1.printStackTrace();
			}
			interval = (int) spinner.getValue();

		}

		@Override
		public void done()
		{
			progressMonitor.close();
			Controller.updateAll();
			Controller.requestSetBrowser().updateSourceList();
			if (!isCancelled())
				Controller.guiMessage("Done Importing");
		}

		@Override
		protected Void doInBackground() throws Exception {

			setProgress(0);
			List<FlashCard> producedCards = new ArrayList<>();

			SerializableFlashCard.Data data;
			int cardsDone = 0;
			for (String id : ids) {
				try {
					QuizletCard[] cards = QuizletCard.getCards(QuizletRequest.getSet(id));
					for (QuizletCard card: cards) {
						if (isCancelled())
							return null;

						if (card.term.length() == 0 || card.definition.length() == 0)
							continue;

						data = new SerializableFlashCard.Data();
						data.name = Controller.parseCardName(card.term);
						data.setQuestion(Controller.readTTS(card.term));
						data.questionText = card.definition;
						data.setAnswer(Controller.readTTS(card.definition));
						data.answerText = card.definition;
						data.interval = interval;
						data.tags = new ArrayList<>();
						data.pathToFile = SerializableFlashCard.makeFlashCardPath(data);

						// add the cards to a temporary place before writing them. If the user cancels, we don't want a half-full set in our db
						producedCards.add(Controller.createCard(data));

						cardsDone++;
						int progress = Math.min(cardsDone * 100 /(cards.length * ids.size()), 99);
						System.out.println("progress is " + progress);
						setProgress(progress);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Download is complete, write the cards.	
			
			FlashCardSet set = Controller.generateNewSet(setName, "quizlet", tags, interval);
			for (FlashCard f : producedCards)
				set.addCard(f);
			return null;
		}
	}

	private class PreviewThread extends SwingWorker<TableModel, Void> {

		private String id;

		PreviewThread(String id) {
			this.id = id;
		}

		@Override
		public TableModel doInBackground() {
			try {
				return new QuizletCardTableModel(QuizletRequest.getSet(id));
			} catch (JSONException e) {
				return null;
			}
		}

		@Override
		public void done() {
			try {
				cardTable.setModel(get());
			} catch (Throwable e) {
				Controller.guiMessage("Quizlet did not respond", true);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("event");
		if ("progress".equals(evt.getPropertyName()) && progressMonitor != null) {
			System.out.println("Setting progress");
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message =
					String.format("Completed %d%%.\n", progress);
			progressMonitor.setNote(message);
			if (progressMonitor.isCanceled() || importThread.isDone()) {
				importThread.cancel(true);
				progressMonitor.close();
			}
		}	
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		if (progressMonitor != null)
			progressMonitor.close();
		if (importThread != null)
			importThread.cancel(true);

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

}
