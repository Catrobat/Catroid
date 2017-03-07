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
package org.catrobat.catroid.test.content.actions;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.test.AndroidTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.nfc.NfcHandler;

import java.nio.charset.Charset;
import java.util.Arrays;

public class SetNfcTagActionTest extends AndroidTestCase {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private Project project;
	private short emptyRecord = 0x00;
	private short wellKnownRecord = 0x01;
	private short mimeMediaRecord = 0x02;
	private short externalRecord = 0x04;
	private String catrobatWeb = "www.catrobat.org";
	private String catrobatText = "catrobat";
	private String catrobatEmailAddress = "contact@catrobat.org";
	private String catrobatPhoneNumber = "+16504279594";
	private String smsTextMessage = "SMS from Catrobat";
	private byte[] wellKnownType = "U".getBytes(UTF8_CHARSET);
	private byte[] catrobatAsHex = "catrobat".getBytes(UTF8_CHARSET);
	private byte[] catrobatWebAsHex = "www.catrobat.org".getBytes(UTF8_CHARSET);
	private byte[] externalType = "catrobat.com:catroid".getBytes(UTF8_CHARSET);

	@Override
	protected void setUp() throws Exception {
		project = new Project(null, "testProject");
		ProjectManager.getInstance().setProject(project);
		super.setUp();
	}

	public void testMakeEmptyMessage() throws InterpretationException {
		NdefMessage generatedMessage = NfcHandler.createMessage("example text", BrickValues.TNF_EMPTY);
		assertEquals("Empty Message payload is false.", getPayload(generatedMessage).length, 0);
		assertEquals("Empty Message TNF is false.", getTnf(generatedMessage), emptyRecord);
		assertEquals("Empty Message type is false.", getType(generatedMessage).length, 0);
	}

