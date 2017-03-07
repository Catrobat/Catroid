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
package org.catrobat.catroid.content.actions;

import android.nfc.NdefMessage;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.stage.StageActivity;

public class SetNfcTagAction extends TemporalAction {

	private Sprite sprite;
	private Formula nfcNdefMessage;
	private int nfcTagNdefSpinnerSelection;
	private static final String TAG = SetNfcTagAction.class.getSimpleName();

	@Override
	protected void update(float percent) {
		if (nfcNdefMessage == null) {
			return;
		}

		try {
			NdefMessage message = NfcHandler.createMessage(nfcNdefMessage.interpretString(sprite),
					nfcTagNdefSpinnerSelection);
			StageActivity.addNfcTagMessageToDeque(message);
		} catch (Exception e) {
			Log.d(TAG, "No new message was added to the Deque", e);
		}
	}

	public void setNfcTagNdefSpinnerSelection(int spinnerSelection) {
		this.nfcTagNdefSpinnerSelection = spinnerSelection;
	}

	public void setNfcNdefMessage(Formula nfcNdefMessage) {
		this.nfcNdefMessage = nfcNdefMessage;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
