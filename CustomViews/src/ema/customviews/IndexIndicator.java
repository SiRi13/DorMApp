package ema.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class IndexIndicator extends View
{
	private int maximum;

	public int getMaximum()
	{
		return maximum;
	}

	public void setMaximum(int maximum)
	{
		this.maximum = maximum;

		if (value > maximum)
		{
			value = maximum;
		}
		
		invalidate();
	}

	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		if ((value >= 1) && (value <= maximum))
		{
			this.value = value;
		}
		
		invalidate();
	}

	private Paint pen;

	public IndexIndicator(Context context)
	{
		super(context);
		setup();
	}

	public IndexIndicator(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setup();
	}

	public IndexIndicator(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setup();
	}

	private void setup()
	{
		value = 1;
		maximum = 1;

		pen = new Paint();
		pen.setStrokeWidth(0);
		pen.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int DEFAULT_WIDTH = 300;
		final int DEFAULT_HEIGHT = 30;

		int w = widthMeasureSpec;
		if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			w = DEFAULT_WIDTH;
		}

		int h = heightMeasureSpec;
		if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT)
		{
			h = DEFAULT_HEIGHT;
		}

		setMeasuredDimension(w, h);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		final int MARGIN = 10;
		
		super.onDraw(canvas);

		final int boxWidth = (getWidth() - ((maximum - 1) * MARGIN) - 2) / maximum;
		
		for (int i = 0; i < maximum; i++)
		{
			pen.setStyle(Style.STROKE);
			pen.setColor(Color.BLACK);
			
			float left = 1 + i * (boxWidth + MARGIN);
			canvas.drawRect(left, 1, left + boxWidth, getHeight() - 1, pen);
			
			if (i + 1 == value)
			{
				pen.setStyle(Style.FILL);
				pen.setColor(Color.parseColor("#33B5E5"));
				canvas.drawRect(left + 1, 2, left + boxWidth - 1, getHeight() - 2, pen);
			}
		}
	}
}
