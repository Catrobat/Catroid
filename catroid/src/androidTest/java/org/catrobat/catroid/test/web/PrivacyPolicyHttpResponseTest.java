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

import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.web.WebConnection;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static org.catrobat.catroid.common.FlavoredConstants.PRIVACY_POLICY_URL;
import static org.junit.Assert.assertTrue;

@Category(Cat.OutgoingNetworkTests.class)
@RunWith(AndroidJUnit4.class)
public class PrivacyPolicyHttpResponseTest implements WebConnection.WebRequestListener {

	private static final String HTML_RESPONSE_START = "<!doctype html>";
	private static final String HTML_RESPONSE_END = "</html>";
	OkHttpClient okHttpClient = new OkHttpClient();
	private CompletableFuture<String> response;

	@Before
	public void setUp() {
		response = new CompletableFuture();
	}

	@Test
	public void testHttps() {
		new WebConnection(okHttpClient, this, PRIVACY_POLICY_URL).sendWebRequest();
		String body;
		try {
			body = response.get().trim();
		} catch (ExecutionException | InterruptedException e) {
			body = "";
		}
		assertTrue(body.startsWith(HTML_RESPONSE_START));
		assertTrue(body.endsWith(HTML_RESPONSE_END));
	}

	@Override
	public void onRequestSuccess(@NotNull Response httpResponse) {
		try {
			response.complete(httpResponse.body().string());
		} catch (IOException e) {
			response.complete("");
		}
	}

	@Override
	public void onRequestError(@NotNull String httpError) {
		response.complete(httpError);
	}

	@Override
	public void onCancelledCall() {
		response.complete("");
	}
}
