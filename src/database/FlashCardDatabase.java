package database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.FlashcardConstants;
import utils.Writer;
import backend.Resources;
import flashcard.FlashCard;
import flashcard.FlashCardSet;

public class FlashCardDatabase implements Resources {

	/**
	 * Remove everything from a DB if it exists. If it does not exist it will be
	 * created in an uninitialized state
	 * 
	 * @param dir
	 * @param db
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void clear(String dir, String db)
			throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");

		// Create a new one (just connect to it)
		Connection conn = DriverManager.getConnection("jdbc:h2:" + dir + db);

		Statement stat = conn.createStatement();

		stat.execute("DROP ALL OBJECTS");
	}

	private static void deleteCardFromDisk(FlashCard card) throws IOException {
		String path = card.getPath();

		File question = new File(path + FlashcardConstants.QUESTION_FILE);
		question.delete();

		File answer = new File(path + FlashcardConstants.ANSWER_FILE);
		answer.delete();

		File dir = new File(path);
		dir.delete();

	}

	/**
	 * Create a database at given location. Will initialize all of the tables
	 * and stuff
	 * 
	 * @param dir
	 * @param db
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static void initialize(String dir, String db)
			throws ClassNotFoundException, SQLException {
		if (!dir.endsWith("/")) {
			System.out.println("Directory should end with /");
		}

		Class.forName("org.h2.Driver");

		// Create a new one (just connect to it)
		Connection conn = DriverManager.getConnection("jdbc:h2:" + dir + db);

		Statement stat = conn.createStatement();

		stat.execute("CREATE TABLE IF NOT EXISTS FLASHCARDS (id INTEGER PRIMARY KEY AUTO_INCREMENT, file_path VARCHAR(255), name VARCHAR(255), interval INTEGER)");
		// Includes both global tags and regular tags
		stat.execute("CREATE TABLE IF NOT EXISTS TAGS (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))");
		// Maintains track of how cards have tags. If card X has tag Y, then the
		// entry (X, Y) will appear in this table
		stat.execute("CREATE TABLE IF NOT EXISTS FLASHCARDS_TAGS (flashcard_id INTEGER, tag_id INTEGER)");

		stat.execute("CREATE TABLE IF NOT EXISTS SETS (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), interval INTEGER)");
		// Keep track of which sets have which cards. If set X contains card Y,
		// the entry (X, Y) will appear
		stat.execute("CREATE TABLE IF NOT EXISTS SETS_FLASHCARDS (set_id INTEGER, flashcard_id INTEGER)");
		// Maintains track of how sets have tags. If set X has tag Y, then the
		// entry (X, Y) will appear in this table
		stat.execute("CREATE TABLE IF NOT EXISTS SETS_TAGS (set_id INTEGER, tag_id INTEGER)");

		stat.close();
		conn.close();
	}

	/**
	 * Write all of the cards info to disk based on the path it gives
	 * 
	 * @param card
	 * @throws IOException
	 */
	private static void writeCardToDisk(FlashCard card) throws IOException {
		String path = card.getPath();

		if (path == null || path.isEmpty()) {
			throw new IOException("Invalid path");
		}
		// Create the directory for the card if it doesn't exist yet
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdir();

		Writer.writeAudioFile(path, card.getQuestionAudio().getStream(), true);
		Writer.writeAudioFile(path, card.getAnswerAudio().getStream(), false);
	}

	private Connection connection;

	protected Statement statement;

