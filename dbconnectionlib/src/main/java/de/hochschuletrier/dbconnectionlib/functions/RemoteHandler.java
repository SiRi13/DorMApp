package de.hochschuletrier.dbconnectionlib.functions;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hochschuletrier.dbconnectionlib.constants.ConnectionConstants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;
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
     * Function to query a remote table
     */
    public JSONObject readRemoteTable(AuthCredentials _creds, String table) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.SYNC_TAG));
        params.add(new BasicNameValuePair("email", _creds.getEmail()));
        params.add(new BasicNameValuePair("password", _creds.getPassword()));
        params.add(new BasicNameValuePair("table", table));

        return postRemote(ConnectionConstants.SYNC_URL ,params);
    }

    /*
    * Function to query multiple remote tables
    * */
    public JSONObject[] readRemoteTable(AuthCredentials _creds, List<String> tables) {
        JSONObject[] retObjs = new JSONObject[tables.size()];
        int i = 0;
        for (String table : tables) {
            retObjs[i++] = readRemoteTable(_creds, table);
        }

        return retObjs;
    }

    public JSONObject postRemote(String _url, List<BasicNameValuePair> _params) {
        JSONObject json = jsonParser.getJSONFromUrl(_url, _params);
        return json;
    }

    /*
    * Function to insert into remote table
    * */
    public JSONObject insertIntoRemoteTable(AuthCredentials _creds, String table_name, String culmns, String vals) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("tag", ConnectionConstants.WRITE_TAG));
        params.add(new BasicNameValuePair("table", table_name));
        params.add(new BasicNameValuePair("culmns", culmns));
        params.add(new BasicNameValuePair("values", vals));
        params.add(new BasicNameValuePair("email", _creds.getEmail()));
        params.add(new BasicNameValuePair("password", _creds.getPassword()));

        return postRemote(ConnectionConstants.WRITE_URL, params);
    }

    public List<BasicNameValuePair> getParameterList(String _tag, AuthCredentials _creds, Map<String, String> _map) {
        List<BasicNameValuePair> retList = new ArrayList<BasicNameValuePair>();

        retList.add(new BasicNameValuePair("tag",_tag));
        retList.add(new BasicNameValuePair(EnumSqLite.KEY_EMAIL.getName(), _creds.getEmail()));
        retList.add(new BasicNameValuePair(EnumSqLite.KEY_PASSWORD.getName(), _creds.getPassword()));

        for (String key : _map.keySet()) {
            retList.add(new BasicNameValuePair(key, _map.get(key)));
        }

        return retList;
    }
}
