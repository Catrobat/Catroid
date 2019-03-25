/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

	private TextView redValueTextView;
	private TextView blueValueTextView;
	private TextView greenValueTextView;

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

	public View getView(final Context context) {
		seekbarView = View.inflate(context, R.layout.rgb_seek_bar_view, null);

		seekbarView.setFocusableInTouchMode(true);
		seekbarView.requestFocus();

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.rgb_red_value:
						FormulaEditorFragment.showFragment(context, formulaBrick, redField);
						break;
					case R.id.rgb_green_value:
						FormulaEditorFragment.showFragment(context, formulaBrick, greenField);
						break;
					case R.id.rgb_blue_value:
						FormulaEditorFragment.showFragment(context, formulaBrick, blueField);
						break;
				}
			}
		};

		redValueTextView = seekbarView.findViewById(R.id.rgb_red_value);
		redValueTextView.setOnClickListener(onClickListener);

		greenValueTextView = seekbarView.findViewById(R.id.rgb_green_value);
		greenValueTextView.setOnClickListener(onClickListener);

		blueValueTextView = seekbarView.findViewById(R.id.rgb_blue_value);
		blueValueTextView.setOnClickListener(onClickListener);

		redSeekBar = seekbarView.findViewById(R.id.color_rgb_seekbar_red);
		greenSeekBar = seekbarView.findViewById(R.id.color_rgb_seekbar_green);
		blueSeekBar = seekbarView.findViewById(R.id.color_rgb_seekbar_blue);

		int color = Color.rgb(getCurrentBrickFieldValue(context, redField),
				getCurrentBrickFieldValue(context, greenField),
				getCurrentBrickFieldValue(context, blueField));

		redSeekBar.setProgress(Color.red(color));
		greenSeekBar.setProgress(Color.green(color));
		blueSeekBar.setProgress(Color.blue(color));

		redValueTextView.setText(String.valueOf(redSeekBar.getProgress()));
		greenValueTextView.setText(String.valueOf(greenSeekBar.getProgress()));
		blueValueTextView.setText(String.valueOf(blueSeekBar.getProgress()));

		colorPreviewView = seekbarView.findViewById(R.id.color_rgb_preview);
		colorPreviewView.setBackgroundColor(color);
		colorPreviewView.invalidate();

		SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				switch (seekBar.getId()) {
					case R.id.color_rgb_seekbar_red:
						redValueTextView.setText(String.valueOf(seekBar.getProgress()));
						break;
					case R.id.color_rgb_seekbar_green:
						greenValueTextView.setText(String.valueOf(seekBar.getProgress()));
						break;
					case R.id.color_rgb_seekbar_blue:
						blueValueTextView.setText(String.valueOf(seekBar.getProgress()));
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
						changedBrickField = redField;
						break;
					case R.id.color_rgb_seekbar_green:
						changedBrickField = greenField;
						break;
					case R.id.color_rgb_seekbar_blue:
						changedBrickField = blueField;
						break;
					default:
						break;
				}
				formulaBrick.setFormulaWithBrickField(changedBrickField, new Formula(seekBar.getProgress()));
			}
		};

		redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

		return seekbarView;
	}

	private int getCurrentBrickFieldValue(Context context, Brick.BrickField brickField) {
		String currentStringValue = formulaBrick.getFormulaWithBrickField(brickField).getTrimmedFormulaString(context);
		return Double.valueOf(currentStringValue.replace(",", ".")).intValue();
	}
}
