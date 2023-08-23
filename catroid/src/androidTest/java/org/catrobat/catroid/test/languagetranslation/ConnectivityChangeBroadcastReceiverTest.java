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

package org.catrobat.catroid.test.languagetranslation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import org.catrobat.catroid.languagetranslator.ConnectivityChangeBroadcastReceiver;
import org.catrobat.catroid.languagetranslator.LanguageTranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ConnectivityChangeBroadcastReceiverTest {

	private ConnectivityChangeBroadcastReceiver connectivityChangeBroadcastReceiver;
	private LanguageTranslator languageTranslator;
	private Context context;
	private Intent intent;

	@Before
	public void setUp() {
		languageTranslator = mock(LanguageTranslator.class);
		connectivityChangeBroadcastReceiver = new ConnectivityChangeBroadcastReceiver(languageTranslator);
		context = mock(Context.class);
		intent = new Intent();
		intent.setAction(ConnectivityManager.CONNECTIVITY_ACTION);
	}

	@Test
	public void hasConnectionTest() {
		when(languageTranslator.hasConnection(context)).thenReturn(true);
		connectivityChangeBroadcastReceiver.onReceive(context, intent);
		verify(languageTranslator, times(0)).updateDownloadNotificationsConnectionAvailable(true);
	}

	@Test
	public void hasNoConnectionTest() {
		when(languageTranslator.hasConnection(context)).thenReturn(false);
		doNothing().when(languageTranslator).updateDownloadNotificationsConnectionAvailable(false);

		assertTrue(connectivityChangeBroadcastReceiver.getHasConnection());
		connectivityChangeBroadcastReceiver.onReceive(context, intent);

		assertFalse(connectivityChangeBroadcastReceiver.getHasConnection());
		verify(languageTranslator, times(1)).updateDownloadNotificationsConnectionAvailable(false);
	}

	@Test
	public void connectionChangeTest() {
		doNothing().when(languageTranslator).updateDownloadNotificationsConnectionAvailable(false);

		when(languageTranslator.hasConnection(context)).thenReturn(true);
		connectivityChangeBroadcastReceiver.onReceive(context, intent);
		assertTrue(connectivityChangeBroadcastReceiver.getHasConnection());

		when(languageTranslator.hasConnection(context)).thenReturn(false);
		assertTrue(connectivityChangeBroadcastReceiver.getHasConnection());
		connectivityChangeBroadcastReceiver.onReceive(context, intent);

		assertFalse(connectivityChangeBroadcastReceiver.getHasConnection());
		verify(languageTranslator, times(1)).updateDownloadNotificationsConnectionAvailable(false);
	}
}
