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
package org.catrobat.catroid.test.content.bricks

import android.content.Context
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.Arrays
import java.util.stream.Collectors

@RunWith(Parameterized::class)
class BroadcastMessageBrickTest {
    private var context: Context? = null
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var messages: List<String>? = null
    @JvmField
    @Parameterized.Parameter(2)
    var expectedOutput: List<String>? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        context = Mockito.mock(Context::class.java)
        Mockito.`when`(context!!.getString(ArgumentMatchers.eq(R.string
                                                                  .brick_broadcast_default_value)))
            .thenReturn(
                defaultValueString
            )
        Mockito.`when`(context!!.getString(ArgumentMatchers.eq(R.string.new_option))).thenReturn(
            newOptionString
        )
        Mockito.`when`(context!!.getString(ArgumentMatchers.eq(R.string.edit_option))).thenReturn(
            editOptionString
        )
    }

    @Test
    fun testGetSortedItemListFromMessages() {
        val output = BroadcastMessageBrick.getSortedItemListFromMessages(context, messages)
        val outputStrings = output.stream().map { obj: Nameable -> obj.name }
            .collect(Collectors.toList())
        Assert.assertThat(outputStrings, CoreMatchers.equalTo(expectedOutput))
    }

    companion object {
        private const val defaultValueString = "defaultString"
        private const val newOptionString = "new..."
        private const val editOptionString = "edit..."
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "MultipleCharsWithDifferentCase",
                        Arrays.asList("a", "R", "x"),
                        Arrays.asList(
                            newOptionString, editOptionString, "a", "R", "x"
                        )
                    ), arrayOf(
                        "MultipleNumbers", Arrays.asList("50", "3", "12"), Arrays.asList(
                            newOptionString, editOptionString, "12", "3", "50"
                        )
                    ), arrayOf(
                        "WithSpecialCharacters",
                        Arrays.asList(".", "a", ":", "_b", "c"),
                        Arrays.asList(
                            newOptionString, editOptionString, ".", ":", "_b", "a", "c"
                        )
                    ), arrayOf(
                        "NoMessage", ArrayList<Any>(), listOf(
                            defaultValueString
                        )
                    )
                )
            )
        }
    }
}
