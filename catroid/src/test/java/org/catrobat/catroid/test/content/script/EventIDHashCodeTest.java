/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.content.script;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.eventids.BroadcastEventId;
import org.catrobat.catroid.content.eventids.RaspiEventId;
import org.catrobat.catroid.content.eventids.SetLookEventId;
import org.catrobat.catroid.content.eventids.WhenConditionEventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class EventIDHashCodeTest {

	private static final String EVENT_STRING = "test";

	@Test
	public void setBackgroundNullTest() {
		Sprite sprite = Mockito.mock(Sprite.class);
		SetLookEventId backgroundEventId = new SetLookEventId(sprite, null);
		assertEquals(backgroundEventId.hashCode(), sprite.hashCode() * 31);
	}

	@Test
	public void setBackgroundTest() {
		Sprite sprite = Mockito.mock(Sprite.class);
		LookData lookData = Mockito.mock(LookData.class);
		SetLookEventId backgroundEventId = new SetLookEventId(sprite, lookData);
		assertEquals(backgroundEventId.hashCode(), sprite.hashCode() * 31 + lookData.hashCode());
	}

	@Test
	public void whenConditionTest() {
		Formula formula = Mockito.mock(Formula.class);
		WhenConditionEventId whenConditionEventId = new WhenConditionEventId(formula);
		assertEquals(whenConditionEventId.hashCode(), formula.hashCode());
	}

	@Test
	public void whenConditionNullTest() {
		WhenConditionEventId whenConditionEventId = new WhenConditionEventId(null);
		assertEquals(whenConditionEventId.hashCode(), 0);
	}

	@Test
	public void raspiTest() {
		RaspiEventId raspiEventId = new RaspiEventId(null, EVENT_STRING);
		assertEquals(raspiEventId.hashCode(), EVENT_STRING.hashCode());
	}

	@Test
	public void raspiNullTest() {
		RaspiEventId raspiEventId = new RaspiEventId(null, null);
		assertEquals(raspiEventId.hashCode(), 0);
	}

	@Test
	public void broadcastTest() {
		BroadcastEventId broadcastEventId = new BroadcastEventId(EVENT_STRING);
		assertEquals(broadcastEventId.hashCode(), EVENT_STRING.hashCode());
	}

	@Test
	public void broadcastNullTest() {
		BroadcastEventId broadcastEventId = new BroadcastEventId(null);
		assertEquals(broadcastEventId.hashCode(), 0);
	}
}
