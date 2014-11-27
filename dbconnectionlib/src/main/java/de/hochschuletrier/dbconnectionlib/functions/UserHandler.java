package de.hochschuletrier.dbconnectionlib.functions;

import android.util.Log;

import com.securepreferences.SecurePreferences;
import com.securepreferences.SecurePreferences.Editor;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hochschuletrier.dbconnectionlib.constants.ConnectionConstants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dbconnectionlib.helper.JSONParser;

/**
 * Created by simon on 11/16/14.
 */
public class UserHandler {
    private JSONParser jsonParser;

    // constructor
    public UserHandler()
    {
        jsonParser = new JSONParser();
    }

    /**
     * Function to Login
     **/
    public JSONObject loginUser(String email, String password)
    {
        // Building Parameters
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.LOGIN_TAG));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(ConnectionConstants.LOGIN_URL, params);
        return json;
    }

    /**
     * Function to change password
     **/
    public JSONObject chgPass(String newpas, String email)
    {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.CHANGE_PASSWORD_TAG));
        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.getJSONFromUrl(ConnectionConstants.CHANGE_PASSWORD_URL, params);
        return json;
    }

    /**
     * Function to reset the password
     **/
    public JSONObject forPass(String forgotpassword)
    {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.FORGOT_PASSWORD_TAG));
        params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject json = jsonParser.getJSONFromUrl(ConnectionConstants.FORGOT_PASSWORD_URL, params);
        return json;
    }

    /**
     * Function to Register
     **/
/*    public JSONObject registerUser(String fname, String lname, String email, String uname, String password)
    {
        // Building Parameters
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", registerTag));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("uname", uname));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(registerUrl, params);
        return json;
    }*/
    /**
     * Function to logout user Resets the temporary data stored in SQLite
     * Database
     * */
/*    public boolean logoutUser(final SecurePreferences secPrefs)
    {
        // TODO change logout routine
        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(context);
        db.resetTables();
        resetCredentials(context);
        return true;
    }*/

    public void resetCredentials(final SecurePreferences secPrefs) {
        // TODO reset creds
/*        final SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_CREDENTIALS, Context.MODE_PRIVATE);
        Editor prefEditor = prefs.edit();
        prefEditor.clear();
        prefEditor.commit();*/

        Editor secPrefEditor = secPrefs.edit();
        secPrefEditor.clear();
        secPrefEditor.commit();

    }

    public void storeCredentials(final SecurePreferences secPrefs, AuthCredentials _creds) {
        // TODO save creds

        Editor secPrefEditor = secPrefs.edit();
        secPrefEditor.clear();
        secPrefEditor.putString(EnumSqLite.KEY_UID.getName(), _creds.getUid());
        secPrefEditor.putString(EnumSqLite.KEY_USERNAME.getName(), _creds.getUname() != null ? _creds.getUname() : _creds.getEmail());
        secPrefEditor.putString(EnumSqLite.KEY_PASSWORD.getName(), _creds.getPassword());
        secPrefEditor.putString(EnumSqLite.KEY_EMAIL.getName(), _creds.getEmail());
        secPrefEditor.commit();

/*        almost obsolete method
        final SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_CREDENTIALS, Context.MODE_PRIVATE);
        final Editor prefEditor = prefs.edit();
        prefEditor.clear();
        prefEditor.putInt(Constants.KEY_UID, creds.getUid());
        prefEditor.putString(Constants.KEY_USERNAME, creds.getUname());
        prefEditor.putString(Constants.KEY_PASSWORD, creds.getPassword());
        prefEditor.putString(Constants.KEY_EMAIL, creds.getEmail());
        prefEditor.putString(Constants.KEY_FORENAME, creds.getFirstname());
        prefEditor.putString(Constants.KEY_LASTNAME, creds.getLastname());
        prefEditor.commit();
        */
    }

    public static AuthCredentials loggedInUser(final SecurePreferences secPrefs) {
        String uid = null, uname = null, upassword = null, email = null;
        if (!secPrefs.getAll().isEmpty()) {
            uid = secPrefs.getString(EnumSqLite.KEY_UID.getName(), null);
            uname = secPrefs.getString(EnumSqLite.KEY_USERNAME.getName(), null);
            upassword = secPrefs.getString(EnumSqLite.KEY_PASSWORD.getName(), null);
            email = secPrefs.getString(EnumSqLite.KEY_EMAIL.getName(), null);
        }
        if (uid != null & upassword != null & email != null ) {
            AuthCredentials creds = new AuthCredentials(uid, email, upassword);
            creds.setEmail(email);
            Log.i("loggedInUser", "user: " + creds.getUname() + "; pw: " + creds.getPassword());
            return creds;
        }

        return null;
    }


}
