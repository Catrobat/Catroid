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
package org.catrobat.catroid.uiespresso.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.adapter.NfcTagAdapter;
import org.catrobat.catroid.ui.fragment.NfcTagFragment;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

public final class UiNFCTestUtils {
	// Suppress default constructor for non-instantiability
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

	public static void fakeNfcTag(String uid, NdefMessage ndefMessage, Tag tag, Activity callingActivity) {
		Class activityCls = callingActivity.getClass();

		PendingIntent pendingIntent = PendingIntent.getActivity(callingActivity, 0,
				new Intent(callingActivity, activityCls).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

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
			// todo: Why do we need this check?
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

	public static void enableNfcBricks(Context context) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(SettingsActivity.SETTINGS_SHOW_NFC_BRICKS, true).commit();
	}

	public static void gotoNfcFragment(int nfcBrickPosition) {
		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.perform(click());
		onView(withText(R.string.new_nfc_tag))
				.perform(click());
	}

	public static void clickSpinnerValueOnBrick(int spinnerResourceId, int position, int stringResourceId) {
		onBrickAtPosition(position).onSpinner(spinnerResourceId)
				.perform(click());
		onView(withText(UiTestUtils.getResourcesString(stringResourceId)))
				.perform(click());
		onBrickAtPosition(position).onSpinner(spinnerResourceId)
				.checkShowsText(stringResourceId);
	}

	public static NfcTagFragment getNfcTagFragment(Activity callingActivity) {
		ScriptActivity activity = (ScriptActivity) callingActivity;
		return (NfcTagFragment) activity.getFragment(ScriptActivity.FRAGMENT_NFCTAGS);
	}

	public static void checkNfcSpinnerContains(int nfcBrickPosition, List<String>
			spinnerValuesResourceIdsContained, List<String> spinnerValuesResourceIdsNotContained) {
		onBrickAtPosition(nfcBrickPosition).onSpinner(R.id.brick_when_nfc_spinner)
				.perform(click());

		for (String spinnerValue : spinnerValuesResourceIdsContained) {
			onView(withText(spinnerValue))
					.check(matches(isDisplayed()));
		}
		for (String spinnerValue : spinnerValuesResourceIdsNotContained) {
			onView(withText(spinnerValue))
					.check(doesNotExist());
		}
		onView(withText(spinnerValuesResourceIdsContained.get(0)))
				.perform(click());
	}

	public static NfcTagAdapter getNfcTagAdapter(Activity callingActivity) {
		return (NfcTagAdapter) getNfcTagFragment(callingActivity).getListAdapter();
	}
}
