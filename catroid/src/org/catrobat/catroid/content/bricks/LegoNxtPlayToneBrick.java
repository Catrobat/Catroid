/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.LegoNXT.LegoNXT;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class LegoNxtPlayToneBrick implements Brick, OnClickListener, OnSeekBarChangeListener {
	private static final long serialVersionUID = 1L;

	private static final int MIN_FREQ_IN_HERTZ = 200;
	private static final int MAX_FREQ_IN_HERTZ = 14000;
	private static final int MIN_DURATION = 0;
	private static final int MAX_DURATION = Integer.MAX_VALUE;

	public LegoNxtPlayToneBrick() {

	}

	private Sprite sprite;
	private int hertz;
	private int durationInMilliSeconds;

	private transient EditText editFreq;
	private transient SeekBar freqBar;
	private transient CheckBox checkbox;
	private transient View view;
	private transient boolean checked;

	public LegoNxtPlayToneBrick(Sprite sprite, int hertz, int duration) {
		this.sprite = sprite;
		this.hertz = hertz;
		this.durationInMilliSeconds = duration;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public void execute() {
		LegoNXT.sendBTCPlayToneMessage(hertz, durationInMilliSeconds);

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		SeekBar noClick = (SeekBar) view.findViewById(R.id.seekBarNXTToneFrequency);
		noClick.setEnabled(false);
		return view;
	}

	@Override
	public Brick clone() {
		return new LegoNxtPlayToneBrick(getSprite(), hertz, durationInMilliSeconds);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_nxt_play_tone, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_nxt_play_tone_checkbox);

			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
		}

		TextView textDuration = (TextView) view.findViewById(R.id.nxt_tone_duration_text_view);
		EditText editDuration = (EditText) view.findViewById(R.id.nxt_tone_duration_edit_text);
		editDuration.setText(String.valueOf(durationInMilliSeconds / 1000.0));
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		TextView textFreq = (TextView) view.findViewById(R.id.nxt_tone_freq_text_view);
		editFreq = (EditText) view.findViewById(R.id.nxt_tone_freq_edit_text);
		editFreq.setText(String.valueOf(hertz / 100));
		//		dialogFreq = new EditIntegerDialog(context, editFreq, frequency, true, MIN_FREQ, MAX_FREQ);
		//		dialogFreq.setOnDismissListener(this);
		//		dialogFreq.setOnCancelListener((OnCancelListener) context);
		//		editFreq.setOnClickListener(dialogFreq);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		freqBar = (SeekBar) view.findViewById(R.id.seekBarNXTToneFrequency);
		freqBar.setOnSeekBarChangeListener(this);
		freqBar.setMax(MAX_FREQ_IN_HERTZ / 100);
		freqBar.setEnabled(true);
		freqToSeekBarVal();

		Button freqDown = (Button) view.findViewById(R.id.freq_down_btn);
		freqDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (hertz <= 200) {
					return;
				}

				hertz -= 100;
				freqToSeekBarVal();
				editFreq.setText(String.valueOf(hertz / 100));
			}
		});

		Button freqUp = (Button) view.findViewById(R.id.freq_up_btn);
		freqUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (hertz >= 14000) {
					return;
				}

				hertz += 100;
				freqToSeekBarVal();
				editFreq.setText(String.valueOf(hertz / 100));
			}
		});
		return view;
	}

	@Override
	public void onProgressChanged(SeekBar freqBar, int progress, boolean fromUser) {
		if (!fromUser) { //Robotium fromUser=false
			if (progress == 0) {
				return;
			}
		}

		if (progress != (hertz / 100)) {
			seekbarValToFreq();
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar freqBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar freqBar) {

	}

	private void seekbarValToFreq() {
		hertz = freqBar.getProgress() * 100;

		if (hertz < 200) {
			hertz = 200;
			freqBar.setProgress(2);
		}

		editFreq.setText(String.valueOf(hertz / 100));
	}

	private void freqToSeekBarVal() {
		if (hertz < 200) {
			hertz = 200;
			freqBar.setProgress(2);
		}
		freqBar.setProgress(hertz / 100);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_nxt_play_tone_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public void onClick(final View view) {
		ScriptActivity activity = (ScriptActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				if (view.getId() == R.id.nxt_tone_duration_edit_text) {
					input.setText(String.valueOf(durationInMilliSeconds / 1000.0));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				} else if (view.getId() == R.id.nxt_tone_freq_edit_text) {
					input.setText(String.valueOf(hertz / 100));
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					if (view.getId() == R.id.nxt_tone_duration_edit_text) {

						int newDuration = (int) (Double.parseDouble(input.getText().toString()) * 1000);
						if (newDuration > MAX_DURATION) {
							newDuration = MAX_DURATION;
							Toast.makeText(getActivity(), R.string.number_to_big, Toast.LENGTH_SHORT).show();
						} else if (newDuration < MIN_DURATION) {
							newDuration = MIN_DURATION;
							Toast.makeText(getActivity(), R.string.number_to_small, Toast.LENGTH_SHORT).show();
						}
						durationInMilliSeconds = newDuration;
					} else if (view.getId() == R.id.nxt_tone_freq_edit_text) {
						int newFrequency = Integer.parseInt(input.getText().toString()) * 100;
						if (newFrequency > MAX_FREQ_IN_HERTZ) {
							newFrequency = MAX_FREQ_IN_HERTZ;
							Toast.makeText(getActivity(), R.string.number_to_big, Toast.LENGTH_SHORT).show();
						} else if (newFrequency < MIN_FREQ_IN_HERTZ) {
							newFrequency = MIN_FREQ_IN_HERTZ;
							Toast.makeText(getActivity(), R.string.number_to_small, Toast.LENGTH_SHORT).show();
						}
						hertz = newFrequency;
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_nxt_play_tone_brick");
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		checked = newValue;
	}
}
