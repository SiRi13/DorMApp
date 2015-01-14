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

import de.hochschuletrier.dbconnectionlib.common.GroceryItem;
import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;

import static de.hochschuletrier.dbconnectionlib.constants.EnumSqLite.KEY_SHOPPING_LIST_ID;
import static de.hochschuletrier.dbconnectionlib.constants.EnumSqLite.TABLE_SHOPPING_LIST;

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

    public Long addRow(String table, String[] columns, Object[] values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (columns.length == values.length) {
            contentValues = getContentVals(columns, values);
        }
        Log.v(TAG, "db.insert(" + table + ", null, " + contentValues.toString());
        Long id = db.insert(table, null, contentValues);
        db.close();
        return id;
    }

    private ContentValues getContentVals(final String[] _clmns, final Object[] _vals) {
        final ContentValues retCntnVals = new ContentValues();
        for (int i = 0; i < _clmns.length; ++i) {
            if (_vals[i] == null || _vals[i].equals("")) {
                retCntnVals.put(_clmns[i], "blub");
                continue;
            }
            retCntnVals.put(String.valueOf(_clmns[i]), String.valueOf(_vals[i]));
        }
        return retCntnVals;
    }

    public boolean updateRow(String table, String[] columns, Object[] values, String[] whereColumns, Object[] where_args ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cntnVals = new ContentValues();
        if (columns != null && columns.length > 0) {
            cntnVals = getContentVals(columns, values);
        }

        String whereClause = null;
        String[] whereArgs = new String[whereColumns.length];
        if (whereColumns != null) {
            whereClause = whereColumns[0] + " = ? ";
            whereArgs[0] = where_args[0].toString();
            for (int i=1; i < whereColumns.length; ++i) {
                whereClause += " AND " + whereColumns[i] + " = ? ";
                whereArgs[i] = where_args[i].toString();
            }
        }

        return (db.update(table, cntnVals, whereClause, whereArgs) > 0);
    }

    public Long removeRow(String table, String whereClause, String[] whereArgs) {
        if (table != null && whereClause != null && whereArgs != null) {
            final SQLiteDatabase db = this.getWritableDatabase();
            db.delete(table, whereClause, whereArgs);
        }
        return -1L;
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
        String selectQuery = "SELECT  * FROM "; //+ //EnumSqLite.TABLE_LOGIN;
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

    public int getOpenChoreSteps(final int _chorePlanChoreId) {
        if (_chorePlanChoreId > 0) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT count(*) FROM " + EnumSqLite.TABLE_CHORE_PLAN_STEPS.getName()
                    + " cps WHERE cps.putzplan_aufgaben_id = ? ";
            Cursor crs = db.rawQuery(query, new String[] { String.valueOf(_chorePlanChoreId) });
            if (crs.moveToFirst()) {
                return crs.getInt(0);
            }
        }
        return -1;
    }

    public Cursor getRow(String table, long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + table + " WHERE " + EnumSqLite.KEY_UID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

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
//        db.insert(EnumSqLite.TABLE_LOGIN.getName(), null, values);
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

    public String[] getGrocieries() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(EnumSqLite.TABLE_GROCERIES.getName(),
                new String[] { EnumSqLite.KEY_GROCERY_NAME.getName() },
                null, null, null, null, null);

        if (result.getCount() > 0) {
            String[] str = new String[result.getCount()];
            int nameIdx = result.getColumnIndex(EnumSqLite.KEY_GROCERY_NAME.getName());
            int i = 0;
            while (result.moveToNext()) {
                str[i++] = result.getString(nameIdx);
            }
            return str;
        }
        return null;
    }

    public GroceryItem getItemIdFromName(String _itemName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result;

        String[] cols = new String[] { EnumSqLite.KEY_GROCERY_ID.getName(), EnumSqLite.KEY_GROCERY_NAME.getName(), EnumSqLite.KEY_GROCERY_LAST_PRICE.getName() };
        String sel = "LOWER(" + EnumSqLite.KEY_GROCERY_NAME.getName() + ") LIKE LOWER('%"+_itemName.trim()+"%')";

        result = db.query(EnumSqLite.TABLE_GROCERIES.getName(), cols, sel, null, null, null, null);
        if (result.moveToFirst()) {
            int idx = result.getColumnIndex(EnumSqLite.KEY_GROCERY_ID.getName());
            int nameIdx = result.getColumnIndex(EnumSqLite.KEY_GROCERY_NAME.getName());
            int priceIdx = result.getColumnIndex(EnumSqLite.KEY_GROCERY_LAST_PRICE.getName());
            return new GroceryItem(result.getInt(idx), result.getString(nameIdx), result.getDouble(priceIdx));
        }

        return null;
    }

    public int getShoppingListId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT DISTINCT " + KEY_SHOPPING_LIST_ID.getName() + " FROM " + TABLE_SHOPPING_LIST.getName(), null);
        if (result.moveToFirst() && result.getCount() == 1) {
            return result.getInt(0);
        }
        return 0;
    }
}