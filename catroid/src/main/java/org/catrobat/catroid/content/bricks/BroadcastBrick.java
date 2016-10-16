/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class BroadcastBrick extends BrickBaseType implements BroadcastMessage {
	private static final long serialVersionUID = 1L;

	protected String broadcastMessage;
	protected transient AdapterView<?> adapterView;

	protected Object readResolve() {
		MessageContainer.addMessage(broadcastMessage);
		return this;
	}

	public BroadcastBrick(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
		MessageContainer.addMessage(broadcastMessage);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BroadcastBrick copyBrick = (BroadcastBrick) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new BroadcastBrick(broadcastMessage);
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
	public void setMessage(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_broadcast, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		IconsUtil.addIcon(context, (TextView) view.findViewById(R.id.brick_broadcast_label),
				context.getString(R.string.category_event));

		setCheckboxView(R.id.brick_broadcast_checkbox);
		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_broadcast_spinner);

		broadcastSpinner.setAdapter(MessageContainer.getMessageAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedMessage = broadcastSpinner.getSelectedItem().toString();
				if (selectedMessage.equals(context.getString(R.string.new_broadcast_message))) {
					showNewMessageDialog(broadcastSpinner);
				} else {
					broadcastMessage = selectedMessage;
				}
				if (adapterView != null) {
					TextView spinnerText = (TextView) adapterView.getChildAt(0);
					TextSizeUtil.enlargeTextView(spinnerText);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		setSpinnerSelection(broadcastSpinner);

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_broadcast, null);
		Spinner broadcastSpinner = (Spinner) prototypeView.findViewById(R.id.brick_broadcast_spinner);

		SpinnerAdapter broadcastSpinnerAdapter = MessageContainer.getMessageAdapter(context);
		broadcastSpinner.setAdapter(broadcastSpinnerAdapter);
		setSpinnerSelection(broadcastSpinner);
		return prototypeView;
	}

	protected void setSpinnerSelection(Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(spinner.getContext(), broadcastMessage);
		spinner.setSelection(position, true);
	}

	// TODO: BroadcastBrick and BroadcastReceiverBrick contain this identical method.
	protected void showNewMessageDialog(final Spinner spinner) {
		final Context context = spinner.getContext();
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
				inputTitle.setText(R.string.dialog_new_broadcast_message_name);
			}

			@Override
			protected boolean handleOkButton() {
				String newMessage = input.getText().toString().trim();
				if (newMessage.isEmpty() || newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}

				if (newMessage.contains(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR)) {
					Utils.showErrorDialog(getActivity(), R.string.brick_broadcast_invalid_symbol);
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

			@Override
			protected String getTitle() {
				return getString(R.string.dialog_new_broadcast_message_title);
			}
		};

		editDialog.show(((Activity) context).getFragmentManager(), "dialog_broadcast_brick");
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createBroadcastAction(sprite, broadcastMessage));
		return null;
	}
}
