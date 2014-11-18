package ema.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EUCurrencySpinner extends Spinner
{
	private boolean includeUSD = false;

	public boolean getIncludeUSD()
	{
		return includeUSD;
	}

	public void setIncludeUSD(boolean value)
	{
		includeUSD = value;
		fill();
	}

	public EUCurrencySpinner(Context context)
	{
		super(context);
		fill();
	}

	public EUCurrencySpinner(Context context, int mode)
	{
		super(context, mode);
		fill();
	}

	public EUCurrencySpinner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		parseAttributes(attrs);
		fill();
	}

	public EUCurrencySpinner(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		parseAttributes(attrs);
		fill();
	}

	public EUCurrencySpinner(Context context, AttributeSet attrs, int defStyle, int mode)
	{
		super(context, attrs, defStyle, mode);
		parseAttributes(attrs);
		fill();
	}

	private void parseAttributes(AttributeSet attrs)
	{
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EUCurrencySpinner);
		includeUSD = a.getBoolean(R.styleable.EUCurrencySpinner_includeUSD, includeUSD);
		a.recycle();
	}

	private void fill()
	{
		if (!isInEditMode())
		{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), 
					android.R.layout.simple_spinner_item, 
					includeUSD ? getResources().getStringArray(R.array.currencies_usd) : getResources().getStringArray(R.array.currencies));
			setAdapter(adapter);
		}
		else
		{
			setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{"EUR (€)"}));
		}
	}
}
