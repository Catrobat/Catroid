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

package org.catrobat.catroid.test.content

import org.catrobat.catroid.content.XmlHeader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for the [XmlHeader.getProjectUuid] lazy-generation behavior.
 *
 * The projectUuid field was added as part of the pin-to-home-screen feature
 * for future-proofing shortcut identity across renames. These tests verify
 * the lazy generation contract and backward compatibility with older projects.
 */
class XmlHeaderProjectUuidTest {

    @Test
    fun `getProjectUuid generates UUID when field is null`() {
        val header = XmlHeader()
        // Field starts as null (no value in code.xml)
        val uuid = header.projectUuid
        assertNotNull("UUID should be generated when field is null", uuid)
        assertTrue(
            "Generated value should be a valid UUID format",
            uuid.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
        )
    }

    @Test
    fun `getProjectUuid returns same UUID on repeated calls`() {
        val header = XmlHeader()
        val first = header.projectUuid
        val second = header.projectUuid
        assertEquals(
            "Repeated calls should return the same UUID (stable once generated)",
            first,
            second
        )
    }

    @Test
    fun `getProjectUuid generates new UUID for blank field`() {
        val header = XmlHeader()
        header.projectUuid = ""
        val uuid = header.projectUuid
        assertNotNull("UUID should be generated when field is blank", uuid)
        assertFalse("Generated UUID should not be blank", uuid.isBlank())
        assertTrue(
            "Generated value should be a valid UUID format",
            uuid.matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
        )
    }

    @Test
    fun `existing project without projectUuid gets one assigned on load`() {
        // Simulates an older project's XmlHeader deserialized without projectUuid
        val header = XmlHeader()
        // projectUuid is null by default (simulating old code.xml without this field)
        val uuid = header.projectUuid

        assertNotNull(
            "Older projects without projectUuid should get one assigned on first access",
            uuid
        )
        assertFalse(
            "Assigned UUID should not be blank",
            uuid.isBlank()
        )

        // Verify the UUID persists (would be saved on next code.xml write)
        val uuidAgain = header.projectUuid
        assertEquals(
            "UUID should remain stable after first assignment",
            uuid,
            uuidAgain
        )
    }
}
