package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.json.JSONException;
import org.json.JSONObject;

import controller.Controller;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import quizlet.QuizletCard;
import quizlet.QuizletRequest;

import javax.swing.JSpinner;

public class QuizletPanel extends JPanel {
	public QuizletPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel searchPanel = new JPanel();
		add(searchPanel);
		
		JButton searchButton = new JButton("Search");
		searchPanel.add(searchButton);
		
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SearchThread().start();
			}
		});
		
		
		searchTextField = new JTextField();
		searchPanel.add(searchTextField);
		searchTextField.setColumns(10);
		
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
		
		JPanel bottomPanel = new JPanel();
		add(bottomPanel);
		
		btnImport = new JButton("Import!");
		bottomPanel.add(btnImport);
		
		btnImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (setTable.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(btnImport, "You must select some sets to import");
					return;
				}
				new ImportThread().start();
			}
			
		});
	}

	private static final long serialVersionUID = 1L;
	private JTextField searchTextField;
	private JTable setTable;
	private JTable cardTable;
	private JTextField setNameTextField;
	private JButton btnImport;
	private TagPanel tagPanel;
	private JSpinner spinner;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.getContentPane().add(new QuizletPanel());
		frame.pack();
		frame.setVisible(true);
	}

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
			  
			new PreviewThread(value.toString()).start();
			  
			return button;
		}
	}
	
	private class SearchThread extends Thread {
		private String searchText;
		SearchThread() {
			this.searchText = searchTextField.getText();
		}
		
		@Override
		public void run() {
			setTable.setModel(new QuizletSetTableModel(QuizletRequest.search(searchText)));
		}
	}
	
	private class ImportThread extends Thread {
		
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
			setName = Controller.parseCardName(setNameTextField.getText());
			
			try {
				spinner.commitEdit();
			} catch (ParseException e1) {
				Controller.guiMessage("ERROR: Could not commit spinner edit", true);
				e1.printStackTrace();
			}
			interval = (int) spinner.getValue();
		}
		
		@Override
		public void run() {
			SerializableFlashCard.Data data;
			FlashCardSet set = Controller.createSet(setName, "quizlet", tags, interval);
			for (String id : ids) {
				
				try {
					for (QuizletCard card: QuizletCard.getCards(QuizletRequest.getSet(id))) {
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
						set.addCard(Controller.createCard(data));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
	}
	
	private class PreviewThread extends Thread {
		
		private String id;
		
		PreviewThread(String id) {
			this.id = id;
		}
		
		@Override
		public void run() {
			try {
				cardTable.setModel(new QuizletCardTableModel(QuizletRequest.getSet(id)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
