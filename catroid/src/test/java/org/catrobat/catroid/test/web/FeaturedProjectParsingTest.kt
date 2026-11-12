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

package org.catrobat.catroid.test.web

import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.models.FeaturedProjectApi
import org.catrobat.catroid.retrofit.models.toRoomEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FeaturedProjectParsingTest {

    private val adapter = CatroidWebServer.moshi.adapter(FeaturedProjectApi::class.java)

    @Test
    fun `a featured project opens its project page`() {
        val project = adapter.fromJson(
            """{"id":"1","project_id":"p1","project_url":"https://share.catrobat.org/app/project/p1",
               "url":"https://share.catrobat.org/app/project/p1","name":"Game","author":"kid"}"""
        )!!.toRoomEntity()

        assertEquals("https://share.catrobat.org/app/project/p1", project.projectUrl)
    }

    @Test
    fun `a featured link without a project parses and opens the link`() {
        val link = adapter.fromJson(
            """{"id":"2","project_id":"","project_url":null,"url":"https://catrobat.org/contest",
               "name":"","author":""}"""
        )!!.toRoomEntity()

        assertEquals("https://catrobat.org/contest", link.projectUrl)
    }

    @Test
    fun `a featured link whose project_url is left out parses too`() {
        val link = adapter.fromJson(
            """{"id":"3","project_id":"","url":"https://catrobat.org/contest","name":"","author":""}"""
        )!!.toRoomEntity()

        assertEquals("https://catrobat.org/contest", link.projectUrl)
    }
}
