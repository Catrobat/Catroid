/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.devices.mindstorms;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MindstormsConnectionImpl implements MindstormsConnection {

	private BluetoothConnection bluetoothConnection;
	private OutputStream legoOutputStream = null;
	private DataInputStream legoInputStream = null;

	private boolean isConnected = false;

	private short commandCounter = 1;

	public MindstormsConnectionImpl(BluetoothConnection btConnection) {
		this.bluetoothConnection = btConnection;
	}

	@Override
	public void init() throws MindstormsException {

		try {
			legoInputStream = new DataInputStream(bluetoothConnection.getInputStream());
			legoOutputStream = bluetoothConnection.getOutputStream();
			isConnected = true;
		} catch (IOException e) {
			isConnected = false;
			throw new MindstormsException(e, "Cannot establish BtConnection");
		}
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public void disconnect() {

		isConnected = false;

		bluetoothConnection.disconnect();

		legoInputStream = null;
		legoOutputStream = null;
	}

	@Override
	public synchronized byte[] sendAndReceive(MindstormsCommand command) throws MindstormsException {
		send(command);
		return receive();
	}

	@Override
	public void send(MindstormsCommand command) throws MindstormsException {
		try {
			int messageLength = command.getLength();
			byte[] message = command.getRawCommand();
			byte[] data = new byte[command.getLength() + 2];
			data[0] = (byte) (messageLength & 0x00FF);
			data[1] = (byte) ((messageLength & 0xFF00) >> 8);

			System.arraycopy(message, 0, data, 2, messageLength);

			synchronized (legoOutputStream) {
				legoOutputStream.write(data, 0, messageLength + 2);
				legoOutputStream.flush();
			}
		} catch (IOException e) {
			throw new MindstormsException(e, "Error on message send.");
		}
	}

	@Override
	public short getCommandCounter() {
		return commandCounter;
	}

	@Override
	public void incCommandCounter() {
		commandCounter++;
	}

	protected byte[] receive() throws MindstormsException {
		byte[] data = new byte[2];
		byte[] payload;

		try {
			legoInputStream.readFully(data, 0, 2);
			int expectedLength = ((data[0] & 0xFF) | (data[1] & 0xFF) << 8);
			payload = new byte[expectedLength];

			legoInputStream.readFully(payload, 0, expectedLength);
		} catch (IOException e) {
			throw new MindstormsException(e, "Read Error");
		}

		return payload;
	}
}
