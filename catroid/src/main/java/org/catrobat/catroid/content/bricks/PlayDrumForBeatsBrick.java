/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.PickableDrum;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@CatrobatLanguageBrick(command = "Play")
public class PlayDrumForBeatsBrick extends FormulaBrick
		implements BrickSpinner.OnItemSelectedListener<PickableDrum>, UpdateableSpinnerBrick {

	private static final String DRUM_CATLANG_PARAMETER_NAME = "drum";

	private PickableDrum drumSelection = PickableDrum.values()[0];
	private transient BrickSpinner<PickableDrum> spinner;

	@Override
	public int getViewResource() {
		return R.layout.brick_play_drum_for_beats;
	}

	protected int getSpinnerId() {
		return R.id.play_drum_for_beats_spinner;
	}

	public PlayDrumForBeatsBrick() {
		addAllowedBrickField(BrickField.PLAY_DRUM, R.id.brick_play_drum_for_beats_edit_text, "number of beats");
	}

	public PlayDrumForBeatsBrick(int value) {
		this(new Formula(value));
	}

	private PlayDrumForBeatsBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.PLAY_DRUM, formula);
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		List<Nameable> items = new ArrayList<>();

		for (PickableDrum drum : PickableDrum.values()) {
			drum.setName(drum.getString(context));
			items.add(drum);
		}

		spinner = new BrickSpinner<>(R.id.play_drum_for_beats_spinner, view, items);
		spinner.setSelection(PickableDrum.getIndexByValue(drumSelection.getValue()));
		spinner.setOnItemSelectedListener(this);

		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPlayDrumForBeatsAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.PLAY_DRUM), drumSelection));
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable PickableDrum item) {
		if (item != null) {
			drumSelection = item;
		}
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (spinner != null) {
			spinner.setSelection(itemName);
		}
	}

	@Override
	protected List<Map.Entry<String, String>> getArgumentList() {
		ArrayList<Map.Entry<String, String>> arguments = new ArrayList<>();
		arguments.add(this.getArgumentByCatlangName(DRUM_CATLANG_PARAMETER_NAME));
		arguments.addAll(super.getArgumentList());
		return arguments;
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(DRUM_CATLANG_PARAMETER_NAME)) {
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, PickableDrum.getCatrobatLanguageStringByDrum(drumSelection));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(DRUM_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);

		String drum = arguments.get("drum");
		if (drum != null) {
			drumSelection = PickableDrum.getDrumByCatrobatLanguageString(drum);
		}
	}
}
