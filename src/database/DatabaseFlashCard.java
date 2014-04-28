package database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utils.FlashcardConstants;

import flashcard.FlashCard;
import flashcard.FlashCardSet;

import audio.AudioFile;
import audio.DiscAudioFile;

/***
 * Stores the database ID of a flashcard. All methods just perform queries on
 * the DB
 * 
 * @author puppyofkosh
 * 
 */
public class DatabaseFlashCard implements FlashCard {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private FlashCardDatabase database;

	public DatabaseFlashCard(int id, FlashCardDatabase db) {
		this.id = id;
		this.database = db;
	}

	@Override
	public String getName() {
		try {
			ResultSet rs = database.getStatement().executeQuery(
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get the names of all of the tags associated with this flashcard
	 */
	@Override
	public Collection<String> getTags() {
		List<String> tags = new ArrayList<>();
		try {
			String query = "SELECT NAME FROM "
					+ "(SELECT TAG_ID FROM FLASHCARDS_TAGS WHERE FLASHCARD_ID="
					+ id + ")" + "INNER JOIN TAGS ON TAGS.ID=TAG_ID";
			ResultSet rs = database.getStatement().executeQuery(query);
			
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
				database.getStatement().execute(
						"INSERT INTO TAGS (NAME) VALUES ('" + tag + "')");

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
		try
		{
			ResultSet rs = database.getStatement().executeQuery("SELECT INTERVAL FROM FLASHCARDS WHERE ID=" + id);
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
		
		try {
			database.getStatement().execute(query);
		} catch (SQLException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public String getPath() throws IOException {
		try {
			ResultSet rs = database.getStatement().executeQuery(
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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

}
