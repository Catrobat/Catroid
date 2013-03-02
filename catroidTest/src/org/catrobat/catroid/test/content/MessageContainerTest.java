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
package org.catrobat.catroid.test.content;

import java.util.Set;
import java.util.Vector;

import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;

import android.test.InstrumentationTestCase;

public class MessageContainerTest extends InstrumentationTestCase {

	public void testContainer() {
		String testMessage1 = "test1";
		String testMessage2 = "test2";

		MessageContainer.addMessage(testMessage1);
		Set<String> messages = MessageContainer.getMessages();
		assertEquals("Wrong amount of messages", 1, messages.size());
		assertTrue("Doesn't contain message", messages.contains(testMessage1));

		BroadcastScript script = new BroadcastScript(null);
		MessageContainer.addMessage(testMessage2, script);
		MessageContainer.addMessage(testMessage2);
		assertEquals("Wrong amount of messages", 2, messages.size());
		assertTrue("Doesn't contain message", messages.contains(testMessage2));

		Vector<BroadcastScript> receiverVector = MessageContainer.getReceiverOfMessage(testMessage2);
		assertTrue("Doesn't contain script", receiverVector.contains(script));

		MessageContainer.deleteReceiverScript(testMessage2, script);
		receiverVector = MessageContainer.getReceiverOfMessage(testMessage2);
		assertFalse("Still contains removed script", receiverVector.contains(script));
	}
}