	public void testCreateTextMessage() throws InterpretationException {
		String mimeType = "text/plain";
		byte[] mimeTypeBytes = mimeType.getBytes(UTF8_CHARSET);
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatText, BrickValues.TNF_MIME_MEDIA);
		assertTrue("MIME_MEDIA Message payload is false.", Arrays.equals(getPayload(generatedMessage), catrobatAsHex));
		assertEquals("MIME_MEDIA Message TNF is false.", getTnf(generatedMessage), mimeMediaRecord);
		assertTrue("MIME_MEDIA Message type is false.", Arrays.equals(getType(generatedMessage), mimeTypeBytes));
	}

	public void testCreateHttpMessage() throws InterpretationException, FormatException {
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatWeb, BrickValues.TNF_WELL_KNOWN_HTTP);
		byte[] messageBytes = addProtocolInFrontOfMessage(BrickValues.NDEF_PREFIX_HTTP, catrobatWebAsHex);
		byte[] messageType = wellKnownType;
		assertTrue("HTTP Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("HTTP Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("HTTP Message type is false.", Arrays.equals(getType(generatedMessage), messageType));

		generatedMessage = NfcHandler.createMessage("https://www.catrobat.org", BrickValues
				.TNF_WELL_KNOWN_HTTP);
		assertTrue("HTTP Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("HTTP Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("HTTP Message type is false.", Arrays.equals(getType(generatedMessage), messageType));

		generatedMessage = NfcHandler.createMessage("http://www.catrobat.org", BrickValues
				.TNF_WELL_KNOWN_HTTP);
		assertTrue("HTTP Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("HTTP Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("HTTP Message type is false.", Arrays.equals(getType(generatedMessage), messageType));
	}

	public void testCreateHttpsMessage() throws InterpretationException, FormatException {
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatWeb, BrickValues.TNF_WELL_KNOWN_HTTPS);
		byte[] messageBytes = addProtocolInFrontOfMessage(BrickValues.NDEF_PREFIX_HTTPS, catrobatWebAsHex);
		byte[] messageType = wellKnownType;
		assertTrue("HTTPS Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("HTTPS Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("HTTPS Message type is false.", Arrays.equals(getType(generatedMessage), messageType));

		generatedMessage = NfcHandler.createMessage("https://www.catrobat.org", BrickValues
				.TNF_WELL_KNOWN_HTTPS);
		assertTrue("HTTPS Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("HTTPS Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("HTTPS Message type is false.", Arrays.equals(getType(generatedMessage), messageType));

		generatedMessage = NfcHandler.createMessage("http://www.catrobat.org", BrickValues
				.TNF_WELL_KNOWN_HTTPS);
		assertTrue("HTTPS Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("HTTPS Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("HTTPS Message type is false.", Arrays.equals(getType(generatedMessage), messageType));
	}

	public void testMakeEMailMessage() throws InterpretationException {
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatEmailAddress, BrickValues.TNF_WELL_KNOWN_MAILTO);
		byte[] messageBytes = addProtocolInFrontOfMessage(BrickValues.NDEF_PREFIX_MAILTO, catrobatEmailAddress.getBytes(UTF8_CHARSET));
		byte[] messageType = wellKnownType;
		assertTrue("Email Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("Email Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("Email Message type is false.", Arrays.equals(getType(generatedMessage), messageType));
	}

	public void testMakePhoneNumberMessage() throws InterpretationException {
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatPhoneNumber, BrickValues.TNF_WELL_KNOWN_TEL);
		byte[] messageBytes = addProtocolInFrontOfMessage(BrickValues.NDEF_PREFIX_TEL, catrobatPhoneNumber.getBytes(UTF8_CHARSET));
		byte[] messageType = wellKnownType;
		assertTrue("Phone Number Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("Phone Number Message TNF is false.", getTnf(generatedMessage), wellKnownRecord);
		assertTrue("Phone Number Message type is false.", Arrays.equals(getType(generatedMessage), messageType));
	}

	public void testMakeSMSMessage() throws InterpretationException {
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatPhoneNumber, BrickValues.TNF_WELL_KNOWN_SMS);
		byte[] messageBytes = smsMessageFormat(catrobatPhoneNumber, smsTextMessage);
		byte[] messageType = "nfclab.com:smsService".getBytes(UTF8_CHARSET);
		assertTrue("SMS Message payload is false.", Arrays.equals(getPayload(generatedMessage), messageBytes));
		assertEquals("SMS Message TNF is false.", getTnf(generatedMessage), externalRecord);
		assertTrue("SMS Message type is false.", Arrays.equals(getType(generatedMessage), messageType));
	}

	public void testMakeExternalTypeMessage() throws InterpretationException {
		NdefMessage generatedMessage = NfcHandler.createMessage(catrobatText, BrickValues.TNF_EXTERNAL_TYPE);
		assertTrue("External Type Message payload is false.", Arrays.equals(getPayload(generatedMessage),
				catrobatAsHex));
		assertEquals("External Type Message TNF is false.", getTnf(generatedMessage), externalRecord);
		assertTrue("External Type Message type is false.", Arrays.equals(getType(generatedMessage), externalType));
	}

	public void testDeleteProtocolPrefix() throws InterpretationException {
		String addressWithoutProt = "www.catrobat.org";
		String addressWithProt = "http://www.catrobat.org";
		assertEquals("Prefix not deleted!", addressWithoutProt, NfcHandler
				.deleteProtocolPrefixIfExist(addressWithProt));

		addressWithProt = "https://www.catrobat.org";
		assertEquals("Prefix not deleted!", addressWithoutProt, NfcHandler
				.deleteProtocolPrefixIfExist(addressWithProt));

		addressWithProt = "anything://www.catrobat.org";
		assertEquals("Prefix not deleted!", addressWithoutProt, NfcHandler
				.deleteProtocolPrefixIfExist(addressWithProt));

		addressWithProt = "ftp://www.catrobat.org";
		assertEquals("Prefix not deleted!", addressWithoutProt, NfcHandler
				.deleteProtocolPrefixIfExist(addressWithProt));
	}

	private short getTnf(NdefMessage msg) {
		return msg.getRecords()[0].getTnf();
	}

	private byte[] getType(NdefMessage msg) {
		return msg.getRecords()[0].getType();
	}

	private byte[] getPayload(NdefMessage msg) {
		return msg.getRecords()[0].getPayload();
	}

	private byte[] addProtocolInFrontOfMessage(short protocol, byte[] message) {
		byte[] protocolBytes = shortToByteArray(protocol);
		byte[] finalMessage = new byte[message.length + protocolBytes.length];
		System.arraycopy(protocolBytes, 0, finalMessage, 0, protocolBytes.length);
		System.arraycopy(message, 0, finalMessage, protocolBytes.length, message.length);
		return finalMessage;
	}

	private byte[] smsMessageFormat(String number, String message) {
		return ("sms:" + number + "?body=" + message).getBytes(UTF8_CHARSET);
	}

	private byte[] shortToByteArray(short input) {
		byte[] output = new byte[2];
		output[0] = (byte) input;
		output[1] = (byte) (input >> 8);
		if (output[1] == 0x0) {
			byte[] newOutput = new byte[1];
			newOutput[0] = output[0];
			return newOutput;
		}
		return output;
	}
}
