package ema.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

public class NumPad extends GridLayout implements View.OnClickListener
{
	public interface OnNumClickListener
	{
		public void onNumClick(View v, char num);
	}

	private OnNumClickListener mOnNumClickListener = null;

	public void setOnNumClickListener(OnNumClickListener l)
	{
		mOnNumClickListener = l;
	}

	public NumPad(Context context)
	{
		super(context);
		setup();
	}

	public NumPad(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setup();
	}

	public NumPad(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setup();
	}

	private void setup()
	{
		if (!isInEditMode())
		{
			inflate(getContext(), R.layout.view_numpad, this);

			setOrientation(HORIZONTAL);
			setColumnCount(3);

			findViewById(R.id.button_0).setOnClickListener(this);
			findViewById(R.id.button_1).setOnClickListener(this);
			findViewById(R.id.button_2).setOnClickListener(this);
			findViewById(R.id.button_3).setOnClickListener(this);
			findViewById(R.id.button_4).setOnClickListener(this);
			findViewById(R.id.button_5).setOnClickListener(this);
			findViewById(R.id.button_6).setOnClickListener(this);
			findViewById(R.id.button_7).setOnClickListener(this);
			findViewById(R.id.button_8).setOnClickListener(this);
			findViewById(R.id.button_9).setOnClickListener(this);
			findViewById(R.id.button_dot).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v)
	{
		if (mOnNumClickListener != null)
		{
			mOnNumClickListener.onNumClick(this, ((Button) v).getText().charAt(0));
		}
	}
}
