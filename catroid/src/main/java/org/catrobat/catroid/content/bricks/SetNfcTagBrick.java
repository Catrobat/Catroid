/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.database.DataSetObserver;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class SetNfcTagBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient Spinner spinner;
	private int nfcTagNdefDefaultType = BrickValues.TNF_WELL_KNOWN_HTTPS;
	private int nfcTagNdefType = nfcTagNdefDefaultType;

	public SetNfcTagBrick(String messageString) {
		this(new Formula(messageString));
	}

	public SetNfcTagBrick(Formula message) {
		addAllowedBrickField(BrickField.NFC_NDEF_MESSAGE, R.id.brick_set_nfc_tag_edit_text);
		setFormulaWithBrickField(BrickField.NFC_NDEF_MESSAGE, message);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_nfc_tag;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(NFC_ADAPTER);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);
		spinner = view.findViewById(R.id.brick_set_nfc_tag_ndef_record_spinner);

		final ArrayAdapter<String> spinnerAdapter = createArrayAdapter(context);
		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);
		spinner.setAdapter(spinnerAdapterWrapper);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();
				if (itemSelected.equals(context.getString(R.string.tnf_mime_media))) {
					nfcTagNdefType = BrickValues.TNF_MIME_MEDIA;
				} else if (itemSelected.equals(context.getString(R.string.tnf_well_known_http))) {
					nfcTagNdefType = BrickValues.TNF_WELL_KNOWN_HTTP;
				} else if (itemSelected.equals(context.getString(R.string.tnf_well_known_https))) {
					nfcTagNdefType = BrickValues.TNF_WELL_KNOWN_HTTPS;
				} else if (itemSelected.equals(context.getString(R.string.tnf_well_known_sms))) {
					nfcTagNdefType = BrickValues.TNF_WELL_KNOWN_SMS;
				} else if (itemSelected.equals(context.getString(R.string.tnf_well_known_tel))) {
					nfcTagNdefType = BrickValues.TNF_WELL_KNOWN_TEL;
				} else if (itemSelected.equals(context.getString(R.string.tnf_well_known_mailto))) {
					nfcTagNdefType = BrickValues.TNF_WELL_KNOWN_MAILTO;
				} else if (itemSelected.equals(context.getString(R.string.tnf_external_type))) {
					nfcTagNdefType = BrickValues.TNF_EXTERNAL_TYPE;
				} else {
					nfcTagNdefType = BrickValues.TNF_EMPTY;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		spinner.setSelection(nfcTagNdefType, true);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		spinner = prototypeView.findViewById(R.id.brick_set_nfc_tag_ndef_record_spinner);
		SpinnerAdapter setLookSpinnerAdapter = createArrayAdapter(context);
		spinner.setAdapter(setLookSpinnerAdapter);
		spinner.setSelection(nfcTagNdefType, true);

		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createSetNfcTagAction(sprite, getFormulaWithBrickField(BrickField.NFC_NDEF_MESSAGE), nfcTagNdefType));
		return null;
	}

	private ArrayAdapter<String> createArrayAdapter(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter.addAll(
				context.getString(R.string.tnf_mime_media),
				context.getString(R.string.tnf_well_known_http),
				context.getString(R.string.tnf_well_known_https),
				context.getString(R.string.tnf_well_known_sms),
				context.getString(R.string.tnf_well_known_tel),
				context.getString(R.string.tnf_well_known_mailto),
				context.getString(R.string.tnf_external_type),
				context.getString(R.string.tnf_empty));

		return arrayAdapter;
	}

	private static class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected ArrayAdapter<String> spinnerAdapter;

		SpinnerAdapterWrapper(Context context, ArrayAdapter<String> spinnerAdapter) {
			this.context = context;
			this.spinnerAdapter = spinnerAdapter;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver paramDataSetObserver) {
			spinnerAdapter.registerDataSetObserver(paramDataSetObserver);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver) {
			spinnerAdapter.unregisterDataSetObserver(paramDataSetObserver);
		}

		@Override
		public int getCount() {
			return spinnerAdapter.getCount();
		}

		@Override
		public Object getItem(int paramInt) {
			return spinnerAdapter.getItem(paramInt);
		}

		@Override
		public long getItemId(int paramInt) {
			return spinnerAdapter.getItemId(paramInt);
		}

		@Override
		public boolean hasStableIds() {
			return spinnerAdapter.hasStableIds();
		}

		@Override
		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			return spinnerAdapter.getView(paramInt, paramView, paramViewGroup);
		}

		@Override
		public int getItemViewType(int paramInt) {
			return spinnerAdapter.getItemViewType(paramInt);
		}

		@Override
		public int getViewTypeCount() {
			return spinnerAdapter.getViewTypeCount();
		}

		@Override
		public boolean isEmpty() {
			return spinnerAdapter.isEmpty();
		}

		@Override
		public View getDropDownView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			View dropDownView = spinnerAdapter.getDropDownView(paramInt, paramView, paramViewGroup);

			dropDownView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
					return false;
				}
			});
			return dropDownView;
		}
	}
}
