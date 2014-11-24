package de.hochschuletrier.dormapp.common;

import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hochschuletrier.dbconnectionlib.asynctasks.RemoteSync;
import de.hochschuletrier.dbconnectionlib.helper.AuthCredentials;
import de.hochschuletrier.dormapp.MainActivity;
import de.hochschuletrier.dormapp.R;

/**
 * Created by simon on 11/16/14.
 *
 * TODO: move to service!!
 */
public class InitAppSync extends RemoteSync {

    public InitAppSync(MainActivity _context, final SecurePreferences _secPrefs, AuthCredentials _creds) {
        super(_context, _secPrefs, _creds);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        MainActivity mainAct = ((MainActivity) context);
        String txtHallo = mainAct.getString(R.string.textHallo, values[1]);
        String txtChores = values[2].equals("") ?
                            mainAct.getString(R.string.textDuHastFrei) :
                            mainAct.getString(R.string.textDuBistDran, values[2]);
        String txtGroceries = mainAct.getString(R.string.textKeinNeuerEintragEinkaufsliste);
        String txtBlackboard = mainAct.getString(R.string.textKeinNeuerEintragBlackboard);
        String txtCalendar = mainAct.getString(R.string.textKeintNeuerEintragKalender);


        int anzahl = -1;
        if (!values[3].equals("")) {
            anzahl = Integer.parseInt(values[3].toString());
            txtGroceries = anzahl > 1 ?
                    mainAct.getString(R.string.textNeueEintraegeEinkaufsliste, values[3]) :
                    mainAct.getString(R.string.textNeuerEintragEinkaufsliste);
        }
        if (!values[4].equals("")) {
            anzahl = Integer.parseInt(values[4].toString());
            txtBlackboard = anzahl > 1 ?
                    mainAct.getString(R.string.textNeueEintraegeBlackboard, values[4]) :
                    mainAct.getString(R.string.textNeuerEintragBlackboard);
        }
        if (!values[5].equals("")) {
            anzahl = Integer.parseInt(values[5].toString());
            txtCalendar = anzahl > 1 ?
                    mainAct.getString(R.string.textNeueEintraegeKalender, values[5]) :
                    mainAct.getString(R.string.textNeuerEintragKalender);
        }

        TextView txtViewHallo = (TextView) mainAct.findViewById(R.id.textViewMainHallo);
        TextView txtViewChores = (TextView) mainAct.findViewById(R.id.textViewMainChores);
        TextView txtViewBlackboard = (TextView) mainAct.findViewById(R.id.textViewMainBlackboard);
        TextView txtViewGroceries = (TextView) mainAct.findViewById(R.id.textViewMainGroceries);
        TextView txtViewCalendar = (TextView) mainAct.findViewById(R.id.textViewMainCalendar);

        txtViewHallo.setText(txtHallo);
        txtViewChores.setText(txtChores);
        txtViewBlackboard.setText(txtBlackboard);
        txtViewGroceries.setText(txtGroceries);
        txtViewCalendar.setText(txtCalendar);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        try {
            if (jsonObject != null && jsonObject.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_SUCCESS) != null) {
                String res = jsonObject.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_SUCCESS);
                if (Integer.parseInt(res) >= 1)
                {
                    JSONArray json_array = jsonObject.getJSONArray("result");
                    /**
                     * Clear all previous data in SQlite database.
                     **/
                    dbHandler.dropTable(de.hochschuletrier.dbconnectionlib.constants.Constants.TABLE_INIT_APP);

                    JSONObject json_data = json_array.getJSONObject(0);
                    String[] columns = new String[]{ de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_UID, de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_FIRSTNAME,
                            de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CHORES, de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_GROCERIES_COUNT,
                            de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CALENDAR_COUNT, de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_BLACKBOARD_COUNT,
                            de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CREATED_AT};
                    ArrayList<Object> values = new ArrayList<Object>();
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_UID));
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_FIRSTNAME));
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CHORES));
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_GROCERIES_COUNT));
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_BLACKBOARD_COUNT));
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CALENDAR_COUNT));
                    values.add(json_data.getString(de.hochschuletrier.dbconnectionlib.constants.Constants.KEY_CREATED_AT));

                    dbHandler.addRow(de.hochschuletrier.dbconnectionlib.constants.Constants.TABLE_INIT_APP, columns, values.toArray());

                    this.publishProgress(values.toArray());
                }
            }
        }
        catch (JSONException e) {
            Toast.makeText(context.getApplicationContext(), "Error while extracting data from server response:\nJSONException", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
