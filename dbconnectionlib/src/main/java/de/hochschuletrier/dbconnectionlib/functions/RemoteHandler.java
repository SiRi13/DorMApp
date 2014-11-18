package de.hochschuletrier.dbconnectionlib.functions;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hochschuletrier.dbconnectionlib.constants.ConnectionConstants;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dbconnectionlib.helper.JSONParser;

/**
 * Created by simon on 11/16/14.
 */
public class RemoteHandler {


    private final JSONParser jsonParser;

    public RemoteHandler() {
        jsonParser = new JSONParser();
    }

    /**
     * Function to query Remote Table
     */
    public JSONObject syncRemoteTable(AuthCredentials creds, String table)
    {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.SYNC_TAG));
        params.add(new BasicNameValuePair("email", creds.getEmail()));
        params.add(new BasicNameValuePair("password", creds.getPassword()));
        params.add(new BasicNameValuePair("table", table));

        return syncRemoteParams(creds, params);
    }

    public JSONObject syncRemoteParams(AuthCredentials creds, List<BasicNameValuePair> params) {
        JSONObject json = jsonParser.getJSONFromUrl(ConnectionConstants.SYNC_URL, params);
        return json;
    }

    public JSONObject insertIntoRemoteTable(AuthCredentials creds, String table_name, String culmns, String vals)
    {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.WRITE_TAG));
        params.add(new BasicNameValuePair("table", table_name));
        params.add(new BasicNameValuePair("culmns", culmns));
        params.add(new BasicNameValuePair("values", vals));
        params.add(new BasicNameValuePair("email", creds.getEmail()));
        params.add(new BasicNameValuePair("password", creds.getPassword()));

        return jsonParser.getJSONFromUrl(ConnectionConstants.WRITE_URL, params);
    }

}
