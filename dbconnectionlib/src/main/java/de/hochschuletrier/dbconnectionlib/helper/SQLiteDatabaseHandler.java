package de.hochschuletrier.dbconnectionlib.helper;

/**
 * Created by simon on 11/16/14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper
{

    private final String TAG = Constants.TAG_PREFIX + getClass().getName();

    public SQLiteDatabaseHandler(Context context)
    {
        super(context, EnumSqLite.DATABASE_NAME, null, EnumSqLite.DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        for (EnumSqLite table : EnumSqLite.tablesStructure) {
            Log.v(TAG, table.getName());
            db.execSQL(table.getName());
        }
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        /*if (oldVersion < 4) {
            Log.i(TAG, "DB-Version < 4; DROP TABLES: " + Constants.TABLE_LOGIN
                    + " AND " + Constants.TABLE_TEST);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_LOGIN);
            db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_TEST);
        }*/
        for (EnumSqLite table : EnumSqLite.tables) {
            Log.v(TAG, "DROP TABLE " + table.getName());
            db.execSQL("DROP TABLE IF EXISTS " + table.toString());
        }
        // Create tables again
        onCreate(db);
    }

    public void addRow(String table, String[] columns, Object[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (columns.length == values.length) {
            for (int i = 0; i < columns.length; ++i) {
                if (values[i] == null || values[i].equals("")) {
                    contentValues.put(columns[i], "blub");
                    continue;
                }
                contentValues.put(columns[i], (String) values[i]);
            }
        }
        Log.v(TAG, "db.insert(" + table + ", null, " + contentValues.toString());
        db.insert(table, null, contentValues);
        db.close();
    }

    /*
    * Clear (NOT drop) SQLite-Table
    * */
    public void clearTable(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v(TAG, "db.delete(" + table + ")");
        db.delete(table, null, null);
        db.close();
    }

    /**
     * Re create database Delete all tables and create them again
     * */
    public void resetTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        for (EnumSqLite table : EnumSqLite.tables) {
            Log.v(TAG, "db.delete(" + table + ")");
            db.delete(table.getName(), null, null);
        }
        db.close();
    }

    /*
    * OBSOLETE: moved to UserHandler
    * */
    public AuthCredentials getAuthCreds() {
        try {
            final Map<String, String> userMap = getUserDetails();
            return new AuthCredentials(userMap.get(EnumSqLite.KEY_UID),
                    userMap.get(EnumSqLite.KEY_EMAIL), userMap.get(EnumSqLite.KEY_PASSWORD));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * OBSOLETE: moved to UserHandler
     * Getting user data from database
     * */
    public Map<String, String> getUserDetails() {
        Map<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + EnumSqLite.TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            user.put(EnumSqLite.KEY_FORENAME.getName(), cursor.getString(1));
            user.put(EnumSqLite.KEY_LASTNAME.getName(), cursor.getString(2));
            user.put(EnumSqLite.KEY_EMAIL.getName(), cursor.getString(3));
            user.put(EnumSqLite.KEY_PASSWORD.getName(), cursor.getString(5));
            user.put(EnumSqLite.KEY_UID.getName(), cursor.getString(6));
            user.put(EnumSqLite.KEY_CREATED_AT.getName(), cursor.getString(7));

        }
        cursor.close();
        db.close();
        // return user
        return user;
    }
/*
PROTOTYPE
    public Cursor getTestRow(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + EnumSqLite.TABLE_TEST + " WHERE " + EnumSqLite.KEY_UID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
*/

    /**
     * OBSOLETE part of old login routine
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount(String _table) {
        String countQuery = "SELECT  * FROM " + _table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }

    /**
     * OBSOLETE: moved to UserHandler
     * Storing user details in database
     * */
    public void addUser(String fname, String lname, String email, String uname,
                        String uid, String created_at, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EnumSqLite.KEY_FORENAME.getName(), fname); // FirstName
        values.put(EnumSqLite.KEY_LASTNAME.getName(), lname); // LastName
        values.put(EnumSqLite.KEY_EMAIL.getName(), email); // Email
        values.put(EnumSqLite.KEY_PASSWORD.getName(), password); //password
        values.put(EnumSqLite.KEY_UID.getName(), uid); // Email
        values.put(EnumSqLite.KEY_CREATED_AT.getName(), created_at); // Created At
        // Inserting Row
        db.insert(EnumSqLite.TABLE_LOGIN.getName(), null, values);
        db.close(); // Closing database connection
    }

    public Cursor getReadableCursorTable(String tableName, String[] columns, String selection,
                                         String[] selArgs, String groupBy, String having,
                                         String orderBy, String limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor retCursor = db.query(tableName, columns, selection, selArgs, groupBy, having, orderBy, limit);
        return retCursor;
    }

    public Cursor getReadableCursorQuery(String query, String[] selArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor retCursor = db.rawQuery(query, selArgs);
//        db.close();
        return retCursor;
    }
}