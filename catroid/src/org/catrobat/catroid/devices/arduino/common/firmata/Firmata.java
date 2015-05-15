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

package org.catrobat.catroid.devices.arduino.common.firmata;

import android.util.Log;

import org.catrobat.catroid.devices.arduino.common.firmata.message.AnalogMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cConfigMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cReadRequestMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cRequestMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportAnalogCapabilityMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportAnalogPinMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportDigitalPortMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportFirmwareVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportProtocolVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SamplingIntervalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ServoConfigMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SetPinModeMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.StringSysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexByteMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SystemResetMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.AnalogCapabilityMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.AnalogMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.DigitalMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.FirmwareVersionMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.I2cReplyMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.IMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.ProtocolVersionMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.StringSysexMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.reader.SysexMessageReader;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerialListener;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.AnalogMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.DigitalMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.I2cConfigMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.I2cRequestMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.IMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.ReportAnalogPinMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.ReportDigitalPortMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.ReportProtocolVersionMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.SamplingIntervalMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.ServoConfigMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.SetPinModeMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.SysexByteMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.SysexMessageWriter;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.SystemResetMessageWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.decodeCommand;

/**
 * Plain Java Firmata impl
 */
public class Firmata implements IFirmata, ISerialListener {

	public static final String TAG = Firmata.class.getSimpleName();
    private static final int BUFFER_SIZE = 1024;

    private ISerial serial;

    public ISerial getSerial() {
        return serial;
    }

    public void setSerial(ISerial serial) {
        this.serial = serial;
        serial.addListener(this);
    }

    public Firmata() {
        initWriters();
        initReaders();
    }

    private List<IFirmata.Listener> listeners = new ArrayList<IFirmata.Listener>();

    public void addListener(IFirmata.Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(IFirmata.Listener listener) {
        listeners.remove(listener);
    }

    public boolean containsListener(IFirmata.Listener listener) {
        return listeners.contains(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    private static Map<Class<? extends Message>, IMessageWriter> writers;

    private void initWriters() {
        writers = new HashMap<Class<? extends Message>, IMessageWriter>();

        writers.put(AnalogMessage.class, new AnalogMessageWriter());
        writers.put(DigitalMessage.class, new DigitalMessageWriter());
        writers.put(ReportAnalogPinMessage.class, new ReportAnalogPinMessageWriter());
        writers.put(ReportDigitalPortMessage.class, new ReportDigitalPortMessageWriter());
        writers.put(ReportProtocolVersionMessage.class, new ReportProtocolVersionMessageWriter());
        writers.put(SetPinModeMessage.class, new SetPinModeMessageWriter());
        writers.put(SystemResetMessage.class, new SystemResetMessageWriter());

        // sysex messages
        SysexMessageWriter sysexMessageWriter = new SysexMessageWriter();
        writers.put(SysexMessage.class, sysexMessageWriter);
        writers.put(StringSysexMessage.class, sysexMessageWriter);
        writers.put(ReportFirmwareVersionMessage.class, sysexMessageWriter);
        writers.put(ServoConfigMessage.class, new ServoConfigMessageWriter());
        writers.put(SamplingIntervalMessage.class, new SamplingIntervalMessageWriter());
        writers.put(I2cRequestMessage.class, new I2cRequestMessageWriter());
        writers.put(I2cReadRequestMessage.class, new I2cRequestMessageWriter());
        writers.put(I2cConfigMessage.class, new I2cConfigMessageWriter());
		writers.put(ReportAnalogCapabilityMessage.class, sysexMessageWriter);
		writers.put(SysexByteMessage.class, new SysexByteMessageWriter());
    }

    private static List<IMessageReader> readers;

    private IMessageReader activeReader;

    private void initReaders() {
        readers = new ArrayList<IMessageReader>();
        potentialReaders = new ArrayList<IMessageReader>();

        readers.add(new AnalogMessageReader());
        readers.add(new DigitalMessageReader());
        readers.add(new FirmwareVersionMessageReader());
        readers.add(new ProtocolVersionMessageReader());
        readers.add(new SysexMessageReader());
        readers.add(new StringSysexMessageReader());
        readers.add(new I2cReplyMessageReader());
		readers.add(new AnalogCapabilityMessageReader());
    }

    /**
     * Constructor
     *
     * @param serial specify concrete ISerial implementation
     */
    public Firmata(ISerial serial) {
        this();
        setSerial(serial);
    }

    /**
     * Send message to Arduino board
     *
     * @param message concrete outcoming message
     */
    public void send(Message message) throws SerialException {
        IMessageWriter writer = writers.get(message.getClass());
        if (writer == null) {
			throw new RuntimeException("Unknown message type: " + message.getClass());
		}

        writer.write(message, serial);
    }

    private List<IMessageReader> potentialReaders;

    private byte[] buffer = new byte[BUFFER_SIZE];

    private int bufferLength;

    public void onDataReceived(Object serialImpl) {
        try {
            if (serial.available() > 0) {
                int incomingByte = serial.read();
                if (incomingByte >= 0) {
					onDataReceived(incomingByte);
				}
            }
        } catch (SerialException e) {
            Log.e(TAG, "Failed to read received data", e);
        }
    }

    public void onException(Throwable e) {
    }

    /**
     * Incoming byte received event
     * @param incomingByte incoming byte
     */
    public void onDataReceived(int incomingByte) {
        buffer[bufferLength++] = (byte)incomingByte;

        if (activeReader == null) {
            // new message byte is received
            int command = decodeCommand(incomingByte);

            if (potentialReaders.size() == 0) {
                // first byte check
                findPotentialReaders(command);
            } else {
                // not first byte check
                // few potential readers found, so we should check the next bytes to define MessageReader
                filterPotentialReaders(command);
            }

            tryHandle();

        } else {
            // continue handling with activeReader
            activeReader.read(buffer, bufferLength);

            if (activeReader.finishedReading()) {
                // message is ready
                for (IFirmata.Listener eachListener : listeners) {
					activeReader.fireEvent(eachListener);
				}
                reinitBuffer();
            }
        }
    }

    // pass the next bytes in order to define according reader
    private void filterPotentialReaders(int command) {
        List<IMessageReader> newPotentialReaders = new ArrayList<IMessageReader>();

        for (IMessageReader eachPotentialReader : potentialReaders) {
			if (eachPotentialReader.canRead(buffer, bufferLength, command)) {
				newPotentialReaders.add(eachPotentialReader);
			}
		}

        potentialReaders = newPotentialReaders;
    }

    private void tryHandle() {
        int potentialReadersCount = potentialReaders.size();
        switch (potentialReadersCount) {

            // unknown byte
            case 0:
                for (int i=0; i<bufferLength; i++) {
					for (Listener eachListener : listeners) {
						eachListener.onUnknownByteReceived(buffer[i]);
					}
				}
                reinitBuffer();
                break;

            // the only one reader
            case 1:
                activeReader = potentialReaders.get(0);
                activeReader.startReading();
                break;

            // default:
            //  (in case if few writers are found, we should pass the next bytes to define final reader)
        }
    }

    private void reinitBuffer() {
        bufferLength = 0;
        activeReader = null;
        potentialReaders.clear();
    }

    private void findPotentialReaders(int command) {
        for (IMessageReader eachReader : readers) {
            if (eachReader.canRead(buffer, bufferLength, command)) {
                potentialReaders.add(eachReader);
            }
        }
    }

}
