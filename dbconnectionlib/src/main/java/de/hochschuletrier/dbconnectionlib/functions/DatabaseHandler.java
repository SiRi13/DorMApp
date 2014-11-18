package de.hochschuletrier.dbconnectionlib.functions;

/**
 * Created by simon on 11/16/14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;

public class DatabaseHandler extends SQLiteOpenHelper
{


    public DatabaseHandler(Context context)
    {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + Constants.TABLE_LOGIN + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_FIRSTNAME + " TEXT," + Constants.KEY_LASTNAME + " TEXT," + Constants.KEY_EMAIL + " TEXT UNIQUE," + Constants.KEY_USERNAME + " TEXT," + Constants.KEY_PASSWORD + " TEXT," + Constants.KEY_UID + " TEXT," + Constants.KEY_CREATED_AT + " TEXT" + ")";
        String CREATE_TEST_TABLE = "CREATE TABLE " + Constants.TABLE_TEST + "(" + Constants.TEST_ID + " INTEGER PRIMARY KEY, " + Constants.KEY_UID + " INTEGER, " + Constants.KEY_TEST_STRING + " TEXT," + Constants.KEY_TEST_INT + " INTEGER," + Constants.KEY_CREATED_AT + " TEXT)";
        String CREATE_INIT_TABLE = "CREATE TABLE " + Constants.TABLE_INIT_APP + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY, " + Constants.KEY_UID + " TEXT, " + Constants.KEY_FIRSTNAME + " TEXT," + Constants.KEY_CHORES + " TEXT," + Constants.KEY_CALENDAR_COUNT + " TEXT," + Constants.KEY_GROCERIES_COUNT + " TEXT," + Constants.KEY_BLACKBOARD_COUNT + " TEXT," + Constants.KEY_CREATED_AT + " TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_TEST_TABLE);
        db.execSQL(CREATE_INIT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_TEST);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_INIT_APP);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String fname, String lname, String email, String uname, String uid, String created_at, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_FIRSTNAME, fname); // FirstName
        values.put(Constants.KEY_LASTNAME, lname); // LastName
        values.put(Constants.KEY_EMAIL, email); // Email
        values.put(Constants.KEY_USERNAME, uname); // UserName
        values.put(Constants.KEY_PASSWORD, password); //password
        values.put(Constants.KEY_UID, uid); // Email
        values.put(Constants.KEY_CREATED_AT, created_at); // Created At
        // Inserting Row
        db.insert(Constants.TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    public void addRow(String table, String[] culomns, Object[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues vals = new ContentValues();

        if (culomns.length == values.length) {
            for (int i = 0; i < culomns.length; ++i) {
                if (values[i] == null || values[i].equals("")) {
                    vals.put(culomns[i], "blub");
                    continue;
                }
                vals.put(culomns[i], (String) values[i]);
            }
        }

        db.insert(table, null, vals);
        db.close();
    }

    public AuthCredentials getAuthCreds() {
        try {
            final Map<String, String> userMap = getUserDetails();
            return new AuthCredentials(userMap.get(Constants.KEY_UID), userMap.get(Constants.KEY_EMAIL), userMap.get(Constants.KEY_PASSWORD));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Getting user data from database
     * */
    public Map<String, String> getUserDetails()
    {
        Map<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + Constants.TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            user.put(Constants.KEY_FIRSTNAME, cursor.getString(1));
            user.put(Constants.KEY_LASTNAME, cursor.getString(2));
            user.put(Constants.KEY_EMAIL, cursor.getString(3));
            user.put(Constants.KEY_USERNAME, cursor.getString(4));
            user.put(Constants.KEY_PASSWORD, cursor.getString(5));
            user.put(Constants.KEY_UID, cursor.getString(6));
            user.put(Constants.KEY_CREATED_AT, cursor.getString(7));

        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    public Cursor getTestRow(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_TEST + " WHERE " + Constants.KEY_UID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount()
    {
        String countQuery = "SELECT  * FROM " + Constants.TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }

    public void dropSyncTable()
    {
        this.dropTable(Constants.TABLE_TEST);
    }

    public void dropTable(String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null);
        db.close();
    }

    /**
     * Re create database Delete all tables and create them again
     * */
    public void resetTables()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(Constants.TABLE_LOGIN, null, null);
        db.delete(Constants.TABLE_TEST, null, null);
        db.delete(Constants.TABLE_INIT_APP, null, null);
        db.close();
    }
}