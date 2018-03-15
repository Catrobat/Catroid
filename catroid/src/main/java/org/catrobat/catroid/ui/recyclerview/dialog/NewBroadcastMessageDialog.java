/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.dialog;

import android.content.DialogInterface;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

public class NewBroadcastMessageDialog extends BrickTextDialog {

	NewBroadcastMessageInterface newBroadcastMessageInterface;
	String newString;

	public NewBroadcastMessageDialog(NewBroadcastMessageInterface newBroadcastMessageInterface, String newString) {
		super(R.string.dialog_new_broadcast_message_title, R.string.dialog_new_broadcast_message_name, newString);
		this.newBroadcastMessageInterface = newBroadcastMessageInterface;
		this.newString = newString;
	}

	@Override
	protected boolean onPositiveButtonClick() {
		String newMessage = inputLayout.getEditText().getText().toString().trim();
		if (newMessage.equals(newString)) {
			dismiss();
			return false;
		}
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		currentProject.getBroadcastMessageContainer().addBroadcastMessage(newMessage);
		newBroadcastMessageInterface.setBroadcastMessage(newMessage);
		newBroadcastMessageInterface.updateSpinnerSelection();
		return true;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		newBroadcastMessageInterface.updateSpinnerSelection();
		super.onDismiss(dialog);
	}

	public interface NewBroadcastMessageInterface {
		void setBroadcastMessage(String message);
		void updateSpinnerSelection();
	}
}
