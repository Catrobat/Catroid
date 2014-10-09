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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNfcScript;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;

public class WhenNfcBrick extends ScriptBrick implements NfcTagFragment.OnNfcTagDataListChangedAfterNewListener{
	protected WhenNfcScript whenNfcScript;
    private transient View prototypeView;
    private transient NfcTagData nfcTag;
    private transient NfcTagData oldSelectedNfcTag;
	private static final long serialVersionUID = 1L;
	private transient AdapterView<?> adapterView;


	public WhenNfcBrick() {
        this.oldSelectedNfcTag = null;
        //TODO: nfcTag needs to be initialized (?)
        this.nfcTag = null;
        this.whenNfcScript = new WhenNfcScript();
        this.whenNfcScript.setMatchAll(true);
	}

	public WhenNfcBrick(String tagName, String tagUid) {
        this.oldSelectedNfcTag = null;
        this.nfcTag = new NfcTagData();
        this.nfcTag.setNfcTagName(tagName);
        this.nfcTag.setNfcTagUid(tagUid);
		this.whenNfcScript = new WhenNfcScript(nfcTag);
        this.whenNfcScript.setMatchAll(false);
	}

	public WhenNfcBrick(WhenNfcScript script) {
        this.oldSelectedNfcTag = null;
        this.nfcTag = script.getNfcTag();
		this.whenNfcScript = script;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		WhenNfcBrick copyBrick = (WhenNfcBrick) clone();

		for (NfcTagData data : sprite.getNfcTagList()) {
			if (data.getNfcTagUid().equals(nfcTag.getNfcTagUid())) {
				copyBrick.nfcTag = data;
				break;
			}
		}
		copyBrick.whenNfcScript = whenNfcScript;
		return copyBrick;
	}

	@Override
	public Script getScriptSafe() {
		if (whenNfcScript == null) {
			setWhenNfcScript(new WhenNfcScript(nfcTag));
		}
		return whenNfcScript;
	}

