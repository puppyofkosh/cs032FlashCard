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
import audio.DiscAudioFile;
import audio.MemoryAudioFile;
import backend.Resources;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.SerializableFlashCard;
import flashcard.SimpleSet;

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

		stat.close();
		conn.close();
	}

	/***
	 * Remove a cards files from disk (that is the q and a files as well as the folder containing them.)
	 * @param card
	 * @throws IOException
	 */
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

		stat.execute("CREATE TABLE IF NOT EXISTS SETS (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), interval INTEGER, author VARCHAR(255))");
		// Keep track of which sets have which cards. If set X contains card Y,
		// the entry (X, Y) will appear
		stat.execute("CREATE TABLE IF NOT EXISTS SETS_FLASHCARDS (set_id INTEGER, flashcard_id INTEGER)");
		// Maintains track of how sets have tags. If set X has tag Y, then the
		// entry (X, Y) will appear in this table
		stat.execute("CREATE TABLE IF NOT EXISTS SETS_TAGS (set_id INTEGER, tag_id INTEGER)");

		stat.close();
		conn.close();
	}

	/***
	 * Currently called by bin/populateDatabase. Just a temporary method to add
	 * a few sets to the DB.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FlashCardSet set = new SimpleSet("presidents");
		set.setAuthor("Ian");
		set.setTags(Arrays.asList("presidents"));

		SerializableFlashCard.Data data = new SerializableFlashCard.Data();
		data.question = new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/hi-there.wav"));
		data.answer = new MemoryAudioFile(new DiscAudioFile(
				"data/flashcard-test/acronym.wav"));
		data.tags = Arrays.asList("tag_a", "weird_people");
		data.interval = 5;
		data.pathToFile = "files/george-washington/";
		data.name = "george washington";

		set.addCard(new SerializableFlashCard(data));

		FlashCardDatabase db = new FlashCardDatabase(FlashcardConstants.DB_DIR,
				FlashcardConstants.DB_FILE);

		// Write the set we just described up there and replace our reference to
		// it with a set that will reflect what's in the database
		set = db.writeSet(set);

		// Now, if we change set the changes will go in the DB

		// Try adding a card for example
		data.pathToFile = "files/abraham/";
		data.name = "abroham";
		set.addCard(new SerializableFlashCard(data));

		// Create another set just for fun
		set = new SimpleSet("state capitals");
		set.setAuthor("Ian!");
		set.setInterval(5);
		set.setTags(Arrays.asList("states", "capitals"));
		
		data.pathToFile = "files/vermont/";
		data.name = "vermont";

		FlashCard vermontCard = new SerializableFlashCard(data);

		set.addCard(vermontCard);
		db.writeSet(set);

		// Now say we're in another function and just wanna get a set based on
		// its name, ya know?
		set = db.getSetByName("presidents");
		if (set != null) {
			// Add the state capital card to the presidents set. Makes no sense,
			// but tests having a card in more than 1 set.
			set.addCard(vermontCard);
		}
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

	public FlashCardDatabase(String dir, String db) {
		// Just make sure we call with correctly formatted args
		if (!dir.endsWith("/")) {
			System.out.println("Directory should end with /");
		}

		try {

			initialize(dir, db);

			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection("jdbc:h2:" + dir + db);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addCardToSet(int cardId, int setId) throws SQLException {
		System.out.println("Adding card" + cardId + " to set " + setId);
		// avoid inserting duplicate, so use MERGE not insert
		String query = "MERGE INTO SETS_FLASHCARDS KEY(SET_ID, FLASHCARD_ID) VALUES ("
				+ setId + ", " + cardId + ")";
		try (Statement statement = getStatement()) {
			statement.execute(query);
		}
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
		try (Statement statement = getStatement()) {
			statement.execute(query);
		}
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

		try (Statement statement = getStatement()) {
			statement.execute(query);
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteCard(FlashCard f) {

		// Get the flashcard that has the same path as f
		try (Statement statement = getStatement()) {
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

			// 3) Remove entries from SETS_FLASHCARDS table
			deleteQuery = "DELETE FROM SETS_FLASHCARDS WHERE FLASHCARD_ID="
					+ cardId;
			statement.execute(deleteQuery);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void deleteSet(FlashCardSet s) {

		try (Statement statement = getStatement()) {
			String query = "SELECT ID FROM SETS WHERE NAME='" + s.getName()
					+ "'";

			ResultSet rs = statement.executeQuery(query);

			if (!rs.next()) {
				// card doesn't exist to begin with
				return;
			}

			int setId = rs.getInt("ID");

			// 1) Remove its entry from SETS
			String deleteQuery = "DELETE FROM SETS WHERE ID=" + setId;
			statement.execute(deleteQuery);

			// 2) Remove all entries from SETS_TAGS table
			deleteQuery = "DELETE FROM SETS_TAGS WHERE SET_ID=" + setId;
			statement.execute(deleteQuery);

			// 3) Remove entries from FLASHCARDS_SETS table
			deleteQuery = "DELETE FROM FLASHCARDS_SETS WHERE SET_ID=" + setId;

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes care of duplicate set names. If a set with the given name already
	 * exists, then it will find a set name (like name5) that hasn't been used
	 * yet.
	 * 
	 * @param namePrefix
	 * @throws SQLException
	 */
	private String findUniqueName(String originalName) throws SQLException {
		try (Statement statement = getStatement()) {

			String name = originalName;
			boolean hasNext = false;
			int number = 0;
			do {
				name = originalName;
				if (number != 0) {
					name += number;
				}

				String idQuery = "SELECT ID FROM SETS WHERE name='" + name
						+ "'";
				ResultSet rs = statement.executeQuery(idQuery);

				hasNext = rs.next();
				number++;
			} while (hasNext);
			return name;
		}
	}

	public List<String> getAllCardNames() {
		ResultSet rs;
		try (Statement statement = getStatement()) {
			rs = statement.executeQuery("SELECT NAME FROM FLASHCARDS");
			List<String> names = new ArrayList<>();
			while (rs.next()) {
				names.add(rs.getString("NAME"));
			}
			return names;
		} catch (SQLException e) {
			e.printStackTrace();
			return Arrays.asList();
		}
	}

	@Override
	public List<FlashCard> getAllCards() {
		List<FlashCard> cards = new ArrayList<>();
		try (Statement statement = getStatement()) {
			ResultSet rs = statement.executeQuery("SELECT ID FROM FLASHCARDS");
			while (rs.next()) {
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cards;
	}

	public List<String> getAllSetNames() {
		ResultSet rs;
		try (Statement statement = getStatement()) {
			rs = statement.executeQuery("SELECT NAME FROM SETS");
			List<String> names = new ArrayList<>();
			while (rs.next()) {
				names.add(rs.getString("NAME"));
			}
			return names;

		} catch (SQLException e) {
			e.printStackTrace();
			return Arrays.asList();
		}
	}

	@Override
	public List<FlashCardSet> getAllSets() {
		List<FlashCardSet> sets = new ArrayList<>();
		try (Statement statement = getStatement()) {
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
		try (Statement statement = getStatement()) {
			rs = statement.executeQuery("SELECT NAME FROM TAGS");
			List<String> tags = new ArrayList<>();
			while (rs.next()) {
				tags.add(rs.getString("NAME"));
			}
			return tags;

		} catch (SQLException e) {
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

		try (Statement statement = getStatement()) {
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
	public List<FlashCard> getFlashCardsWithTag(String tag) {

		List<FlashCard> cards = new ArrayList<>();
		try (Statement statement = getStatement()) {
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
	public FlashCardSet getSetByName(String name) {
		try (Statement statement = getStatement()) {
			String query = "SELECT ID FROM SETS WHERE name='" + name + "'";
			ResultSet rs = statement.executeQuery(query);

			if (!rs.next()) {
				return null;
			}
			FlashCardSet set = new DatabaseSet(rs.getInt("ID"), this);

			// If there was more than 1 result
			if (rs.next()) {
				System.out
						.println("ERROR: Database is in bad state. More than one set with same name");
			}
			return set;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get a statement to run a query with. IT IS UP TO YOU TO CLOSE THIS. Best
	 * practice is to use it like this: try (Statement statement =
	 * database.getStatement()) { }
	 * 
	 * Because it is autocloseable
	 * 
	 * @return
	 */
	public Statement getStatement() {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return the DB id of a given tag. Null if not found
	 * 
	 * @param tag
	 * @return
	 * @throws SQLException
	 */
	public Integer getTagId(String tag) throws SQLException {
		try (Statement statement = getStatement()) {
			ResultSet tagIdCheck = statement
					.executeQuery("SELECT ID FROM TAGS WHERE NAME = '" + tag
							+ "'");
			if (tagIdCheck.next() == false)
				return null;

			return tagIdCheck.getInt("ID");
		}
	}

	/**
	 * Add an entry to the TAGS table with the given name
	 * 
	 * @param tag
	 * @throws SQLException
	 */
	public void makeTag(String tag) throws SQLException {
		try (Statement statement = getStatement()) {
			statement.execute("INSERT INTO TAGS (NAME) VALUES ('" + tag + "')");
		}
	}

	/**
	 * Remove an entry from the FLASHCARDS_TAGS table
	 * 
	 * @param cardId
	 * @param tagId
	 * @throws SQLException
	 */
	public void removeTagFromCard(int cardId, int tagId) throws SQLException {

		try (Statement statement = getStatement()) {
			String query = "DELETE FROM FLASHCARDS_TAGS WHERE TAG_ID = "
					+ tagId + " AND FLASHCARD_ID = " + cardId;
			statement.execute(query);
		}
	}

	public void removeTagFromSet(int setId, int tagId) throws SQLException {
		try (Statement statement = getStatement()) {
			String query = "DELETE FROM SETS_TAGS WHERE TAG_ID = " + tagId
					+ " AND SET_ID = " + setId;
			statement.execute(query);
		}
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
		System.out.println(Thread.currentThread().getName());
		try (Statement statement = getStatement()) {

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
	 * May return a set with a different name than the one given to deal with
	 * name collisions
	 * 
	 * You can use the set returned to change what's in the database (ie. the
	 * set returned represents what is stored in the database, and changing it
	 * will change the database)
	 * 
	 * @param s
	 */
	public DatabaseSet writeSet(FlashCardSet s) {
		try (Statement statement = getStatement()) {

			// Check if set with the same name exists already. If one does,
			// append the number (1, 2, ...) to the set name until we find an
			// unused set name.
			String name = findUniqueName(s.getName());
			System.out.println("Using unique name" + name);
			
			String idQuery = "SELECT ID FROM SETS WHERE name='" + name + "'";

			// 2) Insert to sets table
			String query = "INSERT INTO SETS (name, interval, author) VALUES ('" + name + "', "
					+ s.getInterval() + ", '" + s.getAuthor() + "'" + ")";

			statement.execute(query);

			// 3) Get the ID of whatever we just inserted
			ResultSet rs = statement.executeQuery(idQuery);

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
