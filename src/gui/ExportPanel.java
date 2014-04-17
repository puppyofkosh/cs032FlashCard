package gui;

import java.awt.Color;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import audio.WavFileConcatenator;

import flashcard.FlashCard;
import flashcard.SimpleFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ExportPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CardTablePanel _cardTable;
	/**
	 * Create the panel.
	 */
	public ExportPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel chooseMethodPanel = new JPanel();
		add(chooseMethodPanel);
		
		JTextField searchBox = new JTextField(30);
		searchBox.setForeground(Color.LIGHT_GRAY);
		searchBox.setText("Search Here");
		searchBox.setForeground(Color.BLACK);
		chooseMethodPanel.add(searchBox);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("MP3");
		chooseMethodPanel.add(rdbtnNewRadioButton_1);
		
		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Network");
		chooseMethodPanel.add(rdbtnNewRadioButton_2);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("iTunes");
		chooseMethodPanel.add(rdbtnNewRadioButton);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNewRadioButton_1);
		group.add(rdbtnNewRadioButton_2);
		group.add(rdbtnNewRadioButton);

		
		JButton btnExport = new JButton("Export!");
		btnExport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Export button clicked
				
				// FIXME: This should be run in a separate thread. The code should probably look like:
				// controller.exportCards(_cardTable.getSelectedCards());
				// For now, this will be ok
				List<FlashCard> toExport = _cardTable.getSelectedCards();
				System.out.println("Exporting " + toExport);
				for (FlashCard f : toExport)
				{
					try {
						WavFileConcatenator concat = new WavFileConcatenator("export/" + f.getName());
						concat.concatenate(f);						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				System.out.println("Exporting");
				
			}
		});
		chooseMethodPanel.add(btnExport);
		
		_cardTable = new CardTablePanel();
		add(_cardTable);
	}
	
	
	public void update() {
		_cardTable.updateCards(SimpleFactory.getResources().getAllCards());
	}
	
	public void update(List<FlashCard> cards) {
		_cardTable.updateCards(cards);
	}

}
