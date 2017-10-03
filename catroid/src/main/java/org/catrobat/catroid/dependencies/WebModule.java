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

package org.catrobat.catroid.dependencies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.catrobat.catroid.common.Constants.BASE_URL_HTTPS;
import static org.catrobat.catroid.web.ServerCalls.BASE_URL_TEST_HTTPS;
import static org.catrobat.catroid.web.ServerCalls.useTestUrl;

@Module
public class WebModule {

	@Provides
	@Singleton
	public Gson provideGson() {
		return new GsonBuilder()
				.setPrettyPrinting()
				.create();
	}

	@Provides
	@Singleton
	public OkHttpClient provideOkHttpClient() {
		return new OkHttpClient.Builder()
				.connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS))
				.build();
	}

	@Provides
	@Singleton
	public Retrofit provideRetrofitClient(Gson gson, OkHttpClient okHttpClient) {
		return new Retrofit.Builder()
				.baseUrl(useTestUrl ? BASE_URL_TEST_HTTPS : BASE_URL_HTTPS)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(okHttpClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build();
	}
}
