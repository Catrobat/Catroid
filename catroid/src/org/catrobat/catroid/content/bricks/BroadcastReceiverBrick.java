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

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class BroadcastReceiverBrick extends ScriptBrick {
	private static final long serialVersionUID = 1L;
	private BroadcastScript receiveScript;
	private transient String oldMessage = "";
	private transient String currentSelected = "";

	public BroadcastReceiverBrick() {

	}

	public BroadcastReceiverBrick(Sprite sprite, BroadcastScript receiveScript) {
		this.sprite = sprite;
		this.receiveScript = receiveScript;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		BroadcastReceiverBrick copyBrick = (BroadcastReceiverBrick) clone();
		copyBrick.sprite = sprite;
		copyBrick.receiveScript = (BroadcastScript) script;
		return copyBrick;
	}

	public String getSelectedMessage() {
		return currentSelected;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (receiveScript == null) {
			receiveScript = new BroadcastScript(sprite);
		}

		view = View.inflate(context, R.layout.brick_broadcast_receive, null);

		setCheckboxView(R.id.brick_broadcast_receive_checkbox);

		//method moved to to DragAndDropListView since it is not working on 2.x
		/*
		 * checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		 * 
		 * @Override
		 * public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		 * checked = isChecked;
		 * if (!checked) {
		 * for (Brick currentBrick : adapter.getCheckedBricks()) {
		 * currentBrick.setCheckedBoolean(false);
		 * }
		 * }
		 * adapter.handleCheck(brickInstance, checked);
		 * }
		 * });
		 */

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_receive_spinner);
		broadcastSpinner.setFocusableInTouchMode(false);
		broadcastSpinner.setFocusable(false);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			broadcastSpinner.setClickable(true);
			broadcastSpinner.setEnabled(true);
		} else {
			broadcastSpinner.setClickable(false);
			broadcastSpinner.setEnabled(false);
		}

		final ArrayAdapter<String> spinnerAdapter = MessageContainer.getMessageAdapter(context);

		SpinnerAdapterWrapper spinnerAdapterWrapper = new SpinnerAdapterWrapper(context, broadcastSpinner,
				spinnerAdapter);

		broadcastSpinner.setAdapter(spinnerAdapterWrapper);

		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedString = ((String) parent.getItemAtPosition(position)).trim();
				if (start) {
					start = false;
					currentSelected = selectedString;
					return;
				}
				String message = selectedString;

				if (message == context.getString(R.string.new_broadcast_message)) {
					receiveScript.setBroadcastMessage("");
				} else {
					receiveScript.setBroadcastMessage(message);
					oldMessage = receiveScript.getBroadcastMessage();
					currentSelected = selectedString;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(broadcastSpinner);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_broadcast_receive, null);
		Spinner broadcastReceiverSpinner = (Spinner) prototypeView.findViewById(R.id.brick_broadcast_receive_spinner);
		broadcastReceiverSpinner.setFocusableInTouchMode(false);
		broadcastReceiverSpinner.setFocusable(false);
		SpinnerAdapter broadcastReceiverSpinnerAdapter = MessageContainer.getMessageAdapter(context);
		broadcastReceiverSpinner.setAdapter(broadcastReceiverSpinnerAdapter);
		if (broadcastReceiverSpinnerAdapter.getCount() > 1) {
			oldMessage = broadcastReceiverSpinnerAdapter.getItem(1).toString();
		}
		setSpinnerSelection(broadcastReceiverSpinner);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_broadcast_receive_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public Brick clone() {
		return new BroadcastReceiverBrick(sprite, null);
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (receiveScript == null) {
			receiveScript = new BroadcastScript(sprite);
		}

		return receiveScript;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(receiveScript.getBroadcastMessage());
		if (position > 0) {
			spinner.setSelection(position, true);
		} else {
			if (oldMessage != null && !oldMessage.equals("")) {
				spinner.setSelection(MessageContainer.getPositionOfMessageInAdapter(oldMessage), true);
			} else {
				SpinnerAdapter spinnerAdapter = spinner.getAdapter();
				if (spinnerAdapter != null && spinnerAdapter.getCount() > 1) {
					spinner.setSelection(1, true);
				} else {
					spinner.setSelection(0, true);
				}
			}
		}
	}

	private class SpinnerAdapterWrapper implements SpinnerAdapter {

		protected Context context;
		protected Spinner spinner;
		protected ArrayAdapter<String> spinnerAdapter;

		private boolean isTouchInDropDownView;

		public SpinnerAdapterWrapper(Context context, Spinner spinner, ArrayAdapter<String> spinnerAdapter) {
			this.context = context;
			this.spinner = spinner;
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
			String currentMessage = spinnerAdapter.getItem(paramInt).toString();
			if (!currentMessage.equals(context.getString(R.string.new_broadcast_message))) {
				oldMessage = currentMessage;
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
					showNewMessageDialog();
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

			dropDownView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
					isTouchInDropDownView = true;
					return false;
				}
			});

			return dropDownView;
		}

		protected void showNewMessageDialog() {
			BrickTextDialog editDialog = new BrickTextDialog() {

				@Override
				protected void initialize() {
				}

				@Override
				protected boolean handleOkButton() {
					String newMessage = (input.getText().toString()).trim();
					if (newMessage.length() == 0
							|| newMessage.equals(context.getString(R.string.new_broadcast_message))) {
						dismiss();
						return false;
					}

					receiveScript.setBroadcastMessage(newMessage);
					oldMessage = newMessage;
					setSpinnerSelection(spinner);

					return true;
				}

				@Override
				public void onDismiss(DialogInterface dialog) {
					setSpinnerSelection(spinner);
					super.onDismiss(dialog);
				}
			};

			editDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog_broadcast_brick");
		}
	}
}
