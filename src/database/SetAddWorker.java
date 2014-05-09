package database;

import java.util.List;

import javax.swing.SwingWorker;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

/**
 * Adds a bunch of cards to a set (that's probably on disk) and returns a progress thing
 * Performs all DB operations in a separate thread so GUI doesn't hang up
 * 
 * @author puppyofkosh
 *
 */
public class SetAddWorker extends SwingWorker<Void, Void>{

	private FlashCardSet set;
	private List<FlashCard> cards;
	
	/**
	 * Initialize (in Event Dispatch Thread)
	 * @param s
	 * @param cards
	 */
	public SetAddWorker(FlashCardSet s, List<FlashCard> cards)
	{
		this.cards = cards;
		set = s;
		
	}
	
	/**
	 * Add the cards to the set (performed in worker thread)
	 */
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
