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
package org.catrobat.catroid.test.nfc;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.catrobat.catroid.nfc.NfcManager;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

public class NfcManagerTest extends TestCase {

	public void testDefaultValueZero() throws Exception {
		assertEquals("Default nfc_uid sensor value is not 0.", 0, NfcManager.getInstance().getUid());
	}

	public void testNfcIntent() throws Exception {
		byte[] byteId = new byte[] { 0, 0, 0, 0, 0, 0, 0, 1 };
		simulateNfcTag(byteId);

		assertEquals("nfc_uid sensor value is not 1.", 1, NfcManager.getInstance().getUid());

		byteId = new byte[] { 0, 0, 0, 0, 0, 0, 0, 42 };
		simulateNfcTag(byteId);

		assertEquals("nfc_uid sensor value is not 42.", 42, NfcManager.getInstance().getUid());
	}

	private void simulateNfcTag(byte[] byteId) throws Exception {
		Intent intent = new Intent(NfcAdapter.ACTION_TAG_DISCOVERED);

		@SuppressWarnings("rawtypes")
		Class nfcClass = Class.forName("android.nfc.Tag");
		@SuppressWarnings("unchecked")
		Method nfcMethod = nfcClass.getMethod("createMockTag", new Class[] { byte[].class, int[].class, Bundle[].class });

		int[] techList = new int[] { 1 };
		Bundle[] techListExtra = new Bundle[] { new Bundle(1) };
		Tag tag = (Tag) nfcMethod.invoke(nfcClass, new Object[] { byteId, techList, techListExtra });

		intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
		NfcManager.getInstance().processIntent(intent);
	}
}
