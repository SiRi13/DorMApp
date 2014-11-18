package ema.customviews;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class MasterToggleButton extends ToggleButton
{
	private ArrayList<ToggleButton> slaves = new ArrayList<ToggleButton>();

	public void addSlave(ToggleButton slave)
	{
		if (!slaves.contains(slave))
		{
			slaves.add(slave);
			updateSlaves();
		}
	}

	public void removeSlave(ToggleButton slave)
	{
		slaves.remove(slave);
	}

	private boolean disableSlaves = false;

	public boolean isDisableSlaves()
	{
		return disableSlaves;
	}

	public void setDisableSlaves(boolean disableSlaves)
	{
		this.disableSlaves = disableSlaves;
		updateSlaves();
	}

	public MasterToggleButton(Context context)
	{
		super(context);
	}

	public MasterToggleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		parseAttributes(attrs);
	}

	public MasterToggleButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		parseAttributes(attrs);
	}

	private void parseAttributes(AttributeSet attrs)
	{
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MasterToogleButton);
		disableSlaves = a.getBoolean(R.styleable.MasterToogleButton_disableSlaves, disableSlaves);
		a.recycle();
	}

	@Override
	protected void drawableStateChanged()
	{
		super.drawableStateChanged();

		if (slaves != null)
		{
			updateSlaves();
		}
	}

	private void updateSlaves()
	{
		for (ToggleButton slave : slaves)
		{
			slave.setChecked(isChecked());
			slave.setClickable(!disableSlaves);

			if ((slave instanceof MasterToggleButton) && (disableSlaves))
			{
				((MasterToggleButton) slave).setDisableSlaves(true);
			}
		}
	}
}
