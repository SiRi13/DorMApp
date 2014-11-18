package de.hochschuletrier.dbconnectionlib.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import java.util.List;

import de.hochschuletrier.dbconnectionlib.functions.DatabaseHandler;
import de.hochschuletrier.dbconnectionlib.functions.RemoteHandler;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;

/**
 * Created by simon on 11/16/14.
 */
public abstract class RemoteSync extends AsyncTask<Object, Object, JSONObject> {

    protected final Context context;
    protected final DatabaseHandler dbHandler;
    protected final UserHandler userFunctions;
    protected final RemoteHandler remoteFunctions;
    protected final SecurePreferences secPrefs;
    protected AuthCredentials creds;

    public RemoteSync(Context _context, final SecurePreferences _secPrefs, AuthCredentials _creds) {
        this.context = _context;
        this.creds = _creds;
        this.secPrefs = _secPrefs;
        this.dbHandler = new DatabaseHandler(_context);
        this.userFunctions = new UserHandler();
        this.remoteFunctions = new RemoteHandler();
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        if (creds == null) {
            creds = userFunctions.loggedInUser(secPrefs);
        }
        if (params[0] instanceof String && !(params[0] instanceof List)) {
            return remoteFunctions.syncRemoteTable(creds, params[0].toString());
        }
        return remoteFunctions.syncRemoteParams(creds, (List) params[0]);
    }

}
