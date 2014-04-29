package database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

public class DatabaseSet implements FlashCardSet{

	private int id;
	private FlashCardDatabase database;
	
	public DatabaseSet(int id, FlashCardDatabase db)
	{
		this.id = id;
		this.database = db;
	}
	
	@Override
	public String getName() {
		try {
			ResultSet rs = database.getStatement().executeQuery(
					"SELECT NAME FROM SETS WHERE ID=" + id);
			if (rs.next() == false) {
				System.out.println("BAD SET ID!");
				return "";
			}
			return rs.getString("NAME");

		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public Collection<FlashCard> getAll() throws IOException {
		List<FlashCard> cards = new ArrayList<FlashCard>();

		try {
			String query = "SELECT FLASHCARD_ID FROM SETS_FLASHCARDS WHERE SET_ID=" + id;
			ResultSet rs = database.getStatement().executeQuery(query);
			
			while (rs.next())
			{
				cards.add(new DatabaseFlashCard(rs.getInt("FLASHCARD_ID"), database));
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cards;
	}

	@Override
	public void addTag(String tag) throws IOException {
		try {
			Integer tagId = database.getTagId(tag);

			// If result set is empty (tag does not yet exist in TAGS table)
			if (tagId == null) {
				// make a tag with the given name
				database.makeTag(tag);

				tagId = database.getTagId(tag);
			}

			// Now we should be guaranteed to get some results
			if (tagId == null)
				throw new SQLException(
						"Illegal state of database-could not add tag");

			database.addTagToSet(id, tagId);
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void removeTag(String tag) throws IOException {
		try
		{
			database.removeTagFromSet(id, database.getTagId(tag));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Collection<String> getTags() {
		List<String> tags = new ArrayList<>();
		try {
			String query = "SELECT NAME FROM "
					+ "(SELECT TAG_ID FROM SETS_TAGS WHERE SET_ID="
					+ id + ")" + "INNER JOIN TAGS ON TAGS.ID=TAG_ID";
			ResultSet rs = database.getStatement().executeQuery(query);
			
			while (rs.next())
				tags.add(rs.getString("NAME"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags;
	}

	@Override
	public void addCard(FlashCard f) throws IOException {
		try
		{
			DatabaseFlashCard card = database.writeCard(f);
			database.addCardToSet(card.getId(), id);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

}
