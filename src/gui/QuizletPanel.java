package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.json.JSONException;
import org.json.JSONObject;

import quizlet.QuizletRequest;

public class QuizletPanel extends JPanel {
	public QuizletPanel() {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel searchPanel = new JPanel();
		add(searchPanel);
		
		JButton searchButton = new JButton("Search");
		searchPanel.add(searchButton);
		
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setTable.setModel(new QuizletSetTableModel(QuizletRequest.search(searchTextField.getText())));
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
		
		TagPanel tagPanel = new TagPanel("Set tags: ", true);
		add(tagPanel);
		
		JPanel tablePanel = new JPanel();
		add(tablePanel);
		
		setTable = new JTable();
		JScrollPane setScrollPane = new JScrollPane(setTable);
		tablePanel.add(setScrollPane);
		
		setTable.setDefaultRenderer(Long.class, new PreviewRenderer());
		setTable.setDefaultEditor(Long.class, new PreviewEditor(new JCheckBox()) {
			
		});
		
		//setTable.
		
		cardTable = new JTable();
		JScrollPane cardScrollPane = new JScrollPane(cardTable);
		tablePanel.add(cardScrollPane);
	}

	private static final long serialVersionUID = 1L;
	private JTextField searchTextField;
	private JTable setTable;
	private JTable cardTable;
	private JTextField setNameTextField;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.add(new QuizletPanel());
		frame.setVisible(true);
	}

	class PreviewRenderer implements TableCellRenderer {

		  public Component getTableCellRendererComponent(JTable table, Object value,
		      boolean isSelected, boolean hasFocus, int row, int column) {
		      return new JButton("Preview");
		  }
	}

	class PreviewEditor extends DefaultCellEditor {
		protected JButton button;

		public PreviewEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton("Preview");
			button.setOpaque(true);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column) {
			  
			try {
				cardTable.setModel(new QuizletCardTableModel(QuizletRequest.getSet(value.toString())));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			return button;
		}
	}
	
}
