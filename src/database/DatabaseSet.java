package database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public String toString()
	{
		return getName();
	}
	
	@Override
	public String getName() {
		try (Statement statement = database.getStatement()){
			ResultSet rs = statement.executeQuery(
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

		try (Statement statement = database.getStatement()){
			String query = "SELECT FLASHCARD_ID FROM SETS_FLASHCARDS WHERE SET_ID=" + id;
			ResultSet rs = statement.executeQuery(query);
			
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
		Set<String> tags = new HashSet<>();
		try (Statement statement = database.getStatement()){
			String query = "SELECT NAME FROM "
					+ "(SELECT TAG_ID FROM SETS_TAGS WHERE SET_ID="
					+ id + ")" + "INNER JOIN TAGS ON TAGS.ID=TAG_ID";
			ResultSet rs = statement.executeQuery(query);
			
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
			
			if (card != null)
			{
				database.addCardToSet(card.getLocalId(), id);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}
	
	@Override
	public int getInterval()
	{
		try (Statement statement = database.getStatement()){
			String query = "SELECT INTERVAL FROM SETS WHERE ID=" + id;
			ResultSet rs = statement.executeQuery(query);
			
			if (!rs.next())
			{
				System.out.println("BAD SET ID "+  id);
			}
			
			return rs.getInt("INTERVAL");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public String getAuthor()
	{
		try (Statement statement = database.getStatement()){
			String query = "SELECT AUTHOR FROM SETS WHERE ID=" + id;
			ResultSet rs = statement.executeQuery(query);
			
			if (!rs.next())
			{
				System.out.println("BAD SET ID "+  id);
			}
			
			return rs.getString("AUTHOR");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void addAuthor() {
		// TODO Auto-generated method stub
		throw new IllegalStateException("What is this method supposed to do?");
	}

	@Override
	public void setTags(List<String> tags) throws IOException {
		try (Statement statement = database.getStatement()){
			
			// Remove all current tags
			String deleteQuery = "DELETE FROM SETS_TAGS WHERE SET_ID=" + id;
			statement.execute(deleteQuery);	
			
			// Add the new tags
			for (String tag : tags)
				addTag(tag);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void setAuthor(String author) {
		try (Statement statement = database.getStatement()){
			String query = "UPDATE SETS\n" +
							"SET AUTHOR='" + author + "'\n" +
							"WHERE ID=" + id;
			statement.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setInterval(int interval) {
		try (Statement statement = database.getStatement()){
			String query = "UPDATE SETS\n" +
							"SET INTERVAL=" + interval + "\n" +
							"WHERE ID=" + id;
			statement.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Override
	public boolean sameMetaData(FlashCardSet s)
	{	
		// Make sure both sets have the same tags
		boolean tagEquality = new HashSet<>(getTags()).equals(new HashSet<>(s.getTags()));
		
		return (tagEquality && s.getAuthor().equals(getAuthor()) && s.getName().equals(getName()) && s.getInterval() == getInterval());
	}

	@Override
	public void removeCard(FlashCard f) throws IOException {
		// TODO Auto-generated method stub
		//FIXME IAN
	}

}
