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
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.util.List;

public class WhenNfcBrick extends BrickBaseType implements ScriptBrick {

	private static final long serialVersionUID = 1L;
	protected WhenNfcScript whenNfcScript;
	private transient NfcTagData nfcTag;
	private transient NfcTagData oldSelectedNfcTag;

	public WhenNfcBrick() {
		this.oldSelectedNfcTag = null;
		this.nfcTag = null;
		this.whenNfcScript = new WhenNfcScript();
		this.whenNfcScript.setMatchAll(true);
	}

	public WhenNfcBrick(WhenNfcScript script) {
		this.oldSelectedNfcTag = null;
		this.nfcTag = script.getNfcTag();
		this.whenNfcScript = script;

		if (script.isCommentedOut()) {
			setCommentedOut(true);
		}
	}

	@Override
	public Script getScriptSafe() {
		if (whenNfcScript == null) {
			setWhenNfcScript(new WhenNfcScript(nfcTag));
		}
		return whenNfcScript;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		WhenNfcBrick clone = (WhenNfcBrick) super.clone();
		clone.whenNfcScript = new WhenNfcScript(nfcTag);
		return clone;
	}

	@Override
	protected int getLayoutRes() {
		return R.layout.brick_when_nfc;
	}

	@Override
	public View getView(final Context context, BrickAdapter brickAdapter) {

		if (whenNfcScript == null) {
			whenNfcScript = new WhenNfcScript(nfcTag);
		}

		super.getView(context, brickAdapter);

		final Spinner nfcSpinner = (Spinner) view.findViewById(R.id.brick_when_nfc_spinner);
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
					oldSelectedNfcTag = nfcTag;
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
					oldSelectedNfcTag = nfcTag;
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
			Log.d("setSpinnerSelection", "nfcTag found: " + nfcTag.getNfcTagName());
			oldSelectedNfcTag = nfcTag;
			spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getNfcTagList().indexOf(nfcTag) + 2, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				if (ProjectManager.getInstance().getCurrentSprite().getNfcTagList().indexOf(oldSelectedNfcTag) >= 0) {
					spinner.setSelection(ProjectManager.getInstance().getCurrentSprite().getNfcTagList().indexOf(oldSelectedNfcTag) + 2, true);
					Log.d("setSpinnerSelection", "oldSelectedNfcTag found");
				} else {
					spinner.setSelection(1, true);
					Log.d("setSpinnerSelection", "setSelection(1, true)");
				}
			} else {
				spinner.setSelection(0, true);
				Log.d("setSpinnerSelection", "setSelection(0, true)");
			}
		}
	}

	private ArrayAdapter<NfcTagData> createNfcTagAdapter(Context context) {
		ArrayAdapter<NfcTagData> arrayAdapter = new ArrayAdapter<NfcTagData>(context, android.R.layout.simple_spinner_item);
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
		Spinner nfcSpinner = (Spinner) prototypeView.findViewById(R.id.brick_when_nfc_spinner);

		SpinnerAdapter nfcSpinnerAdapter = createNfcTagAdapter(context); //NfcTagContainer.getMessageAdapter(context);
		nfcSpinner.setAdapter(nfcSpinnerAdapter);
		setSpinnerSelection(nfcSpinner);
		return prototypeView;
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

	public void setWhenNfcScript(WhenNfcScript whenNfcScript) {
		this.whenNfcScript = whenNfcScript;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScriptSafe().setCommentedOut(commentedOut);
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
			NfcTagData currentNfcTag = spinnerAdapter.getItem(paramInt);
			if (!currentNfcTag.getNfcTagName().equals(context.getString(R.string.new_broadcast_message))) {
				oldSelectedNfcTag = currentNfcTag;
			}
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
				//TODO: Switch to NFC Tag Fragment
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
}
