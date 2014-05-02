package gui;

import java.util.List;

import protocol.NetworkedFlashCard;

import flashcard.FlashCard;

/**
 * TODO: This should probably be two separate interfaces
 * @author puppyofkosh
 *
 */
public interface ClientFrontend {
	
	void guiMessage(String msg, int duration);
	void guiMessage(String msg);
	void updateLocallyStoredCards(List<FlashCard> flashcards);
	void updateCardsForImport(List<NetworkedFlashCard> flashcards);

	void displayConnectionStatus(boolean connected);
}