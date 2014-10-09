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
package org.catrobat.catroid.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public final class NfcHandler {
	private static final String TAG = NfcHandler.class.getSimpleName();

    private NfcHandler(){

    }

	public static void processIntent(Intent intent) {
		if (intent == null) {
			return;
		}

        String uid = getUid(intent);

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();

        //String nameForUid = spriteList.//NfcTagContainer.getNameForUid(uid);
        //Log.d(TAG, "namefor uid:" + nameForUid);

		for (Sprite sprite : spriteList) {
			sprite.createWhenNfcScriptAction(uid);
		}
	}

    public static String getUid(Intent intent){
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String uid = byteArrayToHex(tagId);

            //if whole tag is needed
            //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //String uid = byteArrayToHex(tag.getId());

            Log.d(TAG, "read successful. uid = hex:" + uid);

            // uncomment for debugging ndef information
            //			Ndef ndefTag;
            //			if (null != (ndefTag = Ndef.get(tag))) {
            //				try {
            //					ndefTag.connect();
            //					NdefMessage ndefMessage = ndefTag.getNdefMessage();
            //					for (NdefRecord record : ndefMessage.getRecords()) {
            //						Log.d(TAG, "record, tnf: " + record.getTnf() + " " + new String(record.getPayload()));
            //					}
            //					ndefTag.close();
            //				} catch (IOException e) {
            //					// ...
            //				} catch (FormatException e) {
            //					// ...
            //				}
            //
            //			}
            return uid;
        }
        return null;
    }

	public static String byteArrayToHex(byte[] a) {
        if (a == null){
            return null;
        }
		StringBuilder sb = new StringBuilder();
		for (byte b : a) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
}