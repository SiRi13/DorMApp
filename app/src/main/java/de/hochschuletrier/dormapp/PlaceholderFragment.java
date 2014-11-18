package de.hochschuletrier.dormapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Objects;

import ema.customviews.BlackboardListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment
{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<Object> blackboardArray;
    private ArrayAdapter<Object> blackboardAdapter;
    private ListView lvBlackboard;
    private View rootView;
    
    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber)
    {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = null;
        blackboardArray = new ArrayList<Object>();
        blackboardArray.add("Eintrag 1");
        blackboardArray.add("Eintrag 2");
        
        
        switch (getArguments().getInt(ARG_SECTION_NUMBER) - 1) {
            case 0: 
                // Uebersicht
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                break;
            case 1:
                // Putzplan
                rootView = inflater.inflate(R.layout.fragment_putzplan, container, false);
                break;
            case 2:
                // Einkaufsliste
                rootView = inflater.inflate(R.layout.fragment_einkaufsliste, container, false);
                break;
            case 3:
                // Blackboard
                rootView = inflater.inflate(R.layout.fragment_blackboard, container, false);
                ((BlackboardListView) rootView.findViewById(R.id.blackboardList)).setAdapter(new ArrayAdapter<Object>(rootView.getContext(), android.R.layout.two_line_list_item, new String[] { "Nachricht Eins", "Nachricht Zwei", "Nachricht Drei" }));
                break;
            default:
                // back to main
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                break;
                    
        }
        blackboardArray = null;
        blackboardAdapter = null;
        return rootView;
    }
    
    /***********************************************************************/
    /**                                                                   **/
    /**                           Blackboard                              **/
    /**  Verarbeiten von Clicks auf dem R.layout.fragment_blackboard      **/
    /**                                                                   **/
    /***********************************************************************/
    
/*    public void onBlackboardAddClick(final View v) {
        final EditText editTextBlackboardNew = (EditText) findViewById(R.id.editTextBlackboardNew);
        final String newMsg = editTextBlackboardNew.getText().toString();
        if (!newMsg.equals("")) {
            editTextBlackboardNew.setText("");
            //TODO save newMsg to DB
            
            ArrayList<String> newStringArray = new ArrayList<String>();
            newStringArray.add(newMsg);
            CustomAdapter cAdapter = new CustomAdapter(this, R.layout.row, newStringArray);
            cAdapter.notifyDataSetChanged();
            lvBlackboard.setAdapter(cAdapter);
        }
        else {
            Toast.makeText(getApplicationContext(), 
                "Fehler beim Auslesen des neuen Eintrags. Bitte erneut versuchen!" , 
                Toast.LENGTH_LONG).show();
        }
    }*/
    
    public void onBlackboardRemoveClick(final View v) {
        //TODO
        final ListView lvBlackboard = (ListView) v.getParent().getParent();
        RelativeLayout relLayout = (RelativeLayout) v.getParent();
        TextView txtView = null;
        
        if (relLayout.getChildAt(0) instanceof TextView) {
            txtView = (TextView) relLayout.getChildAt(0);
        }
        
        final int positionToRemove = lvBlackboard.getPositionForView(txtView);
        String text = txtView.getText().toString();
        
        
        AlertDialog.Builder adb=new AlertDialog.Builder(getActivity());
        adb.setTitle(getResources().getString(R.string.RowDelete));
        adb.setMessage("Are you sure you want to delete " + positionToRemove + "( " + text + " )");
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row, R.id.textViewMainHallo, refreshArray(lvBlackboard.getAdapter(),positionToRemove));
                cAdapter.notifyDataSetChanged();
                lvBlackboard.setAdapter(cAdapter);
            }});
        AlertDialog ad = adb.create();
        ad.show();
        
    }
    
    protected ArrayList<String> refreshArray(ListAdapter lstAdapter, int positionToRemove)
    {
        ArrayList<String> retVal = new ArrayList<String>();
        for (int i = 0; i < lstAdapter.getCount(); ++i) {
            if (i == positionToRemove) {
                continue;
            }
            retVal.add(lstAdapter.getItem(i).toString());
        }
        return retVal;
    }
}