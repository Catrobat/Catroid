/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.firmata.Serial;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

/**
 * Serial adapter for regular TCP socket
 */
public class SocketSerialAdapter extends StreamingSerialAdapter implements Serializable {

    private transient Socket socket;

    private String address;
    private int port;

    public SocketSerialAdapter(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void start() throws SerialException {
        try {
            socket = new Socket(address, port);
            setInStream(socket.getInputStream());
            setOutStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new SerialException(e);
        }
        super.start();
    }

    @Override
    public void stop() throws SerialException {
        setStopReading();

        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            throw new SerialException(e);
        }
        super.stop();
    }
}
