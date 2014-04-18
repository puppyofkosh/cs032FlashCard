package gui;

import java.util.List;

import flashcard.FlashCard;

public interface ClientFrontend {
	
	void guiMessage(String msg, int duration);
	void guiMessage(String msg);
	void update(List<FlashCard> flashcards);
	void displayConnectionStatus(boolean connected);
}