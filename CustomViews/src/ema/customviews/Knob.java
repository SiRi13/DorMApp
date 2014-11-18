package ema.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class Knob extends View
{
	private final static int ARC_ANGLE = 300;
	private final static int ARC_START = 90 + (360 - ARC_ANGLE) / 2;
	private final static int ARC_STROKE_WIDTH = 2;

	private final static int KNOB_RADIUS = 10;
	private final static int KNOB_PADDING = 20;
	private final static int KNOB_STROKE_WIDTH = 5;

	private Paint pen;
	private RectF boundingBox;

	private double value;

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		// clamp value to [0;1]
		this.value = Math.max(0, Math.min(value, 1));

		invalidate();

		if (mOnValueChangedListener != null)
		{
			mOnValueChangedListener.onValueChanged(this, this.value);
		}
	}

	public interface OnValueChangedListener
	{
		public void onValueChanged(View v, double value);
	}

	private OnValueChangedListener mOnValueChangedListener = null;

	public void setOnValueChangedListener(OnValueChangedListener l)
	{
		mOnValueChangedListener = l;
	}

	public Knob(Context context)
	{
		super(context);
		setup();
	}

	public Knob(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setup();
	}

	public Knob(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		setup();
	}

	private void setup()
	{
		pen = new Paint();
		pen.setColor(Color.BLACK);
		pen.setAntiAlias(true);

		boundingBox = new RectF();

		value = 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		final int DEFAULT_WIDTH = 300;
		final int DEFAULT_HEIGHT = 300;

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
		super.onDraw(canvas);

		final int cx = getWidth() / 2;
		final int cy = getHeight() / 2;
		final int r = Math.min(cx, cy) - ARC_STROKE_WIDTH / 2;
		boundingBox.set(cx - r, cy - r, cx + r, cy + r);

		// draw centered arc
		pen.setStyle(Style.STROKE);
		pen.setStrokeWidth(ARC_STROKE_WIDTH);
		canvas.drawArc(boundingBox, ARC_START, ARC_ANGLE, false, pen);

		// draw centered circle
		pen.setStrokeWidth(KNOB_STROKE_WIDTH);
		canvas.drawCircle(cx, cy, r - KNOB_PADDING, pen);

		// draw value indicator
		pen.setStyle(Style.FILL);
		final double angle = ARC_START + value * ARC_ANGLE;
		canvas.drawCircle((int) (cx - (r - KNOB_PADDING - 2 * KNOB_RADIUS) * Math.sin(Math.toRadians(angle - 90))), (int) (cy + (r - KNOB_PADDING - 2 * KNOB_RADIUS) * Math.cos(Math.toRadians(angle - 90))), KNOB_RADIUS, pen);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			setValue((-120 + Math.toDegrees(Math.atan2(event.getY() - getHeight() / 2, event.getX() - getWidth() / 2)) + 360) % 360 / ARC_ANGLE);
		}
		return true;
	}
}
