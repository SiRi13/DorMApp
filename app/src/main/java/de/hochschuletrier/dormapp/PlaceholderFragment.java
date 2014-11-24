package de.hochschuletrier.dormapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<Object> blackboardArray;
    private ArrayAdapter<Object> blackboardAdapter;
    private ListView lvBlackboard;
    private View rootView;

    public PlaceholderFragment() {
        // EMPTY
    }

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = null;

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
                break;
            default:
                // back to main
                rootView = inflater.inflate(R.layout.fragment_main, container, false);
                break;
                    
        }
        return rootView;
    }
}