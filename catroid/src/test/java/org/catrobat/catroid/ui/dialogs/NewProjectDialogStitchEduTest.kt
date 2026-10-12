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
package org.catrobat.catroid.ui.dialogs

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.web.WebpageUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.times
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class NewProjectDialogStitchEduTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun testStitchEduUrlConstant() {
        assertEquals("https://stitchedu.com/", Constants.STITCH_EDU_URL)
    }

    @Test
    fun testIsStitchEduSupportedMatchesFlavor() {
        val expected = FlavoredConstants.FLAVOR_NAME == "embroidery"
        assertEquals(expected, StitchEduHelper.isStitchEduSupported)
    }

    @Test
    fun testOpenStitchEduLaunchesUrlSuccessfully() {
        mockStatic(WebpageUtils::class.java).use { webpageUtilsMock ->
            val result = StitchEduHelper.openStitchEdu(context)

            assertTrue(result)
            webpageUtilsMock.verify(
                { WebpageUtils.openWebPage(context, Constants.STITCH_EDU_URL) },
                times(1)
            )
        }
    }

    @Test
    fun testOpenStitchEduHandlesActivityNotFoundException() {
        mockStatic(WebpageUtils::class.java).use { webpageUtilsMock ->
            mockStatic(ToastUtil::class.java).use { toastUtilMock ->
                webpageUtilsMock.`when`<Unit> {
                    WebpageUtils.openWebPage(context, Constants.STITCH_EDU_URL)
                }.thenAnswer {
                    throw ActivityNotFoundException("No browser")
                }

                val result = StitchEduHelper.openStitchEdu(context)

                assertFalse(result)
                toastUtilMock.verify(
                    { ToastUtil.showError(context, R.string.error_no_web_browser_found) },
                    times(1)
                )
            }
        }
    }

    @Test
    fun testStitchEduVisibilityAndUIFlowInDialog() {
        val activity = Robolectric.buildActivity(AppCompatActivity::class.java).setup().get()
        val dialog = NewProjectDialogFragment()
        dialog.show(activity.supportFragmentManager, NewProjectDialogFragment.TAG)
        ShadowLooper.shadowMainLooper().idle()

        val view = dialog.requireView()
        val stitchEduRadio = view.findViewById<RadioButton>(R.id.stitchedu_radio_button)
        val portraitRadio = view.findViewById<RadioButton>(R.id.portrait_radio_button)
        val stitchEduDescription = view.findViewById<TextView>(R.id.stitchedu_description)
        val input = view.findViewById<TextInputLayout>(R.id.input)
        val exampleSwitch = view.findViewById<SwitchMaterial>(R.id.example_project_switch)

        if (StitchEduHelper.isStitchEduSupported) {
            assertEquals(View.VISIBLE, stitchEduRadio.visibility)
            assertEquals(
                activity.getString(R.string.new_project_dialog_stitchedu),
                stitchEduRadio.text.toString()
            )

            // Default selection is portrait
            assertTrue(portraitRadio.isChecked)
            assertEquals(View.GONE, stitchEduDescription.visibility)
            assertEquals(View.VISIBLE, input.visibility)
            assertEquals(View.VISIBLE, exampleSwitch.visibility)

            // Select StitchEDU
            stitchEduRadio.isChecked = true
            ShadowLooper.shadowMainLooper().idle()

            assertEquals(View.VISIBLE, stitchEduDescription.visibility)
            assertEquals(View.GONE, input.visibility)
            assertEquals(View.GONE, exampleSwitch.visibility)

            // Select back to portrait
            portraitRadio.isChecked = true
            ShadowLooper.shadowMainLooper().idle()

            assertEquals(View.GONE, stitchEduDescription.visibility)
            assertEquals(View.VISIBLE, input.visibility)
            assertEquals(View.VISIBLE, exampleSwitch.visibility)
        } else {
            assertEquals(View.GONE, stitchEduRadio.visibility)
        }
    }

    @Test
    fun testCreateProjectWhenStitchEduSelectedOpensStitchEdu() {
        if (!StitchEduHelper.isStitchEduSupported) {
            return
        }

        val activity = Robolectric.buildActivity(AppCompatActivity::class.java).setup().get()
        val dialog = NewProjectDialogFragment()
        dialog.show(activity.supportFragmentManager, NewProjectDialogFragment.TAG)
        ShadowLooper.shadowMainLooper().idle()

        val stitchEduRadio =
            dialog.requireView().findViewById<RadioButton>(R.id.stitchedu_radio_button)
        stitchEduRadio.isChecked = true
        ShadowLooper.shadowMainLooper().idle()

        mockStatic(WebpageUtils::class.java).use { webpageUtilsMock ->
            dialog.createProject()

            webpageUtilsMock.verify(
                { WebpageUtils.openWebPage(dialog.requireContext(), Constants.STITCH_EDU_URL) },
                times(1)
            )
        }
    }
}
