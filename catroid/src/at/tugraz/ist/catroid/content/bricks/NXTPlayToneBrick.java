/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditDoubleDialog;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

public class NXTPlayToneBrick implements Brick, OnDismissListener, OnSeekBarChangeListener {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient Handler btcHandler;
	private int frequency;
	private double duration;
	private static final int MIN_FREQ = 0;
	private static final int MAX_FREQ = 14000;
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

	public void execute() {
		if (btcHandler == null) {
			btcHandler = LegoNXT.getBTCHandler();
		}

		LegoNXT.sendBTCPlayToneMessage(frequency + 200, (int) (1000 * duration));

	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.toolbox_brick_nxt_play_tone, null);
		freqBar = (SeekBar) brickView.findViewById(R.id.seekBarNXTToneFrequency);
		freqBar.setEnabled(false);
		return brickView;
	}

	@Override
	public Brick clone() {
		return new NXTPlayToneBrick(getSprite(), frequency, duration);
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_nxt_play_tone, null);

		EditText editDuration = (EditText) brickView.findViewById(R.id.nxt_tone_duration_edit_text);
		editDuration.setText(String.valueOf(duration));
		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
				MAX_DURATION);
		dialogDuration.setOnDismissListener(this);
		dialogDuration.setOnCancelListener((OnCancelListener) context);
		editDuration.setOnClickListener(dialogDuration);

		editFreq = (EditText) brickView.findViewById(R.id.nxt_tone_freq_edit_text);
		editFreq.setText(String.valueOf(frequency));
		dialogFreq = new EditIntegerDialog(context, editFreq, frequency, true, MIN_FREQ, MAX_FREQ);
		dialogFreq.setOnDismissListener(this);
		dialogFreq.setOnCancelListener((OnCancelListener) context);
		editFreq.setOnClickListener(dialogFreq);

		freqBar = (SeekBar) brickView.findViewById(R.id.seekBarNXTToneFrequency);
		freqBar.setOnSeekBarChangeListener(this);
		freqBar.setMax(MAX_FREQ - 200);
		freqBar.setEnabled(true);
		freqToSeekBarVal();
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
		editFreq.setText(String.valueOf(frequency));
	}

	private void freqToSeekBarVal() {
		freqBar.setProgress(frequency);
	}

}