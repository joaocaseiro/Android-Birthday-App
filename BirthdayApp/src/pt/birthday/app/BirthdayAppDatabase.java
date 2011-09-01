package pt.birthday.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BirthdayAppDatabase {
	
	private static final String DATABASE_NAME = "BirthdayDatesAppDatabase";
	private static final int DATABASE_VERSION = 1;
	
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) 
        {
        	compileDatabase(db);
        	
            ////////////
            Log.v(TAG, TAG+" "+"Database Created");
            ////////////
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
        {
            deleteDatabase(db);
            
            ////////////
            Log.v(TAG, TAG+" "+"Database Deleted");
            ////////////

            compileDatabase(db);
            
            ////////////
            Log.v(TAG, TAG+" "+"Database Upgraded");
            ////////////
        }
        
        private void deleteDatabase(SQLiteDatabase db) {
        	db.execSQL("DROP TABLE IF EXISTS " + DATES_TABLE);
		}

		private void compileDatabase(SQLiteDatabase db) {
        	db.execSQL(DATABASE_CREATE);
        }
    }
	
	public BirthdayAppDatabase(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
	
	public static final String DATES_TABLE = "dates";
	public static final String ID_ROW_KEY = "_id";
	public static final String ID_CONTACT_KEY = "contact_id";
    public static final String NAME_CONTACT_KEY = "contact_name";
    public static final String NUMBER_CONTACT_KEY = "contact_number";
    public static final String DATE_CONTACT_KEY = "contact_date";    
    private static final String TAG = "BirthdayAppDatabase";
    private static final String[] DATES_STRUCTURE = new String[] {
    	ID_ROW_KEY, ID_CONTACT_KEY, NAME_CONTACT_KEY, NUMBER_CONTACT_KEY, DATE_CONTACT_KEY}; 

    private static final String DATABASE_CREATE =
        "create table "+ DATES_TABLE +" (" 
        + ID_ROW_KEY + " integer primary key autoincrement, "
        + ID_CONTACT_KEY + " text not null, "
        + NAME_CONTACT_KEY + " text not null, " 
        + NUMBER_CONTACT_KEY + " text, " 
        + DATE_CONTACT_KEY + " text not null);";
    
    public BirthdayAppDatabase open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        
        ////////////
        Log.v(TAG, TAG+" "+"Database Open");
        ////////////

        return this;
    }
    
    public void close() 
    {
        DBHelper.close();
        
        ////////////
        Log.v(TAG, TAG+" "+"Database Closed");
        ////////////
    }
    
    public long insertDATE(String contactID, String contactName, String contactNumber, String contactDate) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ID_CONTACT_KEY, contactID);
        initialValues.put(NAME_CONTACT_KEY, contactName);
        initialValues.put(NUMBER_CONTACT_KEY, contactNumber);
        initialValues.put(DATE_CONTACT_KEY, contactDate);
        long result = db.insert(DATES_TABLE, null, initialValues);
        
        ///////////////
        Log.v(TAG, TAG+" "+"Record inserted with values: rowID("+result+"), contactID("+contactID+"), name("+contactName+"), number("+contactNumber+"), date("+contactDate+")");
        ///////////////
        
        return result;
    }
	
    public boolean deleteDateFromID(long rowId) 
    {
    	Cursor recordToBeDeleted = getSingleDateFromID(rowId);
		String contactID = getContactIDFromCursor(recordToBeDeleted);
		String contactName = getContactNameFromCursor(recordToBeDeleted);
		String contactNumber = getContactNumberFromCursor(recordToBeDeleted);
		String contactDate = getContactDateFromCursor(recordToBeDeleted);
		
    	boolean result = isOperationSuccessful(db.delete(DATES_TABLE, ID_ROW_KEY + "=" + rowId, null));
    	
    	//////////////
    	if(result) {
    		Log.v(TAG, TAG+" "+"Record deleted with values: rowID("+result+"), contactID("+contactID+"), name("+contactName+"), number("+contactNumber+"), date("+contactDate+")");
    	} else {
    		Log.v(TAG, TAG+" "+"Failed to delete record with values: rowID("+result+"), contactID("+contactID+"), name("+contactName+"), number("+contactNumber+"), date("+contactDate+")");
    	}
    	//////////////
    	
        return result;
    }
    
    public boolean deleteAllDates() 
    {
    	int numberOfRecordsToDelete = 0;
    	Cursor recordAllDates = getAllDates();
    	
    	//////////////
    	for(recordAllDates.moveToFirst() ; !recordAllDates.isAfterLast() ; recordAllDates.moveToNext()) {
			numberOfRecordsToDelete++;
		}
		String stringLog = numberOfRecordsToDelete + " records deleted from database";
		Log.v(TAG, TAG+" "+stringLog);
		//////////////
    	
    	db.delete(DATES_TABLE, null, null);
    	
        return true;
    }

	private boolean isOperationSuccessful(int result) {
		return result > 0;
	}
	
	public Cursor getAllDates() 
    {
		Cursor allDatesCursor = db.query(DATES_TABLE, DATES_STRUCTURE, null, null, null, null, null);
		if (!cursorEmpty(allDatesCursor)) {
			allDatesCursor.moveToFirst();
        }
		
		/////////////
		String contactID, contactName, contactNumber, contactDate;
		Log.v(TAG, TAG+" "+"Returned the following records from the database");
		for(; !allDatesCursor.isAfterLast() ; allDatesCursor.moveToNext()) {
			contactID = getContactIDFromCursor(allDatesCursor);
			contactName = getContactNameFromCursor(allDatesCursor);
			contactNumber = getContactNumberFromCursor(allDatesCursor);
			contactDate = getContactDateFromCursor(allDatesCursor);
			Log.v(TAG, TAG+" "+"id: "+contactID+" name: "+contactName+" number: "+contactNumber+" date: "+contactDate);
		}
		
		allDatesCursor.moveToFirst();
		//////////////
		
        return allDatesCursor;
    }
	
	private boolean cursorEmpty(Cursor cursor) {
		return cursor == null;
	}

	public Cursor getSingleDateFromID(long rowId) 
    {
        Cursor singleDateCursor = db.query(true, DATES_TABLE, DATES_STRUCTURE, ID_ROW_KEY + "=" + rowId, null, null, null, null, null);
        if (!cursorEmpty(singleDateCursor)) {
        	singleDateCursor.moveToFirst();
        }
        
		//////////////
        String contactID = getContactIDFromCursor(singleDateCursor);
        String contactName = getContactNameFromCursor(singleDateCursor);
        String contactNumber = getContactNumberFromCursor(singleDateCursor);
        String contactDate = getContactDateFromCursor(singleDateCursor);
		Log.v(TAG, TAG+" "+"id: "+contactID+" name: "+contactName+" number: "+contactNumber+" date: "+contactDate);
		//////////////
		
        return singleDateCursor;
    }
	
	public boolean updateDateFromID(long rowId, String contactID, String contactName, String contactNumber, String contactDate) 
	{
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(ID_CONTACT_KEY, contactID);
		updatedValues.put(NAME_CONTACT_KEY, contactName);
		updatedValues.put(NUMBER_CONTACT_KEY, contactNumber);
		updatedValues.put(DATE_CONTACT_KEY, contactDate);
		boolean result = isOperationSuccessful(db.update(DATES_TABLE, updatedValues, ID_ROW_KEY + "=" + rowId, null));
		
		//////////////
		Cursor singleDateCursor = getSingleDateFromID(rowId);
		contactID = getContactIDFromCursor(singleDateCursor);
        contactName = getContactNameFromCursor(singleDateCursor);
        contactNumber = getContactNumberFromCursor(singleDateCursor);
        contactDate = getContactDateFromCursor(singleDateCursor);
		
		if(result) {
			Log.v(TAG, TAG+" "+"Updated record to => id: "+contactID+" name: "+contactName+" number: "+contactNumber+" date: "+contactDate);
		} else {
			Log.v(TAG, TAG+" "+"Failed to update record => id: "+contactID+" name: "+contactName+" number: "+contactNumber+" date: "+contactDate);
		}
		//////////////
		
		return result; 
	}
	
	public int getNumberOfRecords() {
		Cursor entries = getAllDates();
		int numberOfEntries = entries.getCount();
		return numberOfEntries;
	}

	public static String getContactIDFromCursor(Cursor actualRecord) {
		return actualRecord.getString(actualRecord.getColumnIndex(BirthdayAppDatabase.ID_CONTACT_KEY));
	}
	
	public static String getContactNameFromCursor(Cursor actualRecord) {
		return actualRecord.getString(actualRecord.getColumnIndex(BirthdayAppDatabase.NAME_CONTACT_KEY));
	}
	
	public static String getContactNumberFromCursor(Cursor actualRecord) {
		return actualRecord.getString(actualRecord.getColumnIndex(BirthdayAppDatabase.NUMBER_CONTACT_KEY));
	}
	
	public static String getContactDateFromCursor(Cursor actualRecord) {
		return actualRecord.getString(actualRecord.getColumnIndex(BirthdayAppDatabase.DATE_CONTACT_KEY));
	}
}
