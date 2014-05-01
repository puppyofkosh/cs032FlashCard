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
		return 2;
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
			
		default :
			throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public String getColumnName(int index) {
		switch(index) {
		case 0:
			return "term";
			
		case 1:
			return "definition";
		
		default :
			throw new IndexOutOfBoundsException();
		}
	}
}
