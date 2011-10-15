/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;
import at.tugraz.ist.catroid.utils.Utils;

public class NXTPlayToneBrick implements Brick, OnDismissListener, OnClickListener, OnSeekBarChangeListener {
	private static final long serialVersionUID = 1L;
	public static final int REQUIRED_RESSOURCES = BLUETOOTH_LEGO_NXT;

	private Sprite sprite;
	private transient Handler btcHandler;
	private int frequency;
	private double duration;
	private static final int MIN_FREQ = 2;
	private static final int MAX_FREQ = 140;
	private static final double MIN_DURATION = 0;
	private static final double MAX_DURATION = Double.MAX_VALUE;

	private transient EditText editFreq;
	private transient SeekBar freqBar;
	private transient EditIntegerDialog dialogFreq;

	public NXTPlayToneBrick(Sprite sprite, int frequency, double duration) {
		this.sprite = sprite;
		this.frequency = frequency;
		this.duration = duration;
	}

	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		LegoNXT.sendBTCPlayToneMessage(frequency * 100, (int) (1000 * duration));

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.toolbox_brick_nxt_play_tone, null);
		SeekBar noClick = (SeekBar) view.findViewById(R.id.seekBarNXTToneFrequency);
		noClick.setEnabled(false);
		return view;
	}

	@Override
	public Brick clone() {
		return new NXTPlayToneBrick(getSprite(), frequency, duration);
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_nxt_play_tone, null);

		EditText editDuration = (EditText) brickView.findViewById(R.id.nxt_tone_duration_edit_text);
		editDuration.setText(String.valueOf(duration));
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);
		editDuration.setOnClickListener(this);

		editFreq = (EditText) brickView.findViewById(R.id.nxt_tone_freq_edit_text);
		editFreq.setText(String.valueOf(frequency));
		//		dialogFreq = new EditIntegerDialog(context, editFreq, frequency, true, MIN_FREQ, MAX_FREQ);
		//		dialogFreq.setOnDismissListener(this);
		//		dialogFreq.setOnCancelListener((OnCancelListener) context);
		//		editFreq.setOnClickListener(dialogFreq);
		editFreq.setOnClickListener(this);

		freqBar = (SeekBar) brickView.findViewById(R.id.seekBarNXTToneFrequency);
		freqBar.setOnSeekBarChangeListener(this);
		freqBar.setMax(MAX_FREQ);
		freqBar.setEnabled(true);
		freqToSeekBarVal();

		Button freqDown = (Button) brickView.findViewById(R.id.freq_down_btn);
		freqDown.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (frequency <= 2) {
					return;
				}

				frequency--;
				freqToSeekBarVal();
				editFreq.setText(String.valueOf(frequency));
			}
		});

		Button freqUp = (Button) brickView.findViewById(R.id.freq_up_btn);
		freqUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (frequency >= 140) {
					return;
				}

				frequency++;
				freqToSeekBarVal();
				editFreq.setText(String.valueOf(frequency));
			}
		});

		return brickView;
	}

	public void onProgressChanged(SeekBar freqBar, int progress, boolean fromUser) {
		if (progress != (frequency)) {
			seekbarValToFreq();
			if (dialogFreq != null) {
				dialogFreq.setValue(progress);
			}
		}

	}

	public void onStartTrackingTouch(SeekBar freqBar) {

	}

	public void onStopTrackingTouch(SeekBar freqBar) {

	}

	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof EditIntegerDialog) {
			EditIntegerDialog inputDialog = (EditIntegerDialog) dialog;
			if (inputDialog.getRefernecedEditTextId() == R.id.nxt_tone_freq_edit_text) {
				frequency = inputDialog.getValue();
				freqToSeekBarVal();
			}
		} else if (dialog instanceof EditDoubleDialog) {
			EditDoubleDialog inputDialog = (EditDoubleDialog) dialog;
			duration = inputDialog.getValue();
		} else {
			throw new RuntimeException("Received illegal id from EditText in NXTPlayToneBrick");
		}

		dialog.cancel();
	}

	private void seekbarValToFreq() {
		frequency = freqBar.getProgress();

		if (frequency < 2) {
			frequency = 2;
			freqBar.setProgress(2);
		}

		editFreq.setText(String.valueOf(frequency));
	}

	private void freqToSeekBarVal() {
		if (frequency < 2) {
			frequency = 2;
			freqBar.setProgress(2);
		}
		freqBar.setProgress(frequency);
	}

	public void onClick(final View view) {
		final Context context = view.getContext();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText input = new EditText(context);
		if (view.getId() == R.id.nxt_tone_duration_edit_text) {
			input.setText(String.valueOf(duration));
			input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		} else if (view.getId() == R.id.nxt_tone_freq_edit_text) {
			input.setText(String.valueOf(frequency));
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		input.setSelectAllOnFocus(true);
		dialog.setView(input);
		dialog.setOnCancelListener((OnCancelListener) context);
		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					if (view.getId() == R.id.nxt_tone_duration_edit_text) {

						double newDuration = Double.parseDouble(input.getText().toString());
						if (newDuration > MAX_DURATION) {
							newDuration = MAX_DURATION;
							Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
						} else if (newDuration < MIN_DURATION) {
							newDuration = MIN_DURATION;
							Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
						}
						duration = newDuration;
					} else if (view.getId() == R.id.nxt_tone_freq_edit_text) {
						int newFrequency = Integer.parseInt(input.getText().toString());
						if (newFrequency > MAX_FREQ) {
							newFrequency = MAX_FREQ;
							Toast.makeText(context, R.string.number_to_big, Toast.LENGTH_SHORT).show();
						} else if (newFrequency < MIN_FREQ) {
							newFrequency = MIN_FREQ;
							Toast.makeText(context, R.string.number_to_small, Toast.LENGTH_SHORT).show();
						}
						frequency = newFrequency;
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT);
				}
				dialog.cancel();
			}
		});
		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		AlertDialog finishedDialog = dialog.create();
		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));

		finishedDialog.show();
	}
}