	@Override
	public Brick clone() {
        //return new WhenNfcBrick(sprite, new WhenNfcScript(sprite));
		return new WhenNfcBrick(new WhenNfcScript(nfcTag));
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
        if (whenNfcScript == null)
        {
            whenNfcScript = new WhenNfcScript(nfcTag);
        }
        final Brick brickInstance = this;
        view = View.inflate(context, R.layout.brick_when_nfc, null);
        view = getViewWithAlpha(alphaValue);

        setCheckboxView(R.id.brick_when_nfc_checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                adapter.handleCheck(brickInstance, isChecked);
            }
        });

		final Spinner nfcSpinner = (Spinner) view.findViewById(R.id.brick_when_nfc_spinner);

		//nfcSpinner.setFocusableInTouchMode(false);
		//nfcSpinner.setFocusable(false);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			nfcSpinner.setClickable(true);
			nfcSpinner.setEnabled(true);
		} else {
			nfcSpinner.setClickable(false);
			nfcSpinner.setEnabled(false);
		}

		//nfcSpinner.setAdapter(NfcTagContainer.getMessageAdapter(context));
        final ArrayAdapter<NfcTagData> spinnerAdapter = createNfcTagAdapter(context);

        SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, spinnerAdapter);

        nfcSpinner.setAdapter(spinnerAdapterWrapper);
		nfcSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedTag = nfcSpinner.getSelectedItem().toString();//context.getString(R.string.brick_when_nfc_default_all);
                Log.d("WhenNfcBrick", "onItemSelected(): " + selectedTag);

                if (position == 0) {
                    nfcTag = null;
                }
                else if (selectedTag.equals(context.getString(R.string.brick_when_nfc_default_all))){
                    whenNfcScript.setMatchAll(true);
                    whenNfcScript.setNfcTag(null);
                    //TODO: rework all
                    nfcTag = null;//(NfcTagData)parent.getItemAtPosition(position);
                    oldSelectedNfcTag = nfcTag;
                    adapterView = parent;
                } else {
                    if (whenNfcScript.getNfcTag() == null) {
						whenNfcScript.setNfcTag(new NfcTagData());
					}
                    for (NfcTagData selTag : ProjectManager.getInstance().getCurrentSprite().getNfcTagList())
                    {
                        if (selTag.getNfcTagName().equals(selectedTag)) {
                            whenNfcScript.setNfcTag(selTag);
                            nfcTag = (NfcTagData)parent.getItemAtPosition(position); //selTag
                            break;
                        }
                    }
					//whenNfcScript.getNfcTag().setNfcTagName(selectedTag);
					whenNfcScript.setMatchAll(false);
                    oldSelectedNfcTag = nfcTag;
                    adapterView = parent;
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
		/*
        int position = 1;
		if (whenNfcScript != null && whenNfcScript.getTagName() != null) {
			position = NfcTagContainer.getPositionOfMessageInAdapter(spinner.getContext(), whenNfcScript.getTagName());
		}
		spinner.setSelection(position, true);
        */
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

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_when_nfc_layout);
			//setCheckboxView(R.id.brick_when_nfc_checkbox);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textWhenNfcLabel = (TextView) view.findViewById(R.id.brick_when_nfc_label);
			textWhenNfcLabel.setTextColor(textWhenNfcLabel.getTextColors().withAlpha(alphaValue));
			Spinner nfcSpinner = (Spinner) view.findViewById(R.id.brick_when_nfc_spinner);
			ColorStateList color = textWhenNfcLabel.getTextColors().withAlpha(alphaValue);
			nfcSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			this.alphaValue = alphaValue;

		}
		return view;
	}

    private ArrayAdapter<NfcTagData> createNfcTagAdapter(Context context) {
        ArrayAdapter<NfcTagData> arrayAdapter = new ArrayAdapter<NfcTagData>(context, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NfcTagData dummyNfcTagData = new NfcTagData();
        dummyNfcTagData.setNfcTagName(context.getString(R.string.new_broadcast_message));
        arrayAdapter.add(dummyNfcTagData);
        //TODO: rework all
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
        prototypeView = View.inflate(context, R.layout.brick_when_nfc, null);
        Spinner nfcSpinner = (Spinner) prototypeView.findViewById(R.id.brick_when_nfc_spinner);
        nfcSpinner.setFocusableInTouchMode(false);
        nfcSpinner.setFocusable(false);
        SpinnerAdapter nfcSpinnerAdapter = createNfcTagAdapter(context);//NfcTagContainer.getMessageAdapter(context);
        nfcSpinner.setAdapter(nfcSpinnerAdapter);
        setSpinnerSelection(nfcSpinner);
        return prototypeView;
    }

    private void setOnNfcTagDataListChangedAfterNewListener(Context context) {
        ScriptActivity scriptActivity = (ScriptActivity) context;
        NfcTagFragment nfcTagFragment = (NfcTagFragment) scriptActivity.getFragment(ScriptActivity.FRAGMENT_NFCTAGS);
        if (nfcTagFragment != null) {
            nfcTagFragment.setOnNfcTagDataListChangedAfterNewListener(this);
        }
    }

    private class SpinnerAdapterWrapper implements SpinnerAdapter {

        protected Context context;
        protected ArrayAdapter<NfcTagData> spinnerAdapter;

        private boolean isTouchInDropDownView;

        public SpinnerAdapterWrapper(Context context, ArrayAdapter<NfcTagData> spinnerAdapter) {
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
                if (paramInt == 0) {
                    switchToNfcTagFragmentFromScriptFragment();
                }
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

        private void switchToNfcTagFragmentFromScriptFragment() {
            ScriptActivity scriptActivity = ((ScriptActivity) context);
            scriptActivity.switchToFragmentFromScriptFragment(ScriptActivity.FRAGMENT_NFCTAGS);

            setOnNfcTagDataListChangedAfterNewListener(context);
        }
    }

    @Override
    public int getRequiredResources() {
        return NFC_ADAPTER;
    }

    @Override
    public void onNfcTagDataListChangedAfterNew(NfcTagData nfcTagData) {
        oldSelectedNfcTag = nfcTagData;
        setNfcTag(nfcTagData);
    }

    public NfcTagData getNfcTag(){
        return nfcTag;
    }

    public void setNfcTag(NfcTagData nfcTagData){
        this.nfcTag = nfcTagData;
    }

    public WhenNfcScript getWhenNfcScript() {
        return whenNfcScript;
    }

    public void setWhenNfcScript(WhenNfcScript whenNfcScript) {
        this.whenNfcScript = whenNfcScript;
    }
}
