package database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import utils.FlashcardConstants;
import audio.AudioFile;
import audio.DiscAudioFile;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

/***
 * Stores the database ID of a flashcard. All methods just perform queries on
 * the DB
 * 
 * @author puppyofkosh
 * 
 */
public class DatabaseFlashCard implements FlashCard {

	private int id;
	private FlashCardDatabase database;

	public DatabaseFlashCard(int id, FlashCardDatabase db) {
		this.id = id;
		this.database = db;
	}

	public int getLocalId()
	{
		return id;
	}
	
	@Override
	public UUID getUniqueId()
	{
		try (Statement statement = database.getStatement()){
			ResultSet rs = statement.executeQuery(
					"SELECT UUID FROM FLASHCARDS WHERE ID=" + id);
			if (rs.next() == false) {
				System.out.println("BAD FLASHCARD ID!");
				return null;
			}
			return UUID.fromString(rs.getString("UUID"));

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getName() {
		try (Statement statement = database.getStatement()){
			ResultSet rs = statement.executeQuery(
					"SELECT NAME FROM FLASHCARDS WHERE ID=" + id);
			if (rs.next() == false) {
				System.out.println("BAD FLASHCARD ID!");
				return "";
			}
			return rs.getString("NAME");

		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public Collection<FlashCardSet> getSets() {
		List<FlashCardSet> sets = new ArrayList<>();
		try (Statement statement = database.getStatement()){
			String query = "SELECT SET_ID FROM SETS_FLASHCARDS WHERE FLASHCARD_ID=" + id;
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next())
			{
				sets.add(new DatabaseSet(rs.getInt("SET_ID"), database));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sets;
	}

	/**
	 * Get the names of all of the tags associated with this flashcard
	 */
	@Override
	public Collection<String> getTags() {
		List<String> tags = new ArrayList<>();
		try (Statement statement = database.getStatement()){
			String query = "SELECT NAME FROM "
					+ "(SELECT TAG_ID FROM FLASHCARDS_TAGS WHERE FLASHCARD_ID="
					+ id + ")" + "INNER JOIN TAGS ON TAGS.ID=TAG_ID";
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next())
				tags.add(rs.getString("NAME"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags;
	}

	/**
	 * Will check database if in TAGS there's an entry for the given string -if
	 * not, create it -if so continue
	 * 
	 * Will then create an entry in the FLASHCARDS_TAGS table to represent the
	 * fact that the given card has the given tag
	 * 
	 * @param cardId
	 * @param tag
	 *            FIXME: Where do we lower-case-ize the tag?
	 * @throws SQLException
	 */
	@Override
	public void addTag(String tag) throws IOException {
		try {
			Integer tagId = database.getTagId(tag);

			// If result set is empty (tag does not yet exist in TAGS table)
			if (tagId == null) {
				// make a tag with the given name
				database.makeTag(tag);

				// re-perform the query to get the ID (Would use SCOPE_IDENTITY,
				// but unsure of support for it)
				tagId = database.getTagId(tag);
			}

			// Now we should be guaranteed to get some results
			if (tagId == null)
				throw new SQLException(
						"Illegal state of database-could not add tag");

			database.addTagToCard(id, tagId);
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public int getInterval() {
		try (Statement statement = database.getStatement())
		{
			ResultSet rs = statement.executeQuery("SELECT INTERVAL FROM FLASHCARDS WHERE ID=" + id);
			if (rs.next() == false)
			{
				System.out.println("BAD CARD ID!");
				return -1;
			}
			
			return rs.getInt("INTERVAL");
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void setInterval(int interval) throws IOException {
		String query = "UPDATE FLASHCARDS\n" + 
						"SET interval=" + interval + "\n" +
						"WHERE ID=" + id;
		
		try (Statement statement = database.getStatement()){
			statement.execute(query);
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public String getPath() throws IOException {
		try (Statement statement = database.getStatement()){
			ResultSet rs = statement.executeQuery(
					"SELECT FILE_PATH FROM FLASHCARDS WHERE ID=" + id);
			if (rs.next() == false)
				throw new IOException("Bad flashcard ID!");

			return rs.getString("FILE_PATH");

		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public AudioFile getQuestionAudio() {
		try {
			String path = getPath();
			
			if (!path.endsWith("/"))
			{
				System.out.println("Weirdly formatted path " + path);
			}
	
			return new DiscAudioFile(path + FlashcardConstants.QUESTION_FILE);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AudioFile getAnswerAudio() {
		try {
			String path = getPath();
			
			if (!path.endsWith("/"))
			{
				System.out.println("Weirdly formatted path " + path);
			}
	
			return new DiscAudioFile(path + FlashcardConstants.ANSWER_FILE);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void removeTag(String tag) throws IOException {
		try {
			Integer tagId = database.getTagId(tag);
			if (tagId == null)
				return;

			database.removeTagFromCard(id, tagId);
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}

	}
	
	@Override
	public String toString()
	{
		return "DB Flashcard with table id " + id;
	}
	
	@Override
	public boolean sameMetaData(FlashCard s)
	{	
		// Make sure both sets have the same tags
		boolean tagEquality = new HashSet<>(getTags()).equals(new HashSet<>(s.getTags()));

		return (tagEquality && s.getName().equals(getName()) && s.getInterval() == getInterval());
	}

	@Override
	public int hashCode()
	{
		UUID uid = getUniqueId();
		if (uid != null)
		{
			return uid.hashCode();
		}
		else
		{
			throw new IllegalStateException("ERROR dbflashcard hashcode");
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof DatabaseFlashCard))
			return false;
		
		DatabaseFlashCard card = (DatabaseFlashCard)o;
		return card.id == id;
	}
}
