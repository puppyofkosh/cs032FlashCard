package gui;

import javax.swing.table.AbstractTableModel;

import org.json.JSONException;
import org.json.JSONObject;

import quizlet.QuizletCard;

public class QuizletCardTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private QuizletCard[] data;
	
	public QuizletCardTableModel(JSONObject data) throws JSONException {
		this.data = QuizletCard.getCards(data);
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		QuizletCard card = data[arg0];
		switch(arg1) {
		case 0:
			return card.term;
			
		case 1:
			return card.definition;
			
		case 2:
			return card;
			
		default :
			throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	
	@Override
	public String getColumnName(int index) {
		switch(index) {
		case 0:
			return "Term";
		case 1:
			return "Definition";
		case 2:
			return "";
		default :
			throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 2;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
	}
	
}
