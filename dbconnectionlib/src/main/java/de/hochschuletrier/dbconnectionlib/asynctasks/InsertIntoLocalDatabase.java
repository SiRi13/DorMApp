package de.hochschuletrier.dbconnectionlib.asynctasks;

/**
 * Created by simon on 11/16/14.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.functions.RemoteHandler;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;

public class InsertIntoLocalDatabase extends AsyncTask<BasicNameValuePair, Void, JSONObject>
{
    private ProgressDialog pDialog;
    private Context appContext;
    private SecurePreferences secPrefs;
    private AuthCredentials creds;

    public InsertIntoLocalDatabase(Context context, final SecurePreferences secPrefs, AuthCredentials creds)
    {
        this.appContext = context;
        this.creds = creds;
        this.secPrefs = secPrefs;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        pDialog = new ProgressDialog(appContext);
        pDialog.setTitle("Contacting Servers");
        pDialog.setMessage("Sending data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected JSONObject doInBackground(BasicNameValuePair... params)
    {
        UserHandler userFunctions = new UserHandler();
        RemoteHandler remoteFunctions = new RemoteHandler();

        if (creds == null)
        {
            creds = userFunctions.loggedInUser(secPrefs);
        }

        String table_name = (params[0].getName().equals("table") ? params[0].getValue() : null);

        final StringBuilder keys = new StringBuilder();
        final StringBuilder vals = new StringBuilder();


        for (int i = 1; i < params.length; i++)
        {
            keys.append(String.format("`%s`, ", params[i].getName()));
            vals.append(String.format("'%s', ", params[i].getValue()));
        }
        keys.append(String.format("`%s`", Constants.KEY_UID));
        vals.append(String.format("'%s'", String.valueOf(creds.getUid())));

        Log.i("InsertIntoDB.doInBackground", "keys: " + keys.toString() + "; vals: " + vals.toString());
        JSONObject result = remoteFunctions.insertIntoRemoteTable(creds, table_name, keys.toString(), vals.toString());
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject json)
    {
        String toastText = "Something went terrible wrong :(";
        try
        {
            if (json != null && json.getString(Constants.KEY_SUCCESS) != null)
            {
                String res = json.getString(Constants.KEY_SUCCESS);
                if (Integer.parseInt(res) > 0)
                {
                    toastText = "Everything went as expected \n Data successfully transferred";
                }
            }
        }
        catch (JSONException jex)
        {
            Toast.makeText(appContext.getApplicationContext(), "Error while extracting data from server response:\nJSONException", Toast.LENGTH_LONG).show();
            jex.printStackTrace();
        }
        finally
        {
            Toast.makeText(appContext.getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }
    }

}