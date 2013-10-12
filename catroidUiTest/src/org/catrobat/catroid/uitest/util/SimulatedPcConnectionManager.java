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
package org.catrobat.catroid.uitest.util;

import android.content.Context;

import org.catrobat.catroid.io.Connection;
import org.catrobat.catroid.io.PcConnectionManager;
import org.catrobat.catroid.io.PcConnectionManager.PcConnectionManagerCreator;

import java.net.Socket;

public class SimulatedPcConnectionManager implements PcConnectionManagerCreator {

	private Socket mockClient;
	private String ip;
	private String serverName;
	private PcConnectionManager connectionManager;

	public SimulatedPcConnectionManager(Socket mockClient, String ip, String serverName) {
		this.mockClient = mockClient;
		this.ip = ip;
		this.serverName = serverName;
		serverName = "kitten";
	}

	public PcConnectionManager create() {
		connectionManager = new ExtendedPcConnectionManager(null);
		return connectionManager;
	}

	public String getServerName() {
		return serverName;
	}

	class ExtendedPcConnectionManager extends PcConnectionManager {

		protected ExtendedPcConnectionManager(Context context) {
			super(context);
		}

		@Override
		public Connection createNewConnection(String serverName) {
			Connection connection = new Connection(ip, connectionManager, serverName);
			Reflection.setPrivateField(connection, "client", mockClient);
			return connection;
		}
	}
}