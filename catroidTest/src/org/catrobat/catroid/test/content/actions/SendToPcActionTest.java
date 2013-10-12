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
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SendToPcAction;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.io.Command;
import org.catrobat.catroid.io.Connection;
import org.catrobat.catroid.io.PcConnectionManager;
import org.catrobat.catroid.test.utils.Reflection;

import java.util.ArrayList;

public class SendToPcActionTest extends InstrumentationTestCase {

	public void testNormalBehavior() throws InterruptedException {
		SendToPcBrick sendToPcBrick = new SendToPcBrick();
		SendToPcAction action = (SendToPcAction) ExtendedActions.sendToPc(sendToPcBrick);
		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		Connection connection = new Connection("192.0.0.1", connectionManager, "testServer");
		action.setConnection(connection);
		action.act(1.0f);
		Thread.sleep(1000);
		@SuppressWarnings("unchecked")
		ArrayList<Command> commandList = (ArrayList<Command>) Reflection.getPrivateField(connection, "commandList");
		assertTrue("CommandList in connection is empty!", commandList.size() > 0);
	}
}
