package com.soa.bhc.userselection;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Contacts table name
	private static final String TABLE_CONTACTS = "cartTable";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_PRODUCT_ID = "key_product_id";
	private static final String KEY_VARIANT_ID = "key_variant_id";
	private static final String KEY_QUANTITY = "quantity";

	public DatabaseHandler(Context context, String dataBaseName) {
		super(context, dataBaseName, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_PRODUCT_ID + " REAL,"
				+ KEY_VARIANT_ID + " REAL," + KEY_QUANTITY + " REAL" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new contact
	void addCartItem(UserSelectionItem userSelectionItem) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		UserSelectionItemDB cidb = new UserSelectionItemDB(userSelectionItem);

		// we store in the database, the id, as the unique product id.
		values.put(KEY_ID, cidb.getID());
		values.put(KEY_PRODUCT_ID, cidb.getProductID()); // Contact Name
		values.put(KEY_VARIANT_ID, cidb.getVariantID()); // Contact Phone
		values.put(KEY_QUANTITY, cidb.getQuantity());
		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	UserSelectionItemDB getCartItemDB(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
				KEY_PRODUCT_ID, KEY_VARIANT_ID, KEY_QUANTITY }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		UserSelectionItemDB userSelectionItemDB = new UserSelectionItemDB(
				Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor
						.getString(1)), Integer.parseInt(cursor.getString(1)));

		return userSelectionItemDB;
	}

	// Getting All Contacts
	public List<UserSelectionItemDB> getAllCartItemDB() {
		List<UserSelectionItemDB> cartItemDbList = new ArrayList<UserSelectionItemDB>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				UserSelectionItemDB userSelectionItemDB = new UserSelectionItemDB(
						Integer.parseInt(cursor.getString(1)),
						Integer.parseInt(cursor.getString(2)),
						Integer.parseInt(cursor.getString(3)));
				// Adding contact to list
				cartItemDbList.add(userSelectionItemDB);
			} while (cursor.moveToNext());
		}

		// return contact list
		return cartItemDbList;
	}

	// Updating single contact
	public int updateCartItem(UserSelectionItem userSelectionItem) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		UserSelectionItemDB cidb = new UserSelectionItemDB(userSelectionItem);
		values.put(KEY_PRODUCT_ID, cidb.getProductID());
		values.put(KEY_VARIANT_ID, cidb.getVariantID());
		values.put(KEY_QUANTITY, cidb.getQuantity());
		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(cidb.getID()) });
	}

	// Deleting single contact
	public void deleteCartItem(UserSelectionItem userSelectionItem) {
		SQLiteDatabase db = this.getWritableDatabase();
		UserSelectionItemDB cidb = new UserSelectionItemDB(userSelectionItem);
		db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
				new String[] { String.valueOf(cidb.getID()) });
		db.close();
	}

	// Deleting single contact
	public void deleteCartItem(UserSelectionItemDB cidb) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
				new String[] { String.valueOf(cidb.getID()) });
		db.close();
	}

	// Getting contacts Count
	public int getCartItemCount() {
		String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

}
