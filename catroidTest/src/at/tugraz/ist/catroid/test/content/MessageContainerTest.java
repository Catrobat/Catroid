/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.test.content;

import java.util.Set;
import java.util.Vector;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.common.MessageContainer;
import at.tugraz.ist.catroid.content.BroadcastScript;

public class MessageContainerTest extends InstrumentationTestCase {

	String testMsg1 = "test1";
	String testMsg2 = "test2";

	public void testContainer() {
		MessageContainer testContainer = new MessageContainer();
		testContainer.addMessage(testMsg1);
		Set<String> messages = testContainer.getMessages();
		assertEquals("Wrong amount of messages", 1, messages.size());
		assertTrue("Doesn't contain message", messages.contains(testMsg1));
		BroadcastScript script = new BroadcastScript("test", null);
		testContainer.addMessage(testMsg2, script);
		testContainer.addMessage(testMsg2);
		assertEquals("Wrong amount of messages", 2, messages.size());
		assertTrue("Doesn't contain message", messages.contains(testMsg2));
		Vector<BroadcastScript> receiverVec = testContainer.getReceiverOfMessage(testMsg2);
		assertTrue("Doesn't contain script", receiverVec.contains(script));
		testContainer.deleteReceiverScript(testMsg2, script);
		receiverVec = testContainer.getReceiverOfMessage(testMsg2);
		assertFalse("Still contains removed script", receiverVec.contains(script));

	}
}
