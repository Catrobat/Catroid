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
package org.catrobat.catroid.test.content.actions

import android.nfc.FormatException
import org.junit.runner.RunWith
import org.catrobat.catroid.test.content.actions.SetNfcTagActionTest
import org.junit.Before
import kotlin.Throws
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.formulaeditor.InterpretationException
import android.nfc.NdefMessage
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import org.catrobat.catroid.nfc.NfcHandler
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.Project
import org.junit.Test
import java.lang.Exception
import java.nio.charset.Charset

@RunWith(AndroidJUnit4::class)
class SetNfcTagActionTest {
    private var project: Project? = null
    private val emptyRecord: Short = 0x00
    private val wellKnownRecord: Short = 0x01
    private val mimeMediaRecord: Short = 0x02
    private val externalRecord: Short = 0x04
    private val catrobatWeb = "www.catrobat.org"
    private val catrobatText = "catrobat"
    private val catrobatEmailAddress = "contact@catrobat.org"
    private val catrobatPhoneNumber = "+16504279594"
    private val smsTextMessage = "SMS from Catrobat"
    private val wellKnownType = "U".toByteArray(UTF8_CHARSET)
    private val catrobatAsHex = "catrobat".toByteArray(UTF8_CHARSET)
    private val catrobatWebAsHex = "www.catrobat.org".toByteArray(UTF8_CHARSET)
    private val externalType = "catrobat.com:catroid".toByteArray(UTF8_CHARSET)
    @Before
    @Throws(Exception::class)
    fun setUp() {
        project = Project(ApplicationProvider.getApplicationContext(), "testProject")
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    @Throws(InterpretationException::class)
    fun testMakeEmptyMessage() {
        val generatedMessage =
            NfcHandler.createMessage("example text", BrickValues.TNF_EMPTY.toInt())
        Assert.assertEquals(getPayload(generatedMessage).size, 0)
        Assert.assertEquals(getTnf(generatedMessage), emptyRecord)
        Assert.assertEquals(getType(generatedMessage).size, 0)
    }

    @Test
    @Throws(InterpretationException::class)
    fun testCreateTextMessage() {
        val mimeType = "text/plain"
        val mimeTypeBytes = mimeType.toByteArray(UTF8_CHARSET)
        val generatedMessage =
            NfcHandler.createMessage(catrobatText, BrickValues.TNF_MIME_MEDIA.toInt())
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), catrobatAsHex)
        Assert.assertEquals(getTnf(generatedMessage), mimeMediaRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), mimeTypeBytes)
    }

    @Test
    @Throws(InterpretationException::class, FormatException::class)
    fun testCreateHttpMessage() {
        var generatedMessage =
            NfcHandler.createMessage(catrobatWeb, BrickValues.TNF_WELL_KNOWN_HTTP.toInt())
        val messageBytes =
            addProtocolInFrontOfMessage(BrickValues.NDEF_PREFIX_HTTP.toShort(), catrobatWebAsHex)
        val messageType = wellKnownType
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
        generatedMessage = NfcHandler.createMessage(
            "https://www.catrobat.org",
            BrickValues.TNF_WELL_KNOWN_HTTP.toInt()
        )
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
        generatedMessage = NfcHandler.createMessage(
            "http://www.catrobat.org",
            BrickValues.TNF_WELL_KNOWN_HTTP.toInt()
        )
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
    }

    @Test
    @Throws(InterpretationException::class, FormatException::class)
    fun testCreateHttpsMessage() {
        var generatedMessage =
            NfcHandler.createMessage(catrobatWeb, BrickValues.TNF_WELL_KNOWN_HTTPS.toInt())
        val messageBytes =
            addProtocolInFrontOfMessage(BrickValues.NDEF_PREFIX_HTTPS.toShort(), catrobatWebAsHex)
        val messageType = wellKnownType
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
        generatedMessage = NfcHandler.createMessage(
            "https://www.catrobat.org",
            BrickValues.TNF_WELL_KNOWN_HTTPS.toInt()
        )
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
        generatedMessage = NfcHandler.createMessage(
            "http://www.catrobat.org",
            BrickValues.TNF_WELL_KNOWN_HTTPS.toInt()
        )
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
    }

    @Test
    @Throws(InterpretationException::class)
    fun testMakeEMailMessage() {
        val generatedMessage = NfcHandler.createMessage(
            catrobatEmailAddress,
            BrickValues.TNF_WELL_KNOWN_MAILTO.toInt()
        )
        val messageBytes = addProtocolInFrontOfMessage(
            BrickValues.NDEF_PREFIX_MAILTO.toShort(), catrobatEmailAddress.toByteArray(
                UTF8_CHARSET
            )
        )
        val messageType = wellKnownType
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
    }

    @Test
    @Throws(InterpretationException::class)
    fun testMakePhoneNumberMessage() {
        val generatedMessage =
            NfcHandler.createMessage(catrobatPhoneNumber, BrickValues.TNF_WELL_KNOWN_TEL.toInt())
        val messageBytes = addProtocolInFrontOfMessage(
            BrickValues.NDEF_PREFIX_TEL.toShort(), catrobatPhoneNumber.toByteArray(
                UTF8_CHARSET
            )
        )
        val messageType = wellKnownType
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), wellKnownRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
    }

    @Test
    @Throws(InterpretationException::class)
    fun testMakeSMSMessage() {
        val generatedMessage =
            NfcHandler.createMessage(catrobatPhoneNumber, BrickValues.TNF_WELL_KNOWN_SMS.toInt())
        val messageBytes = smsMessageFormat(catrobatPhoneNumber, smsTextMessage)
        val messageType = "nfclab.com:smsService".toByteArray(UTF8_CHARSET)
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), messageBytes)
        Assert.assertEquals(getTnf(generatedMessage), externalRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), messageType)
    }

    @Test
    @Throws(InterpretationException::class)
    fun testMakeExternalTypeMessage() {
        val generatedMessage =
            NfcHandler.createMessage(catrobatText, BrickValues.TNF_EXTERNAL_TYPE.toInt())
        org.junit.Assert.assertArrayEquals(getPayload(generatedMessage), catrobatAsHex)
        Assert.assertEquals(getTnf(generatedMessage), externalRecord)
        org.junit.Assert.assertArrayEquals(getType(generatedMessage), externalType)
    }

    @Test
    @Throws(InterpretationException::class)
    fun testDeleteProtocolPrefix() {
        val addressWithoutProt = "www.catrobat.org"
        var addressWithProt = "http://www.catrobat.org"
        Assert.assertEquals(
            addressWithoutProt,
            NfcHandler.deleteProtocolPrefixIfExist(addressWithProt)
        )
        addressWithProt = "https://www.catrobat.org"
        Assert.assertEquals(
            addressWithoutProt,
            NfcHandler.deleteProtocolPrefixIfExist(addressWithProt)
        )
        addressWithProt = "anything://www.catrobat.org"
        Assert.assertEquals(
            addressWithoutProt,
            NfcHandler.deleteProtocolPrefixIfExist(addressWithProt)
        )
        addressWithProt = "ftp://www.catrobat.org"
        Assert.assertEquals(
            addressWithoutProt,
            NfcHandler.deleteProtocolPrefixIfExist(addressWithProt)
        )
    }

    private fun getTnf(msg: NdefMessage): Short {
        return msg.records[0].tnf
    }

    private fun getType(msg: NdefMessage): ByteArray {
        return msg.records[0].type
    }

    private fun getPayload(msg: NdefMessage): ByteArray {
        return msg.records[0].payload
    }

    private fun addProtocolInFrontOfMessage(protocol: Short, message: ByteArray): ByteArray {
        val protocolBytes = shortToByteArray(protocol)
        val finalMessage = ByteArray(message.size + protocolBytes.size)
        System.arraycopy(protocolBytes, 0, finalMessage, 0, protocolBytes.size)
        System.arraycopy(message, 0, finalMessage, protocolBytes.size, message.size)
        return finalMessage
    }

    private fun smsMessageFormat(number: String, message: String): ByteArray {
        return "sms:$number?body=$message".toByteArray(UTF8_CHARSET)
    }

    fun shortToByteArray(input: Short): ByteArray {
        val output = ByteArray(2)
        output[0] = input.toByte()
        output[1] = (input.toInt() shr 8).toByte()
        if (output[1] == 0x0.toByte()) {
            return output.copyOfRange(0, 1)
        }
        return output
    }

    companion object {
        private val UTF8_CHARSET = Charset.forName("UTF-8")
    }
}