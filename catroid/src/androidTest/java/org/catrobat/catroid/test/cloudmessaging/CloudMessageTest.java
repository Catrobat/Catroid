/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.test.cloudmessaging;

import android.test.AndroidTestCase;

import org.catrobat.catroid.cloudmessaging.CloudMessage;

public class CloudMessageTest extends AndroidTestCase {

	private CloudMessage cloudMessage;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cloudMessage = new CloudMessage();
	}

	public void testAllRequiredFieldsAvailable() {

		assertFalse(cloudMessage.isValidData());

		cloudMessage.setNotificationTitle("Test Notification Title");
		cloudMessage.setNotificationMessage("Test Notification Message");
		cloudMessage.setNotificationUrl("https://www.catrobat.org/");

		assertTrue(cloudMessage.isValidData());
	}

	public void testRequiredFieldIsMissing() {

		cloudMessage.setNotificationMessage("Test Notification Message");
		cloudMessage.setNotificationUrl("https://www.catrobat.org/");

		assertFalse(cloudMessage.isValidData());
	}

	public void testRequiredFieldIsEmpty() {

		cloudMessage.setNotificationTitle("");
		cloudMessage.setNotificationMessage("Test Notification Message");
		cloudMessage.setNotificationUrl("https://www.catrobat.org/");

		assertFalse(cloudMessage.isValidData());
	}

	public void testNotificationUrlIsInValid() {

		cloudMessage.setNotificationTitle("Test Notification Title");
		cloudMessage.setNotificationMessage("Test Notification Message");
		cloudMessage.setNotificationUrl("htt0s://www.catrobat.org/");

		assertFalse(cloudMessage.isValidData());
	}
}
