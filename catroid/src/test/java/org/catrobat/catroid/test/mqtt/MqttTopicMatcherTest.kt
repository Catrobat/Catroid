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

package org.catrobat.catroid.test.mqtt

import org.catrobat.catroid.devices.mqtt.MqttTopicMatcher.containsWildcard
import org.catrobat.catroid.devices.mqtt.MqttTopicMatcher.matches
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MqttTopicMatcherTest {

    @Test
    fun testIdenticalTopicMatches() {
        assertTrue(matches("sport/tennis/player1", "sport/tennis/player1"))
    }

    @Test
    fun testDifferentTopicDoesNotMatch() {
        assertFalse(matches("sport/tennis/player1", "sport/tennis/player2"))
    }

    @Test
    fun testShorterTopicDoesNotMatchLongerFilter() {
        assertFalse(matches("sport/tennis/player1", "sport/tennis"))
    }

    @Test
    fun testLongerTopicDoesNotMatchShorterFilter() {
        assertFalse(matches("sport/tennis", "sport/tennis/player1"))
    }

    @Test
    fun testSingleLevelWildcardMatchesOneLevel() {
        assertTrue(matches("sport/tennis/+", "sport/tennis/player1"))
    }

    @Test
    fun testSingleLevelWildcardDoesNotMatchMultipleLevels() {
        assertFalse(matches("sport/tennis/+", "sport/tennis/player1/ranking"))
    }

    @Test
    fun testSingleLevelWildcardDoesNotMatchParentLevel() {
        assertFalse(matches("sport/+", "sport"))
    }

    @Test
    fun testSingleLevelWildcardMatchesEmptyLevel() {
        assertTrue(matches("sport/+", "sport/"))
    }

    @Test
    fun testSingleLevelWildcardInMiddleOfFilter() {
        assertTrue(matches("sport/+/player1", "sport/tennis/player1"))
    }

    @Test
    fun testMultipleSingleLevelWildcards() {
        assertTrue(matches("+/+/player1", "sport/tennis/player1"))
    }

    @Test
    fun testStandaloneSingleLevelWildcardMatchesSingleLevelTopic() {
        assertTrue(matches("+", "sport"))
    }

    @Test
    fun testStandaloneSingleLevelWildcardDoesNotMatchNestedTopic() {
        assertFalse(matches("+", "sport/tennis"))
    }

    @Test
    fun testMultiLevelWildcardMatchesRemainingLevels() {
        assertTrue(matches("sport/tennis/#", "sport/tennis/player1/ranking"))
    }

    @Test
    fun testMultiLevelWildcardMatchesSingleRemainingLevel() {
        assertTrue(matches("sport/#", "sport/tennis"))
    }

    @Test
    fun testMultiLevelWildcardMatchesParentLevelItself() {
        assertTrue(matches("sport/#", "sport"))
    }

    @Test
    fun testStandaloneMultiLevelWildcardMatchesEverything() {
        assertTrue(matches("#", "sport/tennis/player1"))
    }

    @Test
    fun testMultiLevelWildcardDoesNotMatchSiblingBranch() {
        assertFalse(matches("sport/tennis/#", "sport/football/player1"))
    }

    @Test
    fun testMultiLevelWildcardMustBeLastLevel() {
        assertFalse(matches("sport/#/ranking", "sport/tennis/ranking"))
    }

    @Test
    fun testMultiLevelWildcardDoesNotMatchReservedTopic() {
        assertFalse(matches("#", "\$SYS/broker/uptime"))
    }

    @Test
    fun testSingleLevelWildcardDoesNotMatchReservedTopic() {
        assertFalse(matches("+/broker/uptime", "\$SYS/broker/uptime"))
    }

    @Test
    fun testExplicitReservedPrefixMatchesReservedTopic() {
        assertTrue(matches("\$SYS/#", "\$SYS/broker/uptime"))
    }

    @Test
    fun testWildcardMatchesNonReservedTopicContainingDollarLater() {
        assertTrue(matches("sport/+", "sport/\$special"))
    }

    @Test
    fun testContainsWildcardDetectsSingleLevel() {
        assertTrue(containsWildcard("sport/+/player1"))
    }

    @Test
    fun testContainsWildcardDetectsMultiLevel() {
        assertTrue(containsWildcard("sport/#"))
    }

    @Test
    fun testContainsWildcardIsFalseForExactTopic() {
        assertFalse(containsWildcard("sport/tennis/player1"))
    }

    @Test
    fun testEmptyFilterDoesNotMatchTopic() {
        assertFalse(matches("", "sport"))
    }

    @Test
    fun testFilterDoesNotMatchEmptyTopic() {
        assertFalse(matches("sport", ""))
    }

    @Test
    fun testEmptyFilterDoesNotMatchEmptyTopic() {
        assertFalse(matches("", ""))
    }
}