	public FlashCardDatabase(String dir, String db) {
		// Just make sure we call with correctly formatted args
		if (!dir.endsWith("/")) {
			System.out.println("Directory should end with /");
		}

		try {

			initialize(dir, db);

			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection("jdbc:h2:" + dir + db);
			statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addCardToSet(int cardId, int setId) throws SQLException {
		// avoid inserting duplicate, so use MERGE not insert
		String query = "MERGE INTO SETS_FLASHCARDS KEY(SET_ID, FLASHCARD_ID) VALUES ("
				+ setId + ", " + cardId + ")";
		statement.execute(query);
	}

	/**
	 * 
	 * @param cardId
	 * @param tagId
	 * @throws SQLException
	 */
	public void addTagToCard(int cardId, int tagId) throws SQLException {
		// avoid inserting duplicate, so use MERGE not insert
		String query = "MERGE INTO FLASHCARDS_TAGS KEY(FLASHCARD_ID, TAG_ID) VALUES ("
				+ cardId + ", " + tagId + ")";
		statement.execute(query);
	}

	/**
	 * Create entry in SETS_TAGS with the given pair of values
	 * 
	 * @param setId
	 * @param tagId
	 * @throws SQLException
	 */
	public void addTagToSet(int setId, int tagId) throws SQLException {
		// avoid inserting duplicate, so use MERGE not insert
		String query = "MERGE INTO SETS_TAGS KEY(SET_ID, TAG_ID) VALUES ("
				+ setId + ", " + tagId + ")";
		statement.execute(query);
	}

	public void close() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getAllCardNames()
	{
		ResultSet rs;
		try {
			rs = statement.executeQuery("SELECT NAME FROM FLASHCARDS");
			List<String> names = new ArrayList<>();
			while (rs.next()) {
				names.add(rs.getString("NAME"));
			}
			return names;

	/**
	 * Delete a card from the database (remove any entries containing it's card
	 * ID) If the card isn't in the database to begin with, return cleanly
	 * 
	 * @param f
	 */
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Arrays.asList();
		}
	}

	
	public List<String> getAllSetNames()
	{
		ResultSet rs;
		try {
			rs = statement.executeQuery("SELECT NAME FROM SETS");
			List<String> names = new ArrayList<>();
			while (rs.next()) {
				names.add(rs.getString("NAME"));
			}
			return names;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Arrays.asList();
		}
	}
	
	
	public void deleteCard(FlashCard f) {

		// Get the flashcard that has the same path as f
		try {
			deleteCardFromDisk(f);

			String query = "SELECT ID FROM FLASHCARDS WHERE FILE_PATH='"
					+ f.getPath() + "'";
			ResultSet rs = statement.executeQuery(query);

			if (!rs.next()) {
				// card doesn't exist to begin with
				return;
			}

			int cardId = rs.getInt("ID");

			// 1) Remove its entry from FLASHCARDS
			String deleteQuery = "DELETE FROM FLASHCARDS WHERE ID=" + cardId;
			statement.execute(deleteQuery);

			// 2) Remove all entries from FLASHCARDS_TAGS table
			deleteQuery = "DELETE FROM FLASHCARDS_TAGS WHERE FLASHCARD_ID="
					+ cardId;
			statement.execute(deleteQuery);

			// FIXME: Delete for sets!

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteSet(FlashCardSet s) {
		try {
			String query = "SELECT ID FROM SETS WHERE NAME='" + s.getName()
					+ "'";
			ResultSet rs = statement.executeQuery(query);

			if (!rs.next()) {
				// card doesn't exist to begin with
				return;
			}

			int cardId = rs.getInt("ID");

			// 1) Remove its entry from FLASHCARDS
			String deleteQuery = "DELETE FROM SETS WHERE ID=" + cardId;
			statement.execute(deleteQuery);

			// 2) Remove all entries from FLASHCARDS_TAGS table
			deleteQuery = "DELETE FROM SETS_TAGS WHERE SET_ID=" + cardId;
			statement.execute(deleteQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<FlashCard> getAllCards() {
		List<FlashCard> cards = new ArrayList<>();
		try {
			ResultSet rs = statement.executeQuery("SELECT ID FROM FLASHCARDS");
			while (rs.next()) {
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cards;
	}

	@Override
	public List<FlashCardSet> getAllSets() {
		List<FlashCardSet> sets = new ArrayList<>();
		try {
			ResultSet rs = statement.executeQuery("SELECT ID FROM SETS");
			while (rs.next()) {
				sets.add(new DatabaseSet(rs.getInt("ID"), this));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sets;
	}

	/**
	 * Do a query on the tags database to get all possible tags a card can have
	 * that exist
	 * 
	 * @return
	 */
	public List<String> getAllTags() {
		ResultSet rs;
		try {
			rs = statement.executeQuery("SELECT NAME FROM TAGS");
			List<String> tags = new ArrayList<>();
			while (rs.next()) {
				tags.add(rs.getString("NAME"));
			}
			return tags;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Arrays.asList();
		}
	}

	/**
	 * Get all of the flashcards with the given name
	 */
	@Override
	public List<FlashCard> getFlashCardsByName(String name) {
		List<FlashCard> cards = new ArrayList<>();

		try {
			ResultSet rs = statement
					.executeQuery("SELECT ID FROM FLASHCARDS WHERE name='"
							+ name + "'");

			while (rs.next()) {
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cards;
	}

	@Override
	public List<String> getFlashCardsByTag(String tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlashCard> getFlashCardsWithTag(String tag) {

		List<FlashCard> cards = new ArrayList<>();
		try {
			Integer tagId = getTagId(tag);

			if (tagId == null)
				return Arrays.asList();

			String query = "SELECT ID FROM FLASHCARDS INNER JOIN "
					+ "(SELECT FLASHCARD_ID FROM FLASHCARDS_TAGS WHERE TAG_ID = "
					+ tagId + ")" + "ON FLASHCARDS.ID = FLASHCARD_ID";

			ResultSet rs = statement.executeQuery(query);

			while (rs.next()) {
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cards;
	}

	@Override
	public FlashCardSet getSetByName(String setName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Statement getStatement() {
		return statement;
	}

	/**
	 * Return the DB id of a given tag. Null if not found
	 * 
	 * @param tag
	 * @return
	 * @throws SQLException
	 */
	public Integer getTagId(String tag) throws SQLException {
		ResultSet tagIdCheck = statement
				.executeQuery("SELECT ID FROM TAGS WHERE NAME = '" + tag + "'");
		if (tagIdCheck.next() == false)
			return null;

		return tagIdCheck.getInt("ID");
	}

	/**
	 * Add an entry to the TAGS table with the given name
	 * 
	 * @param tag
	 * @throws SQLException
	 */
	public void makeTag(String tag) throws SQLException {
		statement.execute("INSERT INTO TAGS (NAME) VALUES ('" + tag + "')");
	}

	/**
	 * Remove an entry from the FLASHCARDS_TAGS table
	 * 
	 * @param cardId
	 * @param tagId
	 * @throws SQLException
	 */
	public void removeTagFromCard(int cardId, int tagId) throws SQLException {
		String query = "DELETE FROM FLASHCARDS_TAGS WHERE TAG_ID = " + tagId
				+ " AND FLASHCARD_ID = " + cardId;
		statement.execute(query);
	}

	public void removeTagFromSet(int setId, int tagId) throws SQLException {
		String query = "DELETE FROM SETS_TAGS WHERE TAG_ID = " + tagId
				+ " AND SET_ID = " + setId;
		statement.execute(query);
	}

	/***
	 * Write given flashcard to the database -returns flashcard object linked to
	 * the database (meaning anything you do with it will change the database)
	 * -returns null on failure to write to DB or read from given flashcard
	 * 
	 * @param flashcard
	 */
	public DatabaseFlashCard writeCard(FlashCard flashcard) {
		System.out.println("Writing " + flashcard);
		try {

			// Write q/a files to disk
			writeCardToDisk(flashcard);

			// 1) Check if this card exists already:
			String idQuery = "SELECT ID FROM FLASHCARDS WHERE name='"
					+ flashcard.getName() + "' AND file_path='"
					+ flashcard.getPath() + "'";
			ResultSet rs = statement.executeQuery(idQuery);

			if (rs.next()) {
				System.out.println("WARNING: A card with that path "
						+ flashcard.getPath()
						+ " already exists. Not writing this card");
				return new DatabaseFlashCard(rs.getInt("ID"), this);
			}

			// 2) Insert to flashcards table
			String query = "INSERT INTO FLASHCARDS (name, file_path, interval) VALUES ('"
					+ flashcard.getName()
					+ "', '"
					+ flashcard.getPath()
					+ "', " + flashcard.getInterval() + ")";

			statement.execute(query);

			// 3) Get the ID of whatever we just inserted
			rs = statement.executeQuery(idQuery);

			if (!rs.next()) {
				System.out.println("ERROR with database!");
			}
			int flashCardId = rs.getInt("ID");

			// Now we need to add all of the tags and sets. To do this, create a
			// DatabaseFlashCard object, which will let us just "add" tags to
			// the card
			DatabaseFlashCard dbCard = new DatabaseFlashCard(flashCardId, this);

			for (String tag : flashcard.getTags()) {
				dbCard.addTag(tag);
			}

			return dbCard;

			// TODO: Sets
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * Write the given set to the database, and return a DatabaseSet that can be
	 * used to modify/update it
	 * 
	 * @param s
	 */
	public DatabaseSet writeSet(FlashCardSet s) {
		try {

			// 1) Check if this card exists already:
			String idQuery = "SELECT ID FROM SETS WHERE name='" + s.getName()
					+ "'";
			ResultSet rs = statement.executeQuery(idQuery);

			if (rs.next()) {
				// FIXME: No duplicate set names, bitchez
				System.out
						.println("WARNING: A set with the name "
								+ s.getName()
								+ " already exists. Writing a duplicate entry. Collision resolving code goes here");
			}

			// 2) Insert to sets table
			String query = "INSERT INTO SETS (name) VALUES ('" + s.getName()
					+ "', " + ")";

			statement.execute(query);

			// 3) Get the ID of whatever we just inserted
			rs = statement.executeQuery(idQuery);

			if (!rs.next()) {
				System.out.println("ERROR with database!");
			}
			int setId = rs.getInt("ID");

			// Now we need to add all of the tags and sets. To do this, create a
			// DatabaseFlashCard object, which will let us just "add" tags to
			// the card
			DatabaseSet dbSet = new DatabaseSet(setId, this);

			// Write all of the tag entries
			for (String tag : s.getTags()) {
				dbSet.addTag(tag);
			}

			// Write all of the cards this set contains
			for (FlashCard card : s.getAll()) {
				dbSet.addCard(card);
			}

			return dbSet;

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}
}
