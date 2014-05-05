package gui;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONException;

import quizlet.QuizletSet;

public class QuizletSetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private JSONArray data;

	public QuizletSetTableModel(JSONArray data) {
		this.data = data;
	}
	

	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.length();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		try {
			QuizletSet set = new QuizletSet(data.getJSONObject(arg0));
			switch(arg1) {
			case 0:
				return set;
			case 1:
				return set.author;
			case 2:
				return set.terms;
			case 3:
				return set.id;
			default :
				throw new IndexOutOfBoundsException();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String getColumnName(int index) {
		switch(index) {
		case 0:
			return "Name";
		case 1:
			return "Author";
		case 2:
			return "Terms";
		default :
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
	}

}
