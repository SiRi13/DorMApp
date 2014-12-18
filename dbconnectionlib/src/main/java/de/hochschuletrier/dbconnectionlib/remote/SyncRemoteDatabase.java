/*
package de.hochschuletrier.dbconnectionlib.remote;

*/
/**
 * Created by simon on 11/16/14.
 *
 * OBSOLETE (should be :E)
 *//*


import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;

public class SyncRemoteDatabase extends RemoteSync {
    private static final String TAG = Constants.TAG_PREFIX + "SyncRemoteDatabase";

    private ProgressDialog pDialog;

    public SyncRemoteDatabase(Context appContext, AuthCredentials creds)
    {
        super(appContext, creds);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
*/
/*        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Contacting Servers");
        pDialog.setMessage("Query database ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();*//*

    };

*/
/*
    protected JSONObject doInBackground(String... params)
    {
        UserHandler userFunctions = new UserHandler();
        RemoteHandler remoteFunctions = new RemoteHandler();
        if (creds == null)
        {
            // TODO error
        }
        JSONObject json = remoteFunctions.syncRemoteTable(creds, params[0]);

        return json;
    }
*//*


    @Override
    protected void onPostExecute(JSONObject json)
    {
        try
        {
            if (json != null && json.getString(Constants.JSON_SUCCESS) != null)
            {
                String res = json.getString(Constants.JSON_SUCCESS);
                if (Integer.parseInt(res) >= 1)
                {
*/
/*                    pDialog.setMessage("Loading Test Data");
                    pDialog.setTitle("Getting Data");*//*

//                    SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(appContext.getApplicationContext());
                    JSONArray json_array = json.getJSONArray("result");
                    */
/**
                     * Clear all previous data in SQlite database.
                     **//*

//                    dbHandler.clearTable(table_name);
                    for (int i = 0; i < Integer.parseInt(res); i++)
                    {
                        JSONObject json_data = json_array.getJSONObject(i);
                        */
/*dbHandler.addRow(json_data.getInt("uid"), json_data.getString(Constants.KEY_TEST_STRING), json_data.getInt(Constants.KEY_TEST_INT), json_data.getString(EnumSqLite.KEY_CREATED_AT.getName()));*//*

                        String[] columns = new String[] { EnumSqLite.KEY_UID.getName(), EnumSqLite.KEY_FORENAME.getName(), EnumSqLite.KEY_CHORES.getName(), EnumSqLite.KEY_CALENDAR_COUNT.getName(), EnumSqLite.KEY_GROCERIES_COUNT.getName(), EnumSqLite.KEY_BLACKBOARD_COUNT.getName(), EnumSqLite.KEY_CREATED_AT.getName() };
                        String test_string = json_data.getString(Constants.KEY_TEST_STRING);
                        String test_int = json_data.getString(Constants.KEY_TEST_INT);
                        Object[] values = new Object[] { json_data.getString("uid"), test_string, test_string, test_int, test_int, test_int, json_data.getString("created_at") };
//                        dbHandler.addRow(table_name, columns, values);
                    }
                    */
/**
                     * If JSON array details are stored in SQlite it
                     * launches the User Panel.
                     **//*

                }
            }
        }
        catch (JSONException e)
        {
            Toast.makeText(context.getApplicationContext(), "Error while extracting data from server response:\nJSONException", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finally
        {
//            pDialog.dismiss();
        }

    }
}*/
