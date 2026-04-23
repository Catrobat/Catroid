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

package org.catrobat.catroid.uiespresso.util.matchers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class BundleMatchers {
	public static final String TAG = BundleMatchers.class.getSimpleName();

	private BundleMatchers() {
		throw new AssertionError();
	}

	public static Matcher<Bundle> bundleHasMatchingString(final String key, final String value) {
		return new TypeSafeMatcher<Bundle>() {
			@Override
			public boolean matchesSafely(final Bundle bundle) {
				return bundle.containsKey(key)
						&& bundle.getString(key).equals(value);
			}
			@Override
			public void describeTo(Description description) {
				description.appendText("expected Bundle with key value pair: " + key + " value: " + value);
			}
		};
	}

	public static Matcher<Bundle> bundleContainsMediaURI(final Uri uri) {
		return new TypeSafeMatcher<Bundle>() {
			@Override
			public boolean matchesSafely(final Bundle bundle) {
				return bundle.containsKey(MediaStore.EXTRA_OUTPUT)
						&& bundle.getString(MediaStore.EXTRA_OUTPUT).equals(uri);
			}
			@Override
			public void describeTo(Description description) {
				description.appendText("expected Bundle with URI: " + uri.toString());
			}
		};
	}

	public static Matcher<Bundle> bundleHasExtraIntent(final Matcher<Intent> intentMatcher) {
		return new TypeSafeMatcher<Bundle>() {
			@Override
			public boolean matchesSafely(final Bundle bundle) {
				return bundle.containsKey(Intent.EXTRA_INTENT)
						&& bundle.getParcelable(Intent.EXTRA_INTENT) instanceof Intent
						&& intentMatcher.matches(bundle.getParcelable(Intent.EXTRA_INTENT));
			}
			@Override
			public void describeTo(Description description) {
				description.appendText("expected Bundle containing Intent");
			}
		};
	}

	public static Matcher<Bundle> debugListBundleContents() {
		return new TypeSafeMatcher<Bundle>() {
			@Override
			public boolean matchesSafely(final Bundle bundle) {
				for (String key : bundle.keySet()) {
					Log.d(TAG, "key = " + key);
					Log.d(TAG, bundle.get(key).toString());
				}
				return true;
			}
			@Override
			public void describeTo(Description description) {
				description.appendText("debug matcher, will always match and log bundle content");
			}
		};
	}
}
