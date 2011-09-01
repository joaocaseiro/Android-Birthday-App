package pt.birthday.app;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactsLoader {
	private String id;
	private String name;
	private String phone;
	private String date;
	
	private ContentResolver contentResolver;
	private BirthdayAppDatabase database;

	public ContactsLoader() {
		id = null;
		name = null;
		phone = null;
		date = null;
	}
	
	public void loadContacts(Context ctx) {
		database = new BirthdayAppDatabase(ctx);
		database.open();
		
		contentResolver = getContentResolver(ctx);
		Cursor contactsCursor = readContacts();
        
        if (!empty(contactsCursor)) {
        	analyseAllContactsInCursor(contactsCursor);
        }
        
        database.close();
	}

	private void analyseAllContactsInCursor(Cursor contactsCursor) {
		while (contactsCursor.moveToNext()) {
			analyseCurrentContact(contactsCursor);
        }
	}

	private void analyseCurrentContact(Cursor contactsCursor) {
		id = readContactID(contactsCursor);
		Cursor eventsCursor = readContactEvents(id);
		if(contactHasEventsAssociated(eventsCursor)) {
    		getContactInformation(contactsCursor, eventsCursor);
    		storeContactInformationInDatabase(database);
		}
	}

	private void storeContactInformationInDatabase(BirthdayAppDatabase database) {
		database.insertDATE(id, name, phone, date);
	}

	private Cursor readContacts() {
		Cursor contactsCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		return contactsCursor;
	}

	private ContentResolver getContentResolver(Context ctx) {
		ContentResolver contentResolver = ctx.getContentResolver();
		return contentResolver;
	}

	private void getContactInformation(Cursor contactsCursor, Cursor eventsCursor) {
		date = readEventDate(eventsCursor);
		name = readContactName(contactsCursor);
		
		if (contactHasPhoneNumberAssociated(contactsCursor)) {
             Cursor contactPhonesCursor = readContactPhoneNumber(id); 
             if(contactPhonesCursor.moveToNext()) {
            	 phone = readPhoneNumber(contactPhonesCursor);
             }
		}
	}

	private String readPhoneNumber(Cursor contactPhonesCursor) {
		String phoneNumber = contactPhonesCursor.getString(contactPhonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		return phoneNumber;
	}

	private Cursor readContactPhoneNumber(String contactID) {
		Cursor contactPhoneNumbers = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{contactID}, null);
		return contactPhoneNumbers;
	}

	private String readContactName(Cursor contactsCursor) {
		String contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		return contactName;
	}

	private String readEventDate(Cursor eventsCursor) {
		String eventDate = eventsCursor.getString(eventsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
		return eventDate;
	}

	private String readContactID(Cursor contactsCursor) {
		String contactID = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
		return contactID;
	}

	private Cursor readContactEvents(String contactID) {
		String filterForCurrentContactEvents = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] argumentsOfFilterForCurrentContactEvents = new String[]{contactID, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE}; 
		Cursor eventsCursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, filterForCurrentContactEvents, argumentsOfFilterForCurrentContactEvents, null);
		return eventsCursor;
	}
	
	private boolean contactHasEventsAssociated(Cursor eventsCursor) {
		return  eventsCursor != null && eventsCursor.moveToFirst();
	}

	private boolean contactHasPhoneNumberAssociated(Cursor cursor) {
		return Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0;
	}

	private boolean empty(Cursor cursor) {
		return cursor.getCount() <= 0;
	}
}
