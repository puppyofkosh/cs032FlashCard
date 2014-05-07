package gui;

import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import gui.GuiConstants.TabType;

import java.awt.Component;
import java.awt.Font;
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
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.json.JSONArray;
import org.json.JSONException;

import quizlet.QuizletCard;
import quizlet.QuizletRequest;
import quizlet.QuizletSet;
import controller.Controller;

public class QuizletPanel extends JPanel implements PropertyChangeListener, ComponentListener {

	private ProgressMonitor progressMonitor;
	private ImportThread importThread;
	private static final long serialVersionUID = 1L;
	private JTextField searchTextField;
	private JTable setTable;
	private JTable cardTable;
	private JTextField setNameTextField;
	private JButton btnImport;
	private JButton btnBack;
	private TagPanel tagPanel;
	private JSpinner spinner;

	private void performSearch()
	{
		long setId = 0;
		try
		{
			setId = Long.parseLong(searchTextField.getText());
		}
		catch (NumberFormatException e)
		{
			new SearchThread().execute();
			return;
		}
		
		// If they entered a set number, we search for that
		new PreviewThread("" + setId).execute();
		setTable.setModel(new QuizletSetTableModel(new JSONArray()));
	}
	
	public QuizletPanel() {
		addComponentListener(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		setOpaque(false);
		JPanel searchPanel = new JPanel();
		searchPanel.setOpaque(false);
		add(searchPanel);

		searchTextField = new JTextField();
		searchPanel.add(searchTextField);
		searchTextField.setColumns(10);
		searchTextField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				performSearch();
			}
		});

		JButton searchButton = new JButton("Search Quizlet");
		searchPanel.add(searchButton);

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				performSearch();
			}
		});


		JPanel setNamePanel = new JPanel();
		setNamePanel.setOpaque(false);
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
		tablePanel.setOpaque(false);
		add(tablePanel);
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.X_AXIS));

		setTable = new JTable();
		setTable.setBackground(GuiConstants.SET_TAG_COLOR);
		setTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	if (setTable.getSelectedRow() == -1)
	        		return;
	        	
	            new PreviewThread(String.valueOf(((QuizletSet) setTable.getValueAt(setTable.getSelectedRow(), 0)).id)).execute();
	        
	        }
	    });
		setTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setTable.setRowSelectionAllowed(true);
		setTable.setDefaultRenderer(QuizletSet.class, new QuizletSetRenderer());

		
		JScrollPane setScrollPane = new JScrollPane(setTable);
		setScrollPane.getViewport().setBackground(GuiConstants.SET_TAG_COLOR);
		setScrollPane.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(),"Sets from Quizlet Search",
				TitledBorder.LEFT, TitledBorder.TOP, new Font(Font.MONOSPACED, Font.PLAIN, 20), GuiConstants.PRIMARY_FONT_COLOR));
		tablePanel.add(setScrollPane);

		cardTable = new JTable();
		cardTable.setBackground(GuiConstants.CARD_TAG_COLOR);
		JScrollPane cardScrollPane = new JScrollPane(cardTable);
		cardScrollPane.getViewport().setBackground(GuiConstants.CARD_TAG_COLOR);
		cardScrollPane.setBorder(new TitledBorder(BorderFactory.createEmptyBorder(),"Cards from Quizlet Set",
				TitledBorder.LEFT, TitledBorder.TOP, new Font(Font.MONOSPACED, Font.PLAIN, 20), GuiConstants.PRIMARY_FONT_COLOR));

		tablePanel.add(cardScrollPane);

		cardTable.setDefaultRenderer(QuizletCard.class, new PreviewRenderer("Play"));
		cardTable.setDefaultEditor(QuizletCard.class, new PreviewEditor(new JCheckBox()));
		cardTable.setRowSelectionAllowed(false);
		cardTable.setColumnSelectionAllowed(false);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setOpaque(false);
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
				// Either no sets have been selected or no cards are in the right pane
				if (setTable.getSelectedRowCount() == 0 && cardTable.getRowCount() == 0 ||
						// OR there are cards in the right pane (from a previous preview) but none selected in the left pane
						(setTable.getRowCount() > 0 && cardTable.getRowCount() > 0 && setTable.getSelectedRow() == -1)) {
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

				List<String> ids = new ArrayList<>();
				for (int row: setTable.getSelectedRows()) {
					ids.add(String.valueOf(((QuizletSet) setTable.getValueAt(row, 0)).id));
				}
				if (ids.size() == 0)
					ids.add(searchTextField.getText());
				
				importThread = new ImportThread(ids);
				importThread.addPropertyChangeListener(QuizletPanel.this);
				importThread.execute();

			}

		});
	}

	/*public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.getContentPane().add(new QuizletPanel());
		frame.pack();
		frame.setVisible(true);
	}*/

	class PreviewRenderer implements TableCellRenderer {

		String _text;

		PreviewRenderer(String text) {
			_text = text;
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			return new JButton(_text);
		}
	}
	
	class QuizletSetRenderer extends DefaultTableCellRenderer {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object o) {
			setText(((QuizletSet) o).name);
		}
	}

	class PreviewEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected JButton button;
		
		private FlashCard cardClicked = null;

		public PreviewEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton("Previewing");
			button.setOpaque(true);
			button.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent evt) {
					try
					{
						if (cardClicked != null && Controller.getCurrentlyPlayingFlashCard() != cardClicked)
						{
							button.setText("Pause");
							Controller.playFlashcardThenRun(cardClicked);
						}
						else if (cardClicked != null)
						{
							button.setText("Play");
							Controller.stopAudio();
						}
					}
					catch (IOException e)
					{
						Controller.guiMessage("Can't play card");
					}
				}
				
			});
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			System.out.println("selected");
			
			if (column == 3)
			{
				System.out.println("Column 3");
				new PreviewThread(value.toString()).execute();
			}
			else  
			{
				cardClicked = ((QuizletCard) value).getFlashCard((int) spinner.getValue());
			}
			return button;
		}
	}

	private class SearchThread extends SwingWorker<TableModel, Void> {
		private String searchText;
		public SearchThread() {
			this.searchText = searchTextField.getText();
		}

		@Override
		public TableModel doInBackground() {
			JSONArray result;
			try {
				result = QuizletRequest.search(searchText);
				if (result == null)
					return null;
				return new QuizletSetTableModel(result);
			} catch (JSONException | IOException e) {
				return null;
			}
		}

		@Override
		public void done() {
			
			TableModel result;
			try {
				result = get();
				if (result != null)
					setTable.setModel(result);
				else
					Controller.guiMessage("Quizlet did not respond");
			} catch (InterruptedException | ExecutionException e) {
			}
		}
	}

	private class ImportThread extends SwingWorker<Boolean, Void> {

		private int interval;
		private String setName;
		private List<String> tags;
		private List<String> ids;


		ImportThread(List<String> ids) {
			this.ids = ids;

			tags = tagPanel.getTags();
			setName = "";
			try {
				setName = Controller.parseInput(setNameTextField.getText());
			} catch (IOException e){};
			if (setName.equals("") && setTable.getSelectedRow() != -1)
			{
				try {
					QuizletSet set = (QuizletSet) setTable.getValueAt(setTable.getSelectedRow(), 0);
					setName = Controller.parseInput(set.name);
				} catch (IOException e) {}
			}
			
			if (setName.equals(""))
				setName = "Quizlet Set";
			
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
			try {
				Boolean result = get();
				
				if (result != null && result == true)
					Controller.guiMessage("Done Importing");	
				else
					Controller.guiMessage("Error importing. Partial set created");
			} catch (InterruptedException | ExecutionException e) {
				
			}
			
			progressMonitor.close();
			Controller.updateGUI(Controller.getCurrentTab());
		}

		@Override
		protected Boolean doInBackground() throws Exception {

			setProgress(0);
			List<FlashCard> producedCards = new ArrayList<>();

			SerializableFlashCard.Data data;
			int cardsDone = 0;
			for (String id : ids) {
				try {
					QuizletCard[] cards = QuizletCard.getCards(QuizletRequest.getSet(id));
					for (QuizletCard card: cards) {
						if (isCancelled())
							return false;

						if (card.term.length() == 0 || card.definition.length() == 0)
							continue;

						data = new SerializableFlashCard.Data();
						try
						{
							data.name = Controller.parseInput(card.term);
						}
						catch (IOException e)
						{
							data.name = "Quizlet Card";
						}
						
						data.setQuestion(Controller.readTTS(card.term));
						data.questionText = card.definition;
						data.setAnswer(Controller.readTTS(card.definition));
						data.answerText = card.definition;
						data.interval = interval;
						data.tags = card.generatedTags();
						data.pathToFile = SerializableFlashCard.makeFlashCardPath(data);

						// add the cards to a temporary place before writing them. If the user cancels, we don't want a half-full set in our db
						producedCards.add(Controller.createCard(data));

						cardsDone++;
						int progress = Math.min(cardsDone * 100 /(cards.length * ids.size()), 99);
						System.out.println("progress is " + progress);
						setProgress(progress);
					}
				} catch (JSONException ignore) {
					return false;
				}
			}

			// Download is complete, write the cards.	

			FlashCardSet set = Controller.createNewSet(setName, "Quizlet Set", tags, interval);
			for (FlashCard f : producedCards)
				set.addCard(f);
			return true;
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
			TableModel model;
			try {
				model = get();
				
				if (model == null)
				{
					Controller.guiMessage("Quizlet did not respond", true);
					return;
				}
					
				cardTable.setModel(model);
			} catch (InterruptedException | ExecutionException e) {
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
