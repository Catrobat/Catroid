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
package org.catrobat.catroid.nfc;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.InterpretationException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public final class NfcHandler {
	private static final String TAG = NfcHandler.class.getSimpleName();
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static String nfcTagId = "0x0";
	private static String nfcTagMessage = "";

	private NfcHandler() {
	}

	public static void processIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		String uid = getTagIdFromIntent(intent);
		setLastNfcTagId(uid);
		setLastNfcTagMessage(getMessageFromIntent(intent));

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteListWithClones();

		for (Sprite sprite : spriteList) {
			sprite.createWhenNfcScriptAction(uid);
		}
	}

	public static String getTagIdFromIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

			String uidHex = String.valueOf(byteArrayToHex(tagId));

			setLastNfcTagId(uidHex);
			Log.d(TAG, "read successful. uid = int: " + uidHex);
			return uidHex;
		}
		return null;
	}

	public static String getMessageFromIntent(Intent intent) {
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				NdefMessage[] messages = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					messages[i] = (NdefMessage) rawMsgs[i];
				}
				if (messages[0] != null) {
					String result = "";
					byte[] payload = messages[0].getRecords()[0].getPayload();
					if (payload.length > 0) {
						int i = payloadStartContainsText(payload[0]) ? 0 : 1;
						for (; i < payload.length; i++) {
							result += (char) payload[i];
						}
					}
					return result;
				}
			}
		}
		return null;
	}

	private static boolean payloadStartContainsText(byte payloadStart) {
		return payloadStart != 1;
	}

	public static Object getLastNfcTagMessage() {
		return nfcTagMessage;
	}

	public static String getLastNfcTagId() {
		return nfcTagId;
	}

	public static void setLastNfcTagId(String tagID) {
		nfcTagId = tagID;
	}

	public static void setLastNfcTagMessage(String message) {
		nfcTagMessage = message;
	}

	public static String byteArrayToHex(byte[] a) {
		if (a == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : a) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	public static void writeTag(Tag tag, NdefMessage message) {
		if (tag != null) {
			try {
				Ndef ndefTag = Ndef.get(tag);
				if (ndefTag == null) {
					NdefFormatable nForm = NdefFormatable.get(tag);
					if (nForm != null) {
						nForm.connect();
						nForm.format(message);
						nForm.close();
					}
				} else {
					ndefTag.connect();
					ndefTag.writeNdefMessage(message);
					ndefTag.close();
				}
			} catch (IOException | FormatException e) {
				Log.d(TAG, "Couldn't create message", e);
			}
		}
	}

	public static NdefMessage createMessage(String message, int spinnerSelection) throws InterpretationException {
		NdefRecord ndefRecord;
		short tnf = 0;
		byte[] type;
		byte[] id;
		byte[] payload;
		byte[] uriField;
		switch (spinnerSelection) {
			case BrickValues.TNF_EMPTY:
				tnf = NdefRecord.TNF_EMPTY;
				type = new byte[] {};
				id = new byte[] {};
				payload = new byte[] {};
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_MIME_MEDIA:
				tnf = NdefRecord.TNF_MIME_MEDIA;
				String mimeType = "text/plain";
				type = mimeType.getBytes(UTF8_CHARSET);
				id = new byte[] {};
				payload = message.getBytes(UTF8_CHARSET);
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_WELL_KNOWN_HTTP:
				tnf = NdefRecord.TNF_WELL_KNOWN;
				type = NdefRecord.RTD_URI;
				id = new byte[] {};
				uriField = deleteProtocolPrefixIfExist(message).getBytes(UTF8_CHARSET);
				payload = new byte[uriField.length + 1];
				payload[0] = BrickValues.NDEF_PREFIX_HTTP;
				System.arraycopy(uriField, 0, payload, 1, uriField.length);
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_WELL_KNOWN_HTTPS:
				tnf = NdefRecord.TNF_WELL_KNOWN;
				type = NdefRecord.RTD_URI;
				id = new byte[] {};
				uriField = deleteProtocolPrefixIfExist(message).getBytes(UTF8_CHARSET);
				payload = new byte[uriField.length + 1];
				payload[0] = BrickValues.NDEF_PREFIX_HTTPS;
				System.arraycopy(uriField, 0, payload, 1, uriField.length);
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_WELL_KNOWN_MAILTO:
				tnf = NdefRecord.TNF_WELL_KNOWN;
				type = NdefRecord.RTD_URI;
				id = new byte[] {};
				uriField = message.getBytes(UTF8_CHARSET);
				payload = new byte[uriField.length + 1];
				payload[0] = BrickValues.NDEF_PREFIX_MAILTO;
				System.arraycopy(uriField, 0, payload, 1, uriField.length);
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_WELL_KNOWN_TEL:
				tnf = NdefRecord.TNF_WELL_KNOWN;
				type = NdefRecord.RTD_URI;
				id = new byte[] {};
				uriField = message.getBytes(UTF8_CHARSET);
				payload = new byte[uriField.length + 1];
				payload[0] = BrickValues.NDEF_PREFIX_TEL;
				System.arraycopy(uriField, 0, payload, 1, uriField.length);
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_WELL_KNOWN_SMS:
				tnf = NdefRecord.TNF_EXTERNAL_TYPE;
				type = "nfclab.com:smsService".getBytes(UTF8_CHARSET);
				id = new byte[] {};
				String textMessage = "SMS from Catrobat";
				String smsMessage = "sms:" + message + "?body=" + textMessage;
				payload = smsMessage.getBytes(UTF8_CHARSET);
				ndefRecord = new NdefRecord(tnf, type, id, payload);
				break;
			case BrickValues.TNF_EXTERNAL_TYPE:
				String domain = "catrobat.com";
				String externalType = "catroid";
				byte[] data = message.getBytes(UTF8_CHARSET);
				ndefRecord = NdefRecord.createExternal(domain, externalType, data);
				break;
			default:
				ndefRecord = NdefRecord.createUri(message);
		}
		return new NdefMessage(new NdefRecord[] { ndefRecord });
	}

	public static String deleteProtocolPrefixIfExist(String url) {
		return url.replaceFirst("^\\w+://", "");
	}
}
