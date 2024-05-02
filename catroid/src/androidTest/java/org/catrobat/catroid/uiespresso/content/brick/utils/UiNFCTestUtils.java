/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.content.brick.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.widget.Spinner;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;

import java.util.List;

import androidx.test.espresso.DataInteraction;

import static org.hamcrest.Matchers.instanceOf;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public final class UiNFCTestUtils {
	private UiNFCTestUtils() {
		throw new AssertionError();
	}

	public static final String FIRST_TEST_TAG_ID = "111111";
	public static final String SECOND_TEST_TAG_ID = "222222";

	public static final String FIRST_TEST_TAG_UID = NfcHandler.byteArrayToHex(FIRST_TEST_TAG_ID.getBytes());
	public static final String SECOND_TEST_TAG_UID = NfcHandler.byteArrayToHex(SECOND_TEST_TAG_ID.getBytes());

	public static final String THIRD_TEST_TAG_ID = "333333";
	public static final String FOURTH_TEST_TAG_ID = "444444";
	public static final String NFC_NDEF_STRING_1 = "I am the first!";
	public static final String NFC_NDEF_STRING_2 = "And I am the second!";

	public static final String READ_TAG_ID = "readTagId";
	public static final String READ_TAG_MESSAGE = "readTagMessage";
	public static final String NUM_DETECTED_TAGS = "numDetectedTags";

	public static final String TAG_NAME_TEST1 = "tagNameTest1";
	public static final String TAG_NAME_TEST2 = "tagNameTest2";

	public static void fakeNfcTag(String uid, NdefMessage ndefMessage, Tag tag, Activity callingActivity) {
		Class activityCls = callingActivity.getClass();

		PendingIntent pendingIntent;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			pendingIntent = PendingIntent.getActivity(callingActivity, 0,
					new Intent(callingActivity, activityCls).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | PendingIntent.FLAG_IMMUTABLE), 0);
		} else {
			pendingIntent = PendingIntent.getActivity(callingActivity, 0,
					new Intent(callingActivity, activityCls).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		}

		String intentAction = NfcAdapter.ACTION_TAG_DISCOVERED;
		byte[] tagId = uid.getBytes();

		Intent intent = new Intent();
		intent.setAction(intentAction);
		if (tag != null) {
			intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
		}
		intent.putExtra(NfcAdapter.EXTRA_ID, tagId);
		if (ndefMessage != null) {
			intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, new NdefMessage[] {ndefMessage});
			if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentAction)) {
				Uri uri = ndefMessage.getRecords()[0].toUri();
				String mime = ndefMessage.getRecords()[0].toMimeType();
				if (uri != null) {
					intent.setData(uri);
				} else {
					intent.setType(mime);
				}
			}
		}

		try {
			pendingIntent.send(callingActivity, Activity.RESULT_OK, intent);
		} catch (PendingIntent.CanceledException e) {
			Log.d("fakeNfcTag", e.getMessage());
		}
	}

	public static NfcBrickDataInteractionWrapper onNfcBrickAtPosition(int position) {
		return new NfcBrickDataInteractionWrapper(onData(instanceOf(Brick.class))
				.inAdapterView(ScriptListMatchers.isScriptListView())
				.atPosition(position));
	}

	public static class NfcBrickDataInteractionWrapper extends BrickDataInteractionWrapper {

		public NfcBrickDataInteractionWrapper(DataInteraction dataInteraction) {
			super(dataInteraction);
		}

		public NfcBrickSpinnerDataInteractionWrapper onSpinner(int spinnerResourceId) {
			dataInteraction.onChildView(withId(spinnerResourceId)).check(matches(instanceOf(Spinner.class)));
			return new NfcBrickSpinnerDataInteractionWrapper(
					dataInteraction.onChildView(withId(spinnerResourceId)));
		}
	}

	public static class NfcBrickSpinnerDataInteractionWrapper extends BrickSpinnerDataInteractionWrapper {
		public NfcBrickSpinnerDataInteractionWrapper(DataInteraction dataInteraction) {
			super(dataInteraction);
		}

		public NfcBrickSpinnerDataInteractionWrapper performSelectString(String selection) {
			dataInteraction.perform(click());

			onView(withText(selection))
					.perform(click());

			return new NfcBrickSpinnerDataInteractionWrapper(dataInteraction);
		}

		public NfcBrickSpinnerDataInteractionWrapper checkTagNamesAvailable(List<String> stringValues) {
			dataInteraction.perform(click());

			for (String value : stringValues) {
				onView(withText(value))
						.check(matches(isDisplayed()));
			}
			pressBack();
			return new NfcBrickSpinnerDataInteractionWrapper(dataInteraction);
		}

		public NfcBrickSpinnerDataInteractionWrapper checkTagNamesNotDisplayed(List<String> stringValues) {
			dataInteraction.perform(click());

			for (String value : stringValues) {
				onView(withText(value))
						.check(doesNotExist());
			}
			pressBack();
			return new NfcBrickSpinnerDataInteractionWrapper(dataInteraction);
		}
	}
}
