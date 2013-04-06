package com.ultimateremotecontrol.urcandroid.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Vertical SeekBar
 * Code from: http://kersevanivan.org/?p=123
 */
public class VerticalSeekBar extends SeekBar {
	private OnSeekBarChangeListener _seekbarListener;

	public VerticalSeekBar(Context context) {
		super(context);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public VerticalSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(h, w, oldh, oldw);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
		this._seekbarListener = mListener;
	}

	protected void onDraw(Canvas c) {

		c.rotate(-90);
		c.translate(-getHeight(), 0);

		super.onDraw(c);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!isEnabled() || _seekbarListener == null) {
			return false;
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			if (_seekbarListener != null)
				_seekbarListener.onStartTrackingTouch(this);
			break;

		case MotionEvent.ACTION_MOVE:
			int position = getMax()
					- (int) (getMax() * event.getY() / getHeight());

			if (position < 0)
				position = 0;
			if (position > getMax())
				position = getMax();

			setProgress(position);
			onSizeChanged(getWidth(), getHeight(), 0, 0);
			if (_seekbarListener != null)
				_seekbarListener.onProgressChanged(this, position, true);
			break;

		case MotionEvent.ACTION_UP:
			if (_seekbarListener != null)
				_seekbarListener.onStopTrackingTouch(this);
			break;

		case MotionEvent.ACTION_CANCEL:
			break;

		}

		return true;

	}

}