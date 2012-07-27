package com.h3nr1ke.livewallpaper.ourworld;

/**
 * Copyright CMW Mobile.com, 2010.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * The SeekBarDialogPreference class is a DialogPreference based and provides a
 * seekbar preference.
 * 
 * @author Casper Wakkers
 */
public class SeekBarDialogPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	// Layout widgets.
	private SeekBar seekBar = null;
	private TextView valueText = null;

	// Custom xml attributes.
	private int maximumValue = 0;
	private int minimumValue = 0;
	private int stepSize = 0;
	private String units = null;

	private Boolean isFloat = false;
//	private String floatSeparator = ".";
//	private int floatPrecision = 1;

	private int value = 0;

	/**
	 * The SeekBarDialogPreference constructor.
	 * 
	 * @param context
	 *            of this preference.
	 * @param attrs
	 *            custom xml attributes.
	 */
	public SeekBarDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.SeekBarDialogPreference);

		maximumValue = typedArray.getInteger(
				R.styleable.SeekBarDialogPreference_maximumValue, 0);
		minimumValue = typedArray.getInteger(
				R.styleable.SeekBarDialogPreference_minimumValue, 0);
		stepSize = typedArray.getInteger(
				R.styleable.SeekBarDialogPreference_stepSize, 1);
		units = typedArray.getString(R.styleable.SeekBarDialogPreference_units);
		isFloat = typedArray.getBoolean(
				R.styleable.SeekBarDialogPreference_isFloat, false);
		// floatSeparator = typedArray
		// .getString(R.styleable.SeekBarDialogPreference_units);
		// floatPrecision = typedArray.getInteger(
		// R.styleable.SeekBarDialogPreference_units, 1);

		typedArray.recycle();
	}

	/**
	 * {@inheritDoc}
	 */
	protected View onCreateDialogView() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());

		View view = layoutInflater.inflate(
				R.layout.seekbardialogpreference_layout, null);

		seekBar = (SeekBar) view.findViewById(R.id.seekbar);
		valueText = (TextView) view.findViewById(R.id.valueText);

		// Get the persistent value and correct it for the minimum value.
		value = getPersistedInt(minimumValue) - minimumValue;

		valueText.setText(String.valueOf(value+1) + " " + (units == null ? "" : units));
		
		// You're never know...
		if (value < 0) {
			value = 0;
		}

		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setKeyProgressIncrement(stepSize);
		seekBar.setMax(maximumValue - minimumValue);
		seekBar.setProgress(value);

		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	public void onProgressChanged(SeekBar seek, int newValue, boolean fromTouch) {
		String valueTextView = null;
		// Round the value to the closest integer value.
		if (stepSize >= 1) {
			value = Math.round(newValue / stepSize) * stepSize;
		} else {
			value = newValue;
		}

		// Set the valueText text.

		// check if the value is float or not
		if (isFloat) {
			// define the string format to output a valid number to the user
			valueTextView = String.format("%.1f%n", (float) value / 10);
		} else {
			valueTextView = String.valueOf(value + minimumValue)
					+ (units == null ? "" : units);
		}
		valueText.setText(valueTextView);
		callChangeListener(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onStartTrackingTouch(SeekBar seek) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void onStopTrackingTouch(SeekBar seek) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void onClick(DialogInterface dialog, int which) {
		// if the positive button is clicked, we persist the value.
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if (shouldPersist()) {
				persistInt(value + minimumValue);
			}
		}

		super.onClick(dialog, which);
	}
}
