/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;

public class ColorSeekbar {

	private final FormulaBrick formulaBrick;

	private final Brick.BrickField redField;
	private final Brick.BrickField greenField;
	private final Brick.BrickField blueField;

	private View colorPreviewView;

	private TextView formulaEditorEditTextRed;
	private TextView formulaEditorEditTextBlue;
	private TextView formulaEditorEditTextGreen;

	private SeekBar redSeekBar;
	private SeekBar greenSeekBar;
	private SeekBar blueSeekBar;
	private View seekbarView;

	public ColorSeekbar(FormulaBrick formulaBrick, Brick.BrickField redField,
			Brick.BrickField greenField, Brick.BrickField blueField) {
		this.formulaBrick = formulaBrick;
		this.redField = redField;
		this.greenField = greenField;
		this.blueField = blueField;
	}

	public View getView(Context context) {
		seekbarView = View.inflate(context, R.layout.fragment_rgb_color_chooser, null);

		seekbarView.setFocusableInTouchMode(true);
		seekbarView.requestFocus();

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.rgb_red_value:
						FormulaEditorFragment.showFragment(view, formulaBrick, redField);
						break;
					case R.id.rgb_green_value:
						FormulaEditorFragment.showFragment(view, formulaBrick, greenField);
						break;
					case R.id.rgb_blue_value:
						FormulaEditorFragment.showFragment(view, formulaBrick, blueField);
						break;
				}
			}
		};

		formulaEditorEditTextRed = (TextView) seekbarView.findViewById(R.id.rgb_red_value);
		formulaEditorEditTextRed.setOnClickListener(onClickListener);
		formulaBrick.getFormulaWithBrickField(redField).setTextFieldId(R.id.rgb_red_value);
		formulaBrick.getFormulaWithBrickField(redField).refreshTextField(seekbarView);

		formulaEditorEditTextGreen = (TextView) seekbarView.findViewById(R.id.rgb_green_value);
		formulaEditorEditTextGreen.setOnClickListener(onClickListener);
		formulaBrick.getFormulaWithBrickField(greenField).setTextFieldId(R.id.rgb_green_value);
		formulaBrick.getFormulaWithBrickField(greenField).refreshTextField(seekbarView);

		formulaEditorEditTextBlue = (TextView) seekbarView.findViewById(R.id.rgb_blue_value);
		formulaEditorEditTextBlue.setOnClickListener(onClickListener);
		formulaBrick.getFormulaWithBrickField(blueField).setTextFieldId(R.id.rgb_blue_value);
		formulaBrick.getFormulaWithBrickField(blueField).refreshTextField(seekbarView);

		redSeekBar = (SeekBar) seekbarView.findViewById(R.id.color_rgb_seekbar_red);
		greenSeekBar = (SeekBar) seekbarView.findViewById(R.id.color_rgb_seekbar_green);
		blueSeekBar = (SeekBar) seekbarView.findViewById(R.id.color_rgb_seekbar_blue);

		colorPreviewView = seekbarView.findViewById(R.id.color_rgb_preview);

		int color = Color.rgb(getCurrentBrickFieldValue(redField), getCurrentBrickFieldValue(greenField), getCurrentBrickFieldValue(blueField));
		redSeekBar.setProgress(Color.red(color));
		greenSeekBar.setProgress(Color.green(color));
		blueSeekBar.setProgress(Color.blue(color));

		colorPreviewView.setBackgroundColor(color);
		colorPreviewView.invalidate();

//		redSeekBar.setProgress(getCurrentBrickFieldValue(redField));
//		greenSeekBar.setProgress(getCurrentBrickFieldValue(greenField));
//		blueSeekBar.setProgress(getCurrentBrickFieldValue(blueField));

		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				switch (seekBar.getId()) {
					case R.id.color_rgb_seekbar_red:
						formulaEditorEditTextRed.setText(String.valueOf(seekBar.getProgress()));
						break;
					case R.id.color_rgb_seekbar_green:
						formulaEditorEditTextGreen.setText(String.valueOf(seekBar.getProgress()));
						break;
					case R.id.color_rgb_seekbar_blue:
						formulaEditorEditTextBlue.setText(String.valueOf(seekBar.getProgress()));
						break;
					default:
						break;
				}
				int color = Color.argb(0xFF, redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());
				colorPreviewView.setBackgroundColor(color);
				colorPreviewView.invalidate();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				Brick.BrickField changedBrickField = null;

				switch (seekBar.getId()) {
					case R.id.color_rgb_seekbar_red:
						FormulaEditorFragment.changeInputField(seekbarView, redField);
						changedBrickField = redField;
						break;
					case R.id.color_rgb_seekbar_green:
						FormulaEditorFragment.changeInputField(seekbarView, greenField);
						changedBrickField = greenField;
						break;
					case R.id.color_rgb_seekbar_blue:
						FormulaEditorFragment.changeInputField(seekbarView, blueField);
						changedBrickField = blueField;
						break;
					default:
						break;
				}
				FormulaEditorFragment.overwriteFormula(seekbarView, new Formula(seekBar.getProgress()));
				// ToDo: this is a hack for saving the value immediately
				FormulaEditorFragment.changeInputField(seekbarView, getOtherField(changedBrickField));
				FormulaEditorFragment.changeInputField(seekbarView, changedBrickField);
				// end of hack
			}
		};

		redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		return seekbarView;
	}

	private Brick.BrickField getOtherField(Brick.BrickField brickField) {
		if (brickField == blueField) {
			return redField;
		}
		return blueField;
	}

	private int getCurrentBrickFieldValue(Brick.BrickField brickField) {
		String stringValue = formulaBrick.getFormulaWithBrickField(brickField)
				.getDisplayString(seekbarView.getContext());
		int value = Double.valueOf(stringValue.replace(",", ".")).intValue();
		return value;
	}
}
