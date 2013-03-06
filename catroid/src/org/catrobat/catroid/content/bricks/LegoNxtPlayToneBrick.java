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
<<<<<<< HEAD
import org.catrobat.catroid.LegoNXT.LegoNXT;
=======
>>>>>>> refs/remotes/origin/master
import org.catrobat.catroid.content.Sprite;
<<<<<<< HEAD
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
=======
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;
>>>>>>> refs/remotes/origin/master

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
<<<<<<< HEAD
=======
import android.widget.Toast;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
>>>>>>> refs/remotes/origin/master

public class LegoNxtPlayToneBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;


	private Sprite sprite;

	private transient EditText editFreq;

	private Formula frequency;
	private Formula durationInSeconds;

	public LegoNxtPlayToneBrick(Sprite sprite, int frequencyValue, int durationValue) {
		this.sprite = sprite;

		this.frequency = new Formula(frequencyValue);
		this.durationInSeconds = new Formula(durationValue);
	}

	public LegoNxtPlayToneBrick(Sprite sprite, Formula frequencyFormula, Formula durationFormula) {
		this.sprite = sprite;

		this.frequency = frequencyFormula;
		this.durationInSeconds = durationFormula;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
<<<<<<< HEAD
	}

	@Override
	public void execute() {
		int frequencyValue = frequency.interpretInteger(MIN_FREQ_IN_HERTZ, MAX_FREQ_IN_HERTZ, sprite);
		int durationInMillisecondsValue = (int) durationInSeconds.interpretFloat(MIN_DURATION, MAX_DURATION, sprite) * 1000;

		LegoNXT.sendBTCPlayToneMessage(frequencyValue, durationInMillisecondsValue);

=======
>>>>>>> refs/remotes/origin/master
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_nxt_play_tone, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new LegoNxtPlayToneBrick(getSprite(), frequency, durationInSeconds);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View brickView = View.inflate(context, R.layout.brick_nxt_play_tone, null);

		TextView textDuration = (TextView) brickView.findViewById(R.id.nxt_tone_duration_text_view);
		EditText editDuration = (EditText) brickView.findViewById(R.id.nxt_tone_duration_edit_text);
		//		editDuration.setText(String.valueOf(durationInMs / 1000.0));
		durationInSeconds.setTextFieldId(R.id.nxt_tone_duration_edit_text);
		durationInSeconds.refreshTextField(brickView);
		//		EditDoubleDialog dialogDuration = new EditDoubleDialog(context, editDuration, duration, MIN_DURATION,
		//				MAX_DURATION);
		//		dialogDuration.setOnDismissListener(this);
		//		dialogDuration.setOnCancelListener((OnCancelListener) context);
		//		editDuration.setOnClickListener(dialogDuration);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		TextView textFreq = (TextView) brickView.findViewById(R.id.nxt_tone_freq_text_view);
		editFreq = (EditText) brickView.findViewById(R.id.nxt_tone_freq_edit_text);
		//		editFreq.setText(String.valueOf(hertz / 100));
		frequency.setTextFieldId(R.id.nxt_tone_freq_edit_text);
		frequency.refreshTextField(brickView);

		textFreq.setVisibility(View.GONE);
		editFreq.setVisibility(View.VISIBLE);

		editFreq.setOnClickListener(this);

		return brickView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.nxt_tone_freq_edit_text:
				FormulaEditorFragment.showFragment(view, this, frequency);
				break;
			case R.id.nxt_tone_duration_edit_text:
				FormulaEditorFragment.showFragment(view, this, durationInSeconds);
				break;
		}

	}

<<<<<<< HEAD
=======
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
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtPlayTone(sprite,frequency, durationInSeconds));
		return null;
	}
}
