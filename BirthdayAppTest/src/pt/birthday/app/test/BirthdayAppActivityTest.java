package pt.birthday.app.test;

import pt.birthday.app.BirthdayAppActivity;
import pt.birthday.app.BirthdayAppDatabase;
import pt.birthday.app.ContactsLoader;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class BirthdayAppActivityTest extends ActivityInstrumentationTestCase2<BirthdayAppActivity> {

	private BirthdayAppActivity mActivity;

	public BirthdayAppActivityTest() {
		super("pt.birthday.app",BirthdayAppActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
	}
	
	public void testDatabase() {
		deleteAllRecordsFromDatabase();
		long recordID = insertRecordInDatabase();
		updateRecordInDatabase(recordID);
		deleteRecordFromDatabase(recordID);
	}
	
	public void testLoadInfoFromContacts() {
		deleteAllRecordsFromDatabase();
		ContactsLoader contactsLoader = new ContactsLoader();
		contactsLoader.loadContacts(mActivity);
		verifyLoadedContacts();
	}
	
	private void verifyLoadedContacts() {
		BirthdayAppDatabase DB = new BirthdayAppDatabase(mActivity);
		DB.open();
		
		Cursor allEntries = DB.getAllDates();
		assertEquals(4, allEntries.getCount());
		
		String id = BirthdayAppDatabase.getContactIDFromCursor(allEntries);
		String name = BirthdayAppDatabase.getContactNameFromCursor(allEntries);
		String number = BirthdayAppDatabase.getContactNumberFromCursor(allEntries);
		String date = BirthdayAppDatabase.getContactDateFromCursor(allEntries);
		assertEquals("168", id);
		assertEquals("Ana Caseiro", name);
		assertEquals("+351911042670", number);
		assertEquals("1992-03-12", date);
		
		Log.v("", "id: "+id+" name: "+name+" number: "+number+" date: "+date);
		
		allEntries.moveToNext();
		
		id = BirthdayAppDatabase.getContactIDFromCursor(allEntries);
		name = BirthdayAppDatabase.getContactNameFromCursor(allEntries);
		number = BirthdayAppDatabase.getContactNumberFromCursor(allEntries);
		date = BirthdayAppDatabase.getContactDateFromCursor(allEntries);
		
		assertEquals("170", id);
		assertEquals("Rita Vieira", name);
		assertEquals("+351913307005", number);
		assertEquals("1988-01-16", date);
		
		Log.v("", "id: "+id+" name: "+name+" number: "+number+" date: "+date);
		
		allEntries.moveToNext();
		
		id = BirthdayAppDatabase.getContactIDFromCursor(allEntries);
		name = BirthdayAppDatabase.getContactNameFromCursor(allEntries);
		number = BirthdayAppDatabase.getContactNumberFromCursor(allEntries);
		date = BirthdayAppDatabase.getContactDateFromCursor(allEntries);
		
		assertEquals("380", id);
		assertEquals("Joao Caseiro", name);
		assertEquals("+351912356660", number);
		assertEquals("1988-03-16", date);
		
		Log.v("", "id: "+id+" name: "+name+" number: "+number+" date: "+date);
		
		allEntries.moveToNext();
		
		id = BirthdayAppDatabase.getContactIDFromCursor(allEntries);
		name = BirthdayAppDatabase.getContactNameFromCursor(allEntries);
		number = BirthdayAppDatabase.getContactNumberFromCursor(allEntries);
		date = BirthdayAppDatabase.getContactDateFromCursor(allEntries);
		
		assertEquals("552", id);
		assertEquals("Andreia Cunha Ferreira", name);
		assertEquals("+351917038884", number);
		assertEquals("1988-02-23", date);
		
		Log.v("", "id: "+id+" name: "+name+" number: "+number+" date: "+date);
		
		DB.close();
	}

	private void deleteAllRecordsFromDatabase() {
		BirthdayAppDatabase DB = new BirthdayAppDatabase(mActivity);
		DB.open();
		
		assertTrue(DB.deleteAllDates());
		
		assertEquals(0, DB.getNumberOfRecords());
		
		DB.close();
	}

	private long insertRecordInDatabase() {
		long entryRowID;
		String entryContactID = "1";
		String entryContactName = "Joao Manuel Vieira Caseiro";
		String entryContactNumber = "912356660";
		String entryContactDate = "16/03/1988";
		
		BirthdayAppDatabase DB = new BirthdayAppDatabase(mActivity);
		DB.open();
		
		entryRowID = DB.insertDATE(entryContactID, entryContactName, entryContactNumber, entryContactDate);
		assertTrue(validEntryID(entryRowID));
		
		Cursor entryJMVC = DB.getSingleDateFromID(entryRowID);
		assertEquals(BirthdayAppDatabase.getContactIDFromCursor(entryJMVC), entryContactID);
		assertEquals(BirthdayAppDatabase.getContactNameFromCursor(entryJMVC), entryContactName);
		assertEquals(BirthdayAppDatabase.getContactNumberFromCursor(entryJMVC), entryContactNumber);
		assertEquals(BirthdayAppDatabase.getContactDateFromCursor(entryJMVC), entryContactDate);
		
		DB.close();
		
		return entryRowID;
	}

	private boolean validEntryID(long IDentry) {
		return biggerThanZero(IDentry);
	}

	private boolean biggerThanZero(long result) {
		return result > 0;
	}
	
	private void updateRecordInDatabase(long recordID) {
		String entryContactID = "2";
		String entryContactName = "John Maximilian Doe";
		String entryContactNumber = "066653219";
		String entryContactDate = "23/02/1988";
		
		BirthdayAppDatabase DB = new BirthdayAppDatabase(mActivity);
		DB.open();
		
		Cursor oldEntryJMVC = DB.getSingleDateFromID(recordID);
		
		assertTrue(!entryContactID.equals(BirthdayAppDatabase.getContactIDFromCursor(oldEntryJMVC)));
		assertTrue(!entryContactName.equals(BirthdayAppDatabase.getContactNameFromCursor(oldEntryJMVC)));
		assertTrue(!entryContactNumber.equals(BirthdayAppDatabase.getContactNumberFromCursor(oldEntryJMVC)));
		assertTrue(!entryContactDate.equals(BirthdayAppDatabase.getContactDateFromCursor(oldEntryJMVC)));
		
		assertTrue(DB.updateDateFromID(recordID, entryContactID, entryContactName, entryContactNumber, entryContactDate));
		
		Cursor entryJMVC = DB.getSingleDateFromID(recordID);

		assertEquals(entryContactID, BirthdayAppDatabase.getContactIDFromCursor(entryJMVC));
		assertEquals(entryContactName, BirthdayAppDatabase.getContactNameFromCursor(entryJMVC));
		assertEquals(entryContactNumber, BirthdayAppDatabase.getContactNumberFromCursor(entryJMVC));
		assertEquals(entryContactDate, BirthdayAppDatabase.getContactDateFromCursor(entryJMVC));
		
		DB.close();
	}

	private void deleteRecordFromDatabase(long recordID) {
		BirthdayAppDatabase DB = new BirthdayAppDatabase(mActivity);
		DB.open();
		
		assertTrue(DB.deleteDateFromID(recordID));
		
		try {
			DB.getSingleDateFromID(recordID);
			fail();
		} catch (CursorIndexOutOfBoundsException e) {
			
		}
		
		DB.close();
	}

	
}
