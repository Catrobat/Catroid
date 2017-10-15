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

package org.catrobat.catroid.uiespresso.util.mocks;

import android.support.test.InstrumentationRegistry;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import org.catrobat.catroid.common.TemplateContainer;
import org.catrobat.catroid.web.FetchTemplatesRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import retrofit2.http.Path;

public class MockFetchTemplatesRequest implements FetchTemplatesRequest {

	@Override
	public Observable<TemplateContainer> fetchTemplates(@Path("name") String name) {
		String json = "";
		try {
			InputStream inputStream = InstrumentationRegistry.getContext().getAssets().open("template_list.json");
			json = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
		} catch (IOException e) {
			return null;
		}
		TemplateContainer container = new Gson().fromJson(json, TemplateContainer.class);
		return Observable.just(container);
	}
}
