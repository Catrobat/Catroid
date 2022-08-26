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

package org.catrobat.catroid.test.web;

import org.catrobat.catroid.web.WebconnectionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.catrobat.catroid.web.CatrobatWebClientKt.performCallWith;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Request.class, Response.class, ResponseBody.class})
public class CatrobatWebClientTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Request requestMock;
	private OkHttpClient clientMock;
	private Call call;

	@Before
	public void setUp() {
		requestMock = PowerMockito.mock(Request.class);
		clientMock = Mockito.mock(OkHttpClient.class);
		call = Mockito.mock(Call.class);
		Mockito.when(clientMock.newCall(requestMock)).thenReturn(call);
	}

	@Test
	public void testThrowsExceptionWhenConnectionFails() throws WebconnectionException, IOException {
		Mockito.when(call.execute()).thenThrow(new IOException());
		exception.expect(WebconnectionException.class);

		performCallWith(clientMock, requestMock);
	}

	@Test
	public void testThrowsExceptionWhenResponseBodyIsNull() throws WebconnectionException, IOException {
		Response response = PowerMockito.mock(Response.class);
		Mockito.when(response.message()).thenReturn("");
		Mockito.when(call.execute()).thenReturn(response);

		exception.expect(WebconnectionException.class);
		performCallWith(clientMock, requestMock);
	}

	@Test
	public void testThrowsExceptionWhenResponseBodyIsInvalid() throws WebconnectionException, IOException {
		Response response = PowerMockito.mock(Response.class);
		Mockito.when(response.message()).thenReturn("");
		Mockito.when(call.execute()).thenReturn(response);

		ResponseBody body = PowerMockito.mock(ResponseBody.class);
		Mockito.when(response.body()).thenReturn(body);

		PowerMockito.when(body.string()).thenThrow(new IOException());

		exception.expect(WebconnectionException.class);

		performCallWith(clientMock, requestMock);
	}

	@Test
	public void testValidRun() throws WebconnectionException, IOException {
		Response response = PowerMockito.mock(Response.class);
		Mockito.when(response.isSuccessful()).thenReturn(true);
		Mockito.when(call.execute()).thenReturn(response);

		ResponseBody body = PowerMockito.mock(ResponseBody.class);
		Mockito.when(response.body()).thenReturn(body);

		PowerMockito.when(body.string()).thenReturn("valid");

		performCallWith(clientMock, requestMock);
		Mockito.verify(clientMock, Mockito.times(1)).newCall(requestMock);
	}
}
