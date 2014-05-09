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
	
	void clientMessage(String msg, int duration);
	void clientMessage(String msg);
	void updateLocallyStoredCards(List<FlashCard> flashcards);
	void updateCardsForImport(List<NetworkedFlashCard> flashcards);

	void displayConnectionStatus(boolean connected);
}