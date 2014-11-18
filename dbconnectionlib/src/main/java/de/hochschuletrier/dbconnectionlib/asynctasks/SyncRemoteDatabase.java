package de.hochschuletrier.dbconnectionlib.asynctasks;

/**
 * Created by simon on 11/16/14.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.functions.RemoteHandler;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;

public class SyncRemoteDatabase extends RemoteSync {
    private ProgressDialog pDialog;

    public SyncRemoteDatabase(Context appContext,final SecurePreferences _secPrefs, AuthCredentials creds)
    {
        super(appContext, _secPrefs, creds);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setTitle("Contacting Servers");
        pDialog.setMessage("Query database ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    };

    protected JSONObject doInBackground(String... params)
    {
        UserHandler userFunctions = new UserHandler();
        RemoteHandler remoteFunctions = new RemoteHandler();
        if (creds == null)
        {
            creds = userFunctions.loggedInUser(secPrefs);
        }
        JSONObject json = remoteFunctions.syncRemoteTable(creds, params[0]);
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject json)
    {
        try
        {
            if (json != null && json.getString(Constants.KEY_SUCCESS) != null)
            {
                String res = json.getString(Constants.KEY_SUCCESS);
                if (Integer.parseInt(res) >= 1)
                {
                    pDialog.setMessage("Loading Test Data");
                    pDialog.setTitle("Getting Data");
//                    DatabaseHandler db = new DatabaseHandler(appContext.getApplicationContext());
                    JSONArray json_array = json.getJSONArray("result");
                    /**
                     * Clear all previous data in SQlite database.
                     **/
                    dbHandler.dropSyncTable();
                    for (int i = 0; i < Integer.parseInt(res); i++)
                    {
                        JSONObject json_data = json_array.getJSONObject(i);
/*
                        dbHandler.addRow(json_data.getInt(Constants.KEY_UID), json_data.getString(Constants.KEY_TEST_STRING), json_data.getInt(Constants.KEY_TEST_INT), json_data.getString(Constants.KEY_CREATED_AT));
*/
                    }
                    /**
                     * If JSON array details are stored in SQlite it
                     * launches the User Panel.
                     **/
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
            pDialog.dismiss();
        }

    }
}