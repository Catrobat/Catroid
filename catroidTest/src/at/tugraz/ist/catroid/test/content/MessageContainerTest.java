/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content;

import java.util.Set;
import java.util.Vector;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.common.MessageContainer;
import at.tugraz.ist.catroid.content.BroadcastScript;

public class MessageContainerTest extends InstrumentationTestCase {

	public void testContainer() {
		String testMessage1 = "test1";
		String testMessage2 = "test2";

		MessageContainer testContainer = new MessageContainer();
		testContainer.addMessage(testMessage1);
		Set<String> messages = testContainer.getMessages();
		assertEquals("Wrong amount of messages", 1, messages.size());
		assertTrue("Doesn't contain message", messages.contains(testMessage1));

		BroadcastScript script = new BroadcastScript(null);
		testContainer.addMessage(testMessage2, script);
		testContainer.addMessage(testMessage2);
		assertEquals("Wrong amount of messages", 2, messages.size());
		assertTrue("Doesn't contain message", messages.contains(testMessage2));

		Vector<BroadcastScript> receiverVector = testContainer.getReceiverOfMessage(testMessage2);
		assertTrue("Doesn't contain script", receiverVector.contains(script));

		testContainer.deleteReceiverScript(testMessage2, script);
		receiverVector = testContainer.getReceiverOfMessage(testMessage2);
		assertFalse("Still contains removed script", receiverVector.contains(script));
	}
}
