/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.FormulaEditorActivity;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.REQUEST_CODE_EDIT_FORMULA;

public abstract class FormulaBrick extends BrickBaseType implements View.OnClickListener {

	@XStreamAlias("formulaList")
	ConcurrentFormulaHashMap formulaMap = new ConcurrentFormulaHashMap();

	public transient BiMap<FormulaField, Integer> brickFieldToTextViewIdMap = HashBiMap.create(2);

	public Formula getFormulaWithBrickField(FormulaField formulaField) throws IllegalArgumentException {
		if (formulaMap.containsKey(formulaField)) {
			return formulaMap.get(formulaField);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field: " + this.getClass().getSimpleName()
					+ " does not have BrickField." + formulaField);
		}
	}

	public void setFormulaWithBrickField(FormulaField formulaField, Formula formula) throws IllegalArgumentException {
		if (formulaMap.containsKey(formulaField)) {
			formulaMap.replace(formulaField, formula);
		} else {
			throw new IllegalArgumentException("Incompatible Brick Field: Cannot set BrickField."
					+ formulaField + " for " + this.getClass().getSimpleName());
		}
	}

	protected void addAllowedBrickField(FormulaField formulaField, int textViewResourceId) {
		formulaMap.putIfAbsent(formulaField, new Formula(0));
		brickFieldToTextViewIdMap.put(formulaField, textViewResourceId);
	}

	@Override
	@CallSuper
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		for (Formula formula : formulaMap.values()) {
			formula.addRequiredResources(requiredResourcesSet);
		}
	}

	public void replaceFormulaBrickField(FormulaField oldFormulaField, FormulaField newFormulaField) {
		if (formulaMap.containsKey(oldFormulaField)) {
			Formula brickFormula = formulaMap.get(oldFormulaField);
			formulaMap.remove(oldFormulaField);
			formulaMap.put(newFormulaField, brickFormula);
		}
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		FormulaBrick clone = (FormulaBrick) super.clone();
		clone.formulaMap = formulaMap.clone();
		return clone;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		for (BiMap.Entry<FormulaField, Integer> entry : brickFieldToTextViewIdMap.entrySet()) {
			TextView formulaFieldView = view.findViewById(entry.getValue());
			formulaFieldView.setText(getFormulaWithBrickField(entry.getKey()).clone().getTrimmedFormulaString(context));
		}
		return view;
	}

	public void setClickListeners() {
		for (BiMap.Entry<FormulaField, Integer> entry : brickFieldToTextViewIdMap.entrySet()) {
			TextView formulaFieldView = view.findViewById(entry.getValue());
			formulaFieldView.setOnClickListener(this);
		}
	}

	public List<Formula> getFormulas() {
		return new ArrayList<>(formulaMap.values());
	}

	public ConcurrentFormulaHashMap getFormulaMap() {
		return formulaMap;
	}

	public TextView getTextView(FormulaField formulaField) {
		return view.findViewById(brickFieldToTextViewIdMap.get(formulaField));
	}

	public void highlightTextView(FormulaField formulaField) {
		TextView formulaTextField = getTextView(formulaField);

		formulaTextField.getBackground().mutate()
				.setColorFilter(view.getContext().getResources()
						.getColor(R.color.brick_field_highlight), PorterDuff.Mode.SRC_ATOP);
	}

	@Override
	public void onClick(View view) {
		saveCodeFile(view);
		showFormulaEditorToEditFormula(view);
	}

	public void showFormulaEditorToEditFormula(View view) {
		if (view != null) {
			Brick.FormulaField formulaField =
					brickFieldToTextViewIdMap.inverse().containsKey(view.getId())
							? getBrickFieldFromTextViewId(view.getId())
							: getDefaultBrickField();

			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			if (activity instanceof FormulaEditorActivity) {
				Fragment fragment = activity
						.getSupportFragmentManager()
						.findFragmentById(R.id.fragment_container);

				if (fragment instanceof FormulaEditorFragment) {
					FormulaEditorFragment
							.handleNextEditedFormulaOnBrick(view.getContext(), formulaField);
				}
			} else {
				startFormulaEditorActivity(formulaField);
			}
		}
	}

	private void startFormulaEditorActivity(Brick.FormulaField formulaField) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		Intent intent = makeIntentForFormulaEditorActivity(view.getContext(), formulaField);
		activity.startActivityForResult(intent, REQUEST_CODE_EDIT_FORMULA);
	}

	private Intent makeIntentForFormulaEditorActivity(Context context, Brick.FormulaField formulaField) {
		Intent intent = new Intent(context, FormulaEditorActivity.class);

		intent.putExtra(FormulaEditorFragment.SHOW_CUSTOM_VIEW, false);

		//TODO revert(only for testing)
		//TODO Issue: ChangeSizeByNBrick can't be serialized
		intent.putExtra(FormulaEditorFragment.FORMULA_BRICK_BUNDLE_ARGUMENT, this);

		intent.putExtra(FormulaEditorFragment.FORMULA_FIELD_BUNDLE_ARGUMENT, formulaField);
		intent.putExtra(FormulaEditorFragment.BRICK_FIELD_TO_TEXT_VIEW_ID_MAP, new HashMap<>(brickFieldToTextViewIdMap));
		intent.putExtra(FormulaEditorFragment.FORMULA_MAP_BUNDLE_ARGUMENT, formulaMap);

		/*
		Get 'internFormula' from 'currentFormula' and pass it to the FormulaEditorActivity as
		data via the intent ('internFormula' is declared as transient in 'Formula' and therefore
		is not serializable). Do the same from 'FormulaEditorActivity' to 'FormulaEditorFragment'
		and set the value of 'internFormula' to the 'currentFormula' in 'onCreate'.
		In InternFormula,'internFormulaTokenSelection' needs to be serializable. Also mark
		'externInternRepresentationMapping' as transient.
		 */

		FormulaBrick formulaBrick = (FormulaBrick) intent
				.getSerializableExtra(FormulaEditorFragment.FORMULA_BRICK_BUNDLE_ARGUMENT);

		Brick.FormulaField currentFormulaField = (Brick.FormulaField) intent
				.getSerializableExtra(FormulaEditorFragment.FORMULA_FIELD_BUNDLE_ARGUMENT);

		Formula currentFormula = formulaBrick.getFormulaWithBrickField(currentFormulaField);
		InternFormula internFormula = currentFormula.internFormula;
		intent.putExtra(FormulaEditorFragment.CURRENT_BRICK_INTERN_FORMULA, internFormula);

		intent.putExtra(EXTRA_BRICK_HASH, hashCode());

		return intent;
	}

	public void updateEditedFormulas(HashMap editedFormulaMap) {
		for (Object key : editedFormulaMap.keySet()) {
			Formula updatedFormula = (Formula) editedFormulaMap.get(key);
			Formula oldFormula = formulaMap.get(key);
			oldFormula.setRoot(updatedFormula.getFormulaTree());
		}
	}

	public FormulaField getDefaultBrickField() {
		return formulaMap.keys().nextElement();
	}

	boolean isBrickFieldANumber(FormulaField formulaField) {
		return getFormulaWithBrickField(formulaField).isNumber();
	}

	public View getCustomView(Context context) {
		throw new IllegalStateException("There is no custom view for the " + getClass().getSimpleName() + ".");
	}

	public FormulaField getBrickFieldFromTextViewId(int textViewId) {
		return brickFieldToTextViewIdMap.inverse().get(textViewId);
	}

	protected void setSecondsLabel(View view, FormulaField formulaField) {
		TextView textView = view.findViewById(R.id.brick_seconds_label);
		Context context = textView.getContext();

		if (getFormulaWithBrickField(formulaField).isNumber()) {
			try {
				ProjectManager projectManager = ProjectManager.getInstance();
				Scope scope = new Scope(projectManager.getCurrentProject(),
						projectManager.getCurrentSprite(), null);
				Double formulaValue = formulaMap.get(formulaField).interpretDouble(scope);
				textView.setText(context.getResources().getQuantityString(R.plurals.second_plural,
						Utils.convertDoubleToPluralInteger(formulaValue)));
				return;
			} catch (InterpretationException e) {
				Log.e(getClass().getSimpleName(),
						"Interpretation of formula failed, "
								+ "fallback to quantity \"other\" for \"second(s)\" label.", e);
			}
		}
		textView.setText(context.getResources()
				.getQuantityString(R.plurals.second_plural, Utils.TRANSLATION_PLURAL_OTHER_INTEGER));
	}

	public void updateUserDataReference(String oldName, String newName, UserData<?> item,
			boolean renameAll) {
		for (Formula formula : getFormulas()) {
			if (renameAll) {
				formula.updateVariableName(oldName, newName);
				formula.updateUserlistName(oldName, newName);
			} else if (item instanceof UserVariable) {
				formula.updateVariableName(oldName, newName);
			} else {
				formula.updateUserlistName(oldName, newName);
			}
		}
	}

	private void saveCodeFile(View view) {
		ScriptFragment scriptFragment = getScriptFragment(view);
		if (scriptFragment != null && scriptFragment.copyProjectForUndoOption()) {
			((SpriteActivity) scriptFragment.getActivity()).setUndoMenuItemVisibility(true);
			scriptFragment.setUndoBrickPosition(this);
		}
	}

	private ScriptFragment getScriptFragment(View view) {
		FragmentActivity activity = null;
		if (view != null) {
			activity = UiUtils.getActivityFromView(view);
		}

		if (activity == null) {
			return null;
		}

		Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		if (currentFragment instanceof ScriptFragment) {
			return (ScriptFragment) currentFragment;
		}

		return null;
	}

	public boolean hasEditableFormulaField() {
		return !brickFieldToTextViewIdMap.isEmpty();
	}
}
