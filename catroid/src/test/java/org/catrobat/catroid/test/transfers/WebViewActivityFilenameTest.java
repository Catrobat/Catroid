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

package org.catrobat.catroid.test.transfers;

import org.catrobat.catroid.ui.WebViewActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(RobolectricTestRunner.class)
public class WebViewActivityFilenameTest {

	private static final String URL = "https://share.catrobat.org/api/media/assets/abc/download";

	@Test
	public void testEncodedFilenameIsDecoded() {
		assertEquals("Tür.png", filename("attachment; filename*=utf-8''T%C3%BCr.png"));
	}

	@Test
	public void testQuotedFilenameIsUsed() {
		assertEquals("cat.png", filename("attachment; filename=\"cat.png\""));
	}

	@Test
	public void testEncodedPathIsReducedToItsName() {
		assertEquals("code.xml", filename("attachment; filename*=utf-8''..%2F..%2Ffiles%2FMyGame%2Fcode.xml"));
	}

	@Test
	public void testQuotedPathIsReducedToItsName() {
		assertEquals("evil.png", filename("attachment; filename=\"../../evil.png\""));
	}

	@Test
	public void testParentDirectoryNameFallsBackToTheUrl() {
		String name = filename("attachment; filename*=utf-8''..");
		assertFalse(name, name.equals("..") || name.contains("/"));
	}

	private static String filename(String contentDisposition) {
		return WebViewActivity.getFilenameFromContentDisposition(contentDisposition, URL, null);
	}
}
