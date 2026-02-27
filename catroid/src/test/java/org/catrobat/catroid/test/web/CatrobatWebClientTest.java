/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.web.WebConnectionException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.catrobat.catroid.web.CatrobatWebClientKt.performCallWith;

@RunWith(MockitoJUnitRunner.class)
public class CatrobatWebClientTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private Request requestMock;
	private OkHttpClient clientMock;
	private Call call;

	@Before
	public void setUp() {
		requestMock = Mockito.mock(Request.class);
		clientMock = Mockito.mock(OkHttpClient.class);
		call = Mockito.mock(Call.class);
		Mockito.when(clientMock.newCall(requestMock)).thenReturn(call);
	}

	@Test
	public void testThrowsExceptionWhenConnectionFails() throws WebConnectionException, IOException {
		Mockito.when(call.execute()).thenThrow(new IOException());
		exception.expect(WebConnectionException.class);

		performCallWith(clientMock, requestMock);
	}

	@Test
	public void testThrowsExceptionWhenResponseBodyIsNull() throws WebConnectionException, IOException {
		Response response = Mockito.mock(Response.class);
		Mockito.when(response.message()).thenReturn("");
		Mockito.when(call.execute()).thenReturn(response);

		exception.expect(WebConnectionException.class);
		performCallWith(clientMock, requestMock);
	}

	@Test
	public void testThrowsExceptionWhenResponseBodyIsInvalid() throws WebConnectionException, IOException {
		Response response = Mockito.mock(Response.class);
//		Mockito.when(response.message()).thenReturn("");
		Mockito.when(call.execute()).thenReturn(response);

		ResponseBody body = Mockito.mock(ResponseBody.class);
		Mockito.when(response.body()).thenReturn(body);

		Mockito.when(body.string()).thenThrow(new IOException());

		exception.expect(WebConnectionException.class);

		performCallWith(clientMock, requestMock);
	}

	@Test
	public void testValidRun() throws WebConnectionException, IOException {
		Response response = Mockito.mock(Response.class);
//		Mockito.when(response.isSuccessful()).thenReturn(true);
		Mockito.when(call.execute()).thenReturn(response);

		ResponseBody body = Mockito.mock(ResponseBody.class);
		Mockito.when(response.body()).thenReturn(body);

		Mockito.when(body.string()).thenReturn("valid");

		performCallWith(clientMock, requestMock);
		Mockito.verify(clientMock, Mockito.times(1)).newCall(requestMock);
	}
}
