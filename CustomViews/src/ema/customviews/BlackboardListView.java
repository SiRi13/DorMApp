package ema.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by simon on 11/18/14.
 */
public class BlackboardListView extends LinearLayout {

    private ListView allItemsView;
    private ArrayList<Object> allItemsList;

    private ArrayList<Object> selectedItems;

    public BlackboardListView(Context context) {
        super(context);
        setup();
    }

    public BlackboardListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public BlackboardListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public ListAdapter getAdapter()
    {
        return allItemsView.getAdapter();
    }

    public void setAdapter(ListAdapter adapter) {
        allItemsView.setAdapter(adapter);
        resetSelection();
    }

    public Object[] getSelection()
    {
        return selectedItems.toArray();
    }

    public void setup() {

        if (!isInEditMode()) {

            inflate(getContext(), R.layout.view_blackboard_list, this);

            allItemsView = (ListView) findViewById(R.id.blackboardListView);
            allItemsView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (selectedItems.indexOf(allItemsView.getAdapter().getItem(i)) == -1) {
                        selectedItems.add(allItemsView.getAdapter().getItem(i));
                        allItemsView.setItemChecked(i,true);
                    }
                    else {
                        selectedItems.remove(allItemsView.getAdapter().getItem(i));
                        allItemsView.setItemChecked(i,false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    for (int i = 0; i < allItemsView.getAdapter().getCount(); ++i) {
                        allItemsView.setItemChecked(i, false);
                        selectedItems.clear();
                    }
                }
            });
            selectedItems = new ArrayList<Object>();
        }

    }

    private void resetSelection() {
        selectedItems.clear();
    }

}
