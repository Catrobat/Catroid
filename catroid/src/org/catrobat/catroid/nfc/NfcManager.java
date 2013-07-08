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
package org.catrobat.catroid.nfc;

import java.math.BigInteger;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

public class NfcManager {
	private static NfcManager INSTANCE = new NfcManager();
	private long uid;

	private NfcManager() {
		resetUid();
	}

	public static NfcManager getInstance() {
		return INSTANCE;
	}

	public long getUid() {
		return uid;
	}

	public void resetUid() {
		uid = 0;
	}

	public void processIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			byte[] byteId = tag.getId();

			uid = byteArrayToInt(byteId);
		}
	}

	private long byteArrayToInt(byte[] byteId) {
		BigInteger n = new BigInteger(byteId);
		return n.longValue();
	}
}
