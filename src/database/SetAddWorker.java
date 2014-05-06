package database;

import java.util.List;

import javax.swing.SwingWorker;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Adds a bunch of cards to a set (that's probably on disk) and returns a progress thing
 * @author puppyofkosh
 *
 */
public class SetAddWorker extends SwingWorker<Void, Void>{

	private FlashCardSet set;
	private List<FlashCard> cards;
	
	public SetAddWorker(FlashCardSet s, List<FlashCard> cards)
	{
		this.cards = cards;
		set = s;
		
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		int progress = 0;
		for (FlashCard c : cards)
		{
			set.addCard(c);
			progress++;
			setProgress((int)(100.0 * progress / cards.size()));
			
			if (isCancelled())
			{
				System.out.println("cancelled");	

				for (FlashCard f : set.getAll())
				{
					set.removeCard(f);
					System.out.println("Removing " + f);	
				}
				
				return null;
			}
		}
		
		return null;
	}

}
