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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.List;

import javax.annotation.Nonnull;

public class WhenNfcBrick extends BrickBaseType implements ScriptBrick {

	private static final long serialVersionUID = 1L;

	private WhenNfcScript whenNfcScript;

	private transient NfcTagData nfcTag;

	public WhenNfcBrick() {
		this(new WhenNfcScript());
	}

	public WhenNfcBrick(@Nonnull WhenNfcScript whenNfcScript) {
		nfcTag = whenNfcScript.getNfcTag();
		whenNfcScript.setScriptBrick(this);
		commentedOut = whenNfcScript.isCommentedOut();
		this.whenNfcScript = whenNfcScript;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		WhenNfcBrick clone = (WhenNfcBrick) super.clone();
		clone.whenNfcScript = (WhenNfcScript) whenNfcScript.clone();
		clone.whenNfcScript.setScriptBrick(clone);
		return clone;
	}

	@Override
	public Script getScript() {
		return whenNfcScript;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_nfc;
	}

	@Override
	public View getView(final Context context) {
		if (whenNfcScript == null) {
			whenNfcScript = new WhenNfcScript(nfcTag);
		}
		super.getView(context);
		final Spinner nfcSpinner = view.findViewById(R.id.brick_when_nfc_spinner);

		final ArrayAdapter<NfcTagData> spinnerAdapter = createNfcTagAdapter(context);

		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);

		nfcSpinner.setAdapter(spinnerAdapterWrapper);
		nfcSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedTag = nfcSpinner.getSelectedItem().toString();
				Log.d("WhenNfcBrick", "onItemSelected(): " + selectedTag);

				if (position == 0) {
					nfcTag = null;
				} else if (selectedTag.equals(context.getString(R.string.brick_when_nfc_default_all))) {
					whenNfcScript.setMatchAll(true);
					whenNfcScript.setNfcTag(null);
					//TODO: rework all
					nfcTag = null; //(NfcTagData)parent.getItemAtPosition(position);
				} else {
					if (whenNfcScript.getNfcTag() == null) {
						whenNfcScript.setNfcTag(new NfcTagData());
					}
					for (NfcTagData selTag : ProjectManager.getInstance().getCurrentSprite().getNfcTagList()) {
						if (selTag.getNfcTagName().equals(selectedTag)) {
							whenNfcScript.setNfcTag(selTag);
							nfcTag = (NfcTagData) parent.getItemAtPosition(position); //selTag
							break;
						}
					}
					whenNfcScript.setMatchAll(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		setSpinnerSelection(nfcSpinner);

		return view;
	}

	private void setSpinnerSelection(Spinner spinner) {
		if (ProjectManager.getInstance().getCurrentSprite().getNfcTagList().contains(nfcTag)) {
			spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getNfcTagList().indexOf(nfcTag) + 2, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (ProjectManager.getInstance().getCurrentSprite().getNfcTagList().indexOf(nfcTag) >= 0) {
					spinner.setSelection(ProjectManager.getInstance()
							.getCurrentSprite().getNfcTagList().indexOf(nfcTag) + 2, true);
				} else {
					spinner.setSelection(1, true);
				}
			} else {
				spinner.setSelection(0, true);
			}
		}
	}

	private ArrayAdapter<NfcTagData> createNfcTagAdapter(Context context) {
		ArrayAdapter<NfcTagData> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		NfcTagData dummyNfcTagData = new NfcTagData();
		dummyNfcTagData.setNfcTagName(context.getString(R.string.new_broadcast_message));
		arrayAdapter.add(dummyNfcTagData);
		dummyNfcTagData = new NfcTagData();
		dummyNfcTagData.setNfcTagName(context.getString(R.string.brick_when_nfc_default_all));
		arrayAdapter.add(dummyNfcTagData);
		for (NfcTagData nfcTagData : ProjectManager.getInstance().getCurrentSprite().getNfcTagList()) {
			arrayAdapter.add(nfcTagData);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		Spinner nfcSpinner = prototypeView.findViewById(R.id.brick_when_nfc_spinner);

		SpinnerAdapter nfcSpinnerAdapter = createNfcTagAdapter(context); //NfcTagContainer.getMessageAdapter(context);
		nfcSpinner.setAdapter(nfcSpinnerAdapter);
		setSpinnerSelection(nfcSpinner);
		return prototypeView;
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected ArrayAdapter<NfcTagData> spinnerAdapter;

		private boolean isTouchInDropDownView;

		SpinnerAdapterWrapper(Context context, ArrayAdapter<NfcTagData> spinnerAdapter) {
			this.context = context;
			this.spinnerAdapter = spinnerAdapter;
			this.isTouchInDropDownView = false;
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
			if (isTouchInDropDownView) {
				isTouchInDropDownView = false;
			}
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
					isTouchInDropDownView = true;
					return false;
				}
			});

			return dropDownView;
		}
	}

	@Override
	public int getRequiredResources() {
		return NFC_ADAPTER;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	public NfcTagData getNfcTag() {
		return nfcTag;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}
}
