package ema.customviews;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListSelectionView extends LinearLayout
{
	private ListView allItemsView;

	private ArrayList<Object> selectedItems;
	private ArrayAdapter<Object> selectedItemsAdapter;
	private ListView selectedItemsView;

	public ListAdapter getAdapter()
	{
		return allItemsView.getAdapter();
	}

	public void setAdapter(ListAdapter adapter)
	{
		allItemsView.setAdapter(adapter);
		resetSelection();
	}

	public Object[] getSelection()
	{
		return selectedItems.toArray();
	}

	public ListSelectionView(Context context)
	{
		super(context);
		setup();
	}

	public ListSelectionView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setup();
	}

	public ListSelectionView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setup();
	}

	private void setup()
	{
		if (!isInEditMode())
		{
			// inflate layout from XML file
			inflate(getContext(), R.layout.view_listselection, this);

			allItemsView = (ListView) findViewById(R.id.allItemsView);
			selectedItemsView = (ListView) findViewById(R.id.selectedItemsView);

			// add click listeners
			allItemsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
				{
					// do not allow duplicates
					if (selectedItems.indexOf(allItemsView.getAdapter().getItem(position)) == -1)
					{
						selectedItems.add(allItemsView.getAdapter().getItem(position));
						selectedItemsAdapter.notifyDataSetChanged();
						selectedItemsView.setItemChecked(selectedItems.size() - 1, true);
					}
				}
			});

			// delete on long click
			selectedItemsView.setLongClickable(true);
			selectedItemsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id)
				{
					// remove clicked item
					selectedItems.remove(position);
					selectedItemsAdapter.notifyDataSetChanged();
					selectedItemsView.clearChoices();
					return true;
				}
			});

			findViewById(R.id.moveUpButton).setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					int selectedItemPos = selectedItemsView.getCheckedItemPosition();
					if ((selectedItemPos != ListView.INVALID_POSITION) && (selectedItemPos > 0))
					{
						// swap items
						Collections.swap(selectedItems, selectedItemPos, selectedItemPos - 1);

						selectedItemsAdapter.notifyDataSetChanged();
						selectedItemsView.setItemChecked(selectedItemPos - 1, true);
					}
				}
			});

			findViewById(R.id.moveDownButton).setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					int selectedItemPos = selectedItemsView.getCheckedItemPosition();
					if ((selectedItemPos != ListView.INVALID_POSITION) && (selectedItemPos < selectedItems.size() - 1))
					{
						// swap items
						Collections.swap(selectedItems, selectedItemPos, selectedItemPos + 1);

						selectedItemsAdapter.notifyDataSetChanged();
						selectedItemsView.setItemChecked(selectedItemPos + 1, true);
					}
				}
			});

			// setup empty selection list
			selectedItems = new ArrayList<Object>();
			selectedItemsAdapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_list_item_single_choice, selectedItems);
			selectedItemsView.setAdapter(selectedItemsAdapter);
		}
	}

	public void resetSelection()
	{
		selectedItems.clear();
		selectedItemsAdapter.notifyDataSetChanged();
	}
}
