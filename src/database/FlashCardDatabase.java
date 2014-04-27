package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import backend.Resources;
import flashcard.FlashCard;
import flashcard.FlashCardSet;
import flashcard.FlashCardStub;

public class FlashCardDatabase implements Resources{

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
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public Statement getStatement() {
		return statement;
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

	/***
	 * Write given flashcard to the database
	 * -returns flashcard object linked to the database (meaning anything you do with it will change the database)
	 * -returns null on failure to write to DB or read from given flashcard
	 * 
	 * @param flashcard
	 */
	public DatabaseFlashCard writeCard(FlashCard flashcard) {
		try {

			// 1) Check if this card exists already:
			String idQuery = "SELECT ID FROM FLASHCARDS WHERE name='"
					+ flashcard.getName() + "' AND file_path='"
					+ flashcard.getPath() + "'";
			ResultSet rs = statement.executeQuery(idQuery);
					
			if (rs.next())
			{
				System.out.println("WARNING: A card with that path " + flashcard.getPath() + " already exists. Writing a new duplicate entry!");
			}

			// 2) Insert to flashcards table
			String query = "INSERT INTO FLASHCARDS (name, file_path, interval) VALUES ('"
					+ flashcard.getName()
					+ "', '"
					+ flashcard.getPath()
					+ "', "
					+ flashcard.getInterval() + ")";

			statement.execute(query);
			
			// 3) Get the ID of whatever we just inserted
			rs = statement.executeQuery(idQuery);
			
			
			if (!rs.next())
			{
				System.out.println("ERROR with database!");
			}
			int flashCardId = rs.getInt("ID");


			// Now we need to add all of the tags and sets. To do this, create a DatabaseFlashCard object, which will let us just "add" tags to the card
			DatabaseFlashCard dbCard = new DatabaseFlashCard(flashCardId, this);
			
			for (String tag : flashcard.getTags())
			{
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
	 * Return the DB id of a given tag. Null if not found
	 * 
	 * @param tag
	 * @return
	 * @throws SQLException
	 */
	public Integer getTagId(String tag) throws SQLException {
		ResultSet tagIdCheck = statement
				.executeQuery("select ID from TAGS WHERE NAME = '" + tag + "'");
		if (tagIdCheck.next() == false)
			return null;

		return tagIdCheck.getInt("ID");
	}
	
	@Override
	public List<FlashCard> getFlashCardsByName(String name) {
		List<FlashCard> cards = new ArrayList<>();

		try
		{
			ResultSet rs = statement.executeQuery("SELECT ID FROM FLASHCARDS WHERE name='" + name + "'");
			
			while (rs.next())
			{
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return cards;
	}

	@Override
	public List<FlashCard> getFlashCardsWithTag(String tag) {
		
		List<FlashCard> cards = new ArrayList<>();
		try
		{	
			Integer tagId = getTagId(tag);
			
			if (tagId == null)
				return Arrays.asList();
			
			String query = "SELECT ID FROM FLASHCARDS INNER JOIN " +
							"(SELECT FLASHCARD_ID FROM FLASHCARDS_TAGS WHERE TAG_ID = " + tagId + ")" +
							"ON FLASHCARDS.ID = FLASHCARD_ID";
			
			ResultSet rs = statement.executeQuery(query);
			
			while (rs.next())
			{
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return cards;
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

	/**
	 * Remove everything from a DB if it exists. If it does not exist it will be created in an uninitialized state
	 * 
	 * @param dir
	 * @param db
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void clear(String dir, String db) throws ClassNotFoundException, SQLException
	{
		Class.forName("org.h2.Driver");
		
		// Create a new one (just connect to it)
		Connection conn = DriverManager.getConnection("jdbc:h2:" + dir + db);

		Statement stat = conn.createStatement();
		
		stat.execute("DROP ALL OBJECTS");
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
		stat.execute("CREATE TABLE IF NOT EXISTS TAGS (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))");
		stat.execute("CREATE TABLE IF NOT EXISTS FLASHCARDS_TAGS (flashcard_id INTEGER, tag_id INTEGER)");
		stat.execute("CREATE TABLE IF NOT EXISTS SETS (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))");
		stat.execute("CREATE TABLE IF NOT EXISTS SETS_FLASHCARDS (set_id INTEGER, flashcard_id INTEGER)");
		stat.execute("CREATE TABLE IF NOT EXISTS GLOBAL_TAGS (id INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))");
		stat.execute("CREATE TABLE IF NOT EXISTS SETS_GLOBAL_TAGS (set_id INTEGER, global_tag_id INTEGER)");

		stat.close();
		conn.close();
	}
	
	public void close()
	{
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException, IOException {

		FlashCardDatabase db = new FlashCardDatabase("./", "carddb");
		System.out.println(db.getAllTags());

		DatabaseFlashCard card = new DatabaseFlashCard(2, db);
		System.out.println(card.getTags());
		card.addTag("math");

		System.out.println(card.getInterval());
		card.setInterval(3);
		System.out.println(card.getInterval());

		System.out.println(card.getPath());
		System.out.println(card.getName());
		
		FlashCard localCard = new FlashCardStub("files/DaBest/");
		
		DatabaseFlashCard dbCard = db.writeCard(localCard);
		dbCard.setInterval(2);
		
		
		// db.addTagToCard(2, "tall people");
		// db.addTagToCard(2, "a new tag");

		/*
		 * // Do stuff
		 * 
		 * // delete the database named 'test' in the user home directory
		 * //DeleteDbFiles.execute("./", "test", true);
		 * 
		 * Class.forName("org.h2.Driver"); Connection conn =
		 * DriverManager.getConnection("jdbc:h2:./carddb"); Statement stat =
		 * conn.createStatement();
		 * 
		 * // this line would initialize the database // from the SQL script
		 * file 'init.sql' // stat.execute("runscript from 'init.sql'");
		 * 
		 * //stat.execute("create table test(id int primary key, name varchar(255))"
		 * ); //stat.execute("insert into test values(2, 'hello again')");
		 * //stat.execute("insert into test values(1, 'Hello')"); ResultSet rs;
		 * rs = stat.executeQuery("select * from FLASHCARDS"); while (rs.next())
		 * { System.out.println(rs.getString("name")); } stat.close();
		 * conn.close();
		 */
	}

	@Override
	public FlashCardSet getSetByName(String setName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlashCardSet> getAllSets() {
		// TODO Auto-generated method stub
		return Arrays.asList();
	}

	@Override
	public List<FlashCard> getAllCards() {
		List<FlashCard> cards = new ArrayList<>();
		try
		{
			ResultSet rs = statement.executeQuery("SELECT ID FROM FLASHCARDS");
			while (rs.next())
			{
				cards.add(new DatabaseFlashCard(rs.getInt("ID"), this));
			}
			System.out.println(cards.size());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return cards;
	}

	@Override
	public List<String> getFlashCardsByTag(String tag) {
		// TODO Auto-generated method stub
		return null;
	}
}
