/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.test.web;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.web.WebConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static junit.framework.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class WebConnectionTest {

	private static final String BASE_URL_TEST_HTTPS = "https://catroid-test.catrob.at/pocketcode/";

	@Test
	public void testSendRequestWithMalformedUrl() {
		WebConnection webConnection = new WebConnection(null);
		WebConnection.WebRequestListener listener = Mockito.mock(WebConnection.WebRequestListener.class);
		webConnection.setListener(listener);
		webConnection.setUrl("test");

		doAnswer(invocation -> {
			assertEquals(invocation.getArgument(0), Integer.toString(Constants.ERROR_BAD_REQUEST));
			return null;
		}).when(listener).onRequestFinished(anyString());

		webConnection.sendWebRequest();

		verify(listener, times(1)).onRequestFinished(anyString());
	}

	@Test
	public void testSendRequest() {
		OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
		WebConnection webConnection = new WebConnection(okHttpClient);
		Call call = Mockito.mock(Call.class);
		when(okHttpClient.newCall(any(Request.class))).thenReturn(call);
		webConnection.setUrl(BASE_URL_TEST_HTTPS);

		webConnection.sendWebRequest();

		verify(call, times(1)).enqueue(any());
	}
}
