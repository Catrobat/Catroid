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
package org.catrobat.catroid.bluetoothdummyserver;

import java.io.IOException;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

//in case the following error occurs "Native Library bluecove_x64 not available"
// you need to install  libbluetooth-dev

public class BTServer {
	private static final UUID BLUETOOTH_SERVER_UUID = new UUID("eb8ec53af07046e0b6ff1645c931f858", false);
	private static final String BT_NAME = "BT Connection Server";
	private static final String CONNECTION_STRING = "btspp://localhost:" + BLUETOOTH_SERVER_UUID + ";name=" + BT_NAME
			+ ";authenticate=false;encrypt=false;";

	public static void main(String[] args) throws IOException, InterruptedException {
		StreamConnectionNotifier stream_conn_notifier = (StreamConnectionNotifier) Connector.open(CONNECTION_STRING);
		StreamConnection connection = null;
		while (true) {
			System.out.println("[CONNECTOR] Waiting for incoming connection...");
			connection = stream_conn_notifier.acceptAndOpen();
			System.out.println("[CONNECTOR] Client Connected...");
			new Thread(new BTConnectionHandler(connection)).start();
		}

		// stream_conn_notifier.close();
	}
}
