package gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONException;

import quizlet.QuizletRequest;
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
		return 4;
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
			return set.name;
			
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
			return "name";
			
		case 1:
			return "author";
		
		case 2:
			return "number of terms";
		case 3:
			return "preview";
			
		default :
			throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 3;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
	}

}
