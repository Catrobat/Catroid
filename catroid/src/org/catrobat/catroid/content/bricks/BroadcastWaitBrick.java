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
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class BroadcastWaitBrick extends BrickBaseType implements BroadcastMessage {
	private static final long serialVersionUID = 1L;

	private String broadcastMessage;
	private transient AdapterView<?> adapterView;

	private Object readResolve() {
		MessageContainer.addMessage(broadcastMessage);
		return this;
	}

	public BroadcastWaitBrick(Sprite sprite, String broadcastMessage) {
		this.sprite = sprite;
		this.broadcastMessage = broadcastMessage;
		MessageContainer.addMessage(broadcastMessage);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		BroadcastWaitBrick copyBrick = (BroadcastWaitBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new BroadcastWaitBrick(sprite, broadcastMessage);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_broadcast_wait, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_broadcast_wait_checkbox);

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(BroadcastWaitBrick.this, isChecked);
			}
		});

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_wait_spinner);
		broadcastSpinner.setFocusableInTouchMode(false);
		broadcastSpinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			broadcastSpinner.setClickable(true);
			broadcastSpinner.setEnabled(true);
		} else {
			broadcastSpinner.setClickable(false);
			broadcastSpinner.setEnabled(false);
		}

		broadcastSpinner.setAdapter(MessageContainer.getMessageAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = broadcastSpinner.getSelectedItem().toString();
				if (selectedMessage.equals(context.getString(R.string.new_broadcast_message))) {
					showNewMessageDialog(broadcastSpinner);
				} else {
					broadcastMessage = selectedMessage;
					adapterView = parent;
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
		View prototypeView = View.inflate(context, R.layout.brick_broadcast_wait, null);
		Spinner broadcastWaitSpinner = (Spinner) prototypeView.findViewById(R.id.brick_broadcast_wait_spinner);
		broadcastWaitSpinner.setFocusableInTouchMode(false);
		broadcastWaitSpinner.setFocusable(false);
		SpinnerAdapter broadcastWaitSpinnerAdapter = MessageContainer.getMessageAdapter(context);
		broadcastWaitSpinner.setAdapter(broadcastWaitSpinnerAdapter);
		setSpinnerSelection(broadcastWaitSpinner);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_broadcast_wait_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		TextView textBroadcastWaitLabel = (TextView) view.findViewById(R.id.brick_broadcast_wait_label);
		textBroadcastWaitLabel.setTextColor(textBroadcastWaitLabel.getTextColors().withAlpha(alphaValue));
		Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_wait_spinner);
		ColorStateList color = textBroadcastWaitLabel.getTextColors().withAlpha(alphaValue);
		broadcastSpinner.getBackground().setAlpha(alphaValue);
		if (adapterView != null) {
			((TextView) adapterView.getChildAt(0)).setTextColor(color);
		}

		this.alphaValue = (alphaValue);
		return view;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(spinner.getContext(), broadcastMessage);
		spinner.setSelection(position, true);
	}

	private void showNewMessageDialog(final Spinner spinner) {
		final Context context = spinner.getContext();
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
			}

			@Override
			protected boolean handleOkButton() {
				String newMessage = (input.getText().toString()).trim();
				if (newMessage.isEmpty() || newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}

				broadcastMessage = newMessage;
				MessageContainer.addMessage(broadcastMessage);
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

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.broadcastFromWaiter(sprite, broadcastMessage));
		return null;
	}
}
