package de.hochschuletrier.dbconnectionlib.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hochschuletrier.dbconnectionlib.constants.Constants;
import de.hochschuletrier.dbconnectionlib.constants.EnumSqLite;
import de.hochschuletrier.dbconnectionlib.constants.EnumViews;
import de.hochschuletrier.dbconnectionlib.functions.LocalHandler;
import de.hochschuletrier.dbconnectionlib.functions.RemoteHandler;
import de.hochschuletrier.dbconnectionlib.functions.UserHandler;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dbconnectionlib.helper.SqlTableRow;
import de.hochschuletrier.dbconnectionlib.messenger.MessengerService;

/**
 * Created by simon on 11/16/14.
 */
public abstract class RemoteSyncBase extends AsyncTask<Object, Message, JSONObject[]> {

    private final String TAG = Constants.TAG_PREFIX + getClass().getName();

    protected Context context;
    protected LocalHandler localHandler;
    protected UserHandler userFunctions;
    protected RemoteHandler remoteFunctions;
    protected AuthCredentials creds;

    protected Map<EnumSqLite, List<SqlTableRow>> local_tables;
    protected EnumViews[] tablesToSync;

    public RemoteSyncBase(Context _context, AuthCredentials _creds) {
        this.context = _context;
        this.creds = _creds;
        this.localHandler = new LocalHandler(_context);
        this.userFunctions = new UserHandler();
        this.remoteFunctions = new RemoteHandler();
    }

    protected void onProgressUpdate(Message _msg) {
        MessengerService.getInstance().sendMessageFromService(_msg);
    };

    protected void onPreExecute() {
        if (creds == null && creds.isEmpty()) {
            this.cancel(true);
        }
    };

    @Override
    protected JSONObject[] doInBackground(Object... params) {
        Object[] tableNames = null;
        if (tablesToSync != null && tablesToSync.length > 0) {
            tableNames = new Object[tablesToSync.length];

            for (int i = 0; i < tablesToSync.length; i++) {
                if (isCancelled()) {
                    return null;
                }
                EnumViews table = tablesToSync[i];
                tableNames[i] = table.getName();
            }
        }
        else {
            Log.v(TAG, "tablesToSync empty -> using params instead");
            tableNames = new Object[params.length];
            tablesToSync = new EnumViews[params.length];

            for (int j = 0; j < params.length; j++) {
                if (isCancelled()) {
                    return null;
                }
                Object param = params[j];
                if (param instanceof String) {
                    tableNames[j] = param;
                    tablesToSync[j] = EnumViews.valueOf(param.toString());
                }
                else if (param instanceof EnumViews) {
                    tableNames[j] = ((EnumViews) param).getName();
                    tablesToSync[j] = (EnumViews) param;
                }
            }
        }

        JSONObject[] retObjs = new JSONObject[] {};
        if ((tableNames != null && tableNames.length == 1) & !isCancelled()) {
            Log.i(TAG, " table_name: " + tableNames[0].toString());
            retObjs[0] = remoteFunctions.readRemoteTable(creds, tableNames[0].toString());
        } else if ((tableNames != null && tableNames.length > 1) & !isCancelled()) {
            List<String> tables = new ArrayList<String>();
            for (Object obj : tableNames) {
                if (isCancelled()) {
                    return null;
                }
                if (obj instanceof EnumViews) {
                    tables.add(((EnumViews) obj).getName());
                }
                if (obj instanceof String) {
                    tables.add(obj.toString());
                }
            }
            retObjs = remoteFunctions.readRemoteTable(creds, tables);
        }
        return retObjs;
    }

    protected void onPostExecute(JSONObject[] objects) {
        local_tables = new HashMap<EnumSqLite, List<SqlTableRow>>();

        List<SqlTableRow> tmpTable = new ArrayList<SqlTableRow>();
        int j = 0;
        for (JSONObject json : objects) {
            EnumViews enumViews = tablesToSync[j++];
            try {
                if (json != null && json.getString(Constants.JSON_SUCCESS) != null) {
                    String res = json.getString(Constants.JSON_SUCCESS);
                    if (Integer.parseInt(res) >= 1) {
                        EnumSqLite[] structures = enumViews.getSqLite().getStructure();
                        for (int i=0; i < Integer.parseInt(res); ++i) {
                            JSONArray json_array = json.getJSONArray("result");
                            JSONObject json_data = json_array.getJSONObject(i);
                            SqlTableRow row = new SqlTableRow();
                            for (EnumSqLite struct : structures) {
                                if (struct.equals(EnumSqLite.KEY_ID)) {
                                    continue;
                                }
                                row.put(struct.getName(), json_data.getString(struct.getName()));
                            }
                            tmpTable.add(row);
                        }
                    }
                }
            } catch (JSONException jex) {
                Log.e(TAG, "JSONException: " + jex.getLocalizedMessage());
                jex.printStackTrace();
            }
            local_tables.put(enumViews.getSqLite(), tmpTable);
            tmpTable = new ArrayList<SqlTableRow>();
        }
    };
}
