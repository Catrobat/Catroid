/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.test.ui.regexassistant

import android.app.Activity
import org.catrobat.catroid.R
import org.catrobat.catroid.utils.HtmlRegexExtractor
import org.catrobat.catroid.utils.ToastUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.times
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.api.mockito.PowerMockito.verifyStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(ToastUtil::class)
class HtmlExtractorErrorHandlingTest {
    private lateinit var htmlExtractor: HtmlRegexExtractor
    private lateinit var context: Activity

    @Before
    fun setUp() {
        context = Activity()
        htmlExtractor = HtmlRegexExtractor(context)
    }

    @Test
    fun testHtmlExtractorDialogNotFoundMessage() {
        mockStatic(ToastUtil::class.java)
        htmlExtractor.searchKeyword("Not", "Found")
        verifyStatic(ToastUtil::class.java, times(1))
        ToastUtil.showError(
            eq(context),
            eq(R.string.formula_editor_function_regex_html_extractor_not_found)
        )
    }
}
