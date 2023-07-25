/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor.sensor

import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorDateDayTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorDateMonthTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorDateWeekdayTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorDateYearTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorTimeHourTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorTimeMinuteTest
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorTimeSecondTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Calendar

@RunWith(Parameterized::class)
class SensorCalendarSensorsTest(
    private val name: String,
    private val sensor: Sensor,
    private val calendarReturnValue: Int,
    private val expectedValue: Int
) {

    @Before
    fun setup() {
        when (sensor) {
            is SensorTimeHourTest -> sensor.setHour(calendarReturnValue)
            is SensorTimeMinuteTest -> sensor.setMinute(calendarReturnValue)
            is SensorTimeSecondTest -> sensor.setSecond(calendarReturnValue)
            is SensorDateDayTest -> sensor.setDay(calendarReturnValue)
            is SensorDateMonthTest -> sensor.setMonth(calendarReturnValue)
            is SensorDateYearTest -> sensor.setYear(calendarReturnValue)
            is SensorDateWeekdayTest -> sensor.setWeekday(calendarReturnValue)
        }
    }

    @Test
    fun calendarSensorsTest() {
        compareToSensor(expectedValue, sensor)
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01

        lateinit var mockCalendar: Calendar

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("timeHour1", SensorTimeHourTest.getInstance(), 10, 10),
            arrayOf("timeHour2", SensorTimeHourTest.getInstance(), 17, 17),
            arrayOf("timeMinute1", SensorTimeMinuteTest.getInstance(), 34, 34),
            arrayOf("timeMinute2", SensorTimeMinuteTest.getInstance(), 21, 21),
            arrayOf("timeSecond1", SensorTimeSecondTest.getInstance(), 45, 45),
            arrayOf("timeSecond2", SensorTimeSecondTest.getInstance(), 2, 2),
            arrayOf("dateDay1", SensorDateDayTest.getInstance(), 11, 11),
            arrayOf("dateDay2", SensorDateDayTest.getInstance(), 12, 12),
            arrayOf("dateMonthJan", SensorDateMonthTest.getInstance(), Calendar.JANUARY, 1),
            arrayOf("dateMonthFeb", SensorDateMonthTest.getInstance(), Calendar.FEBRUARY, 2),
            arrayOf("dateMonthMar", SensorDateMonthTest.getInstance(), Calendar.MARCH, 3),
            arrayOf("dateMonthApr", SensorDateMonthTest.getInstance(), Calendar.APRIL, 4),
            arrayOf("dateMonthMay", SensorDateMonthTest.getInstance(), Calendar.MAY, 5),
            arrayOf("dateMonthJun", SensorDateMonthTest.getInstance(), Calendar.JUNE, 6),
            arrayOf("dateMonthJul", SensorDateMonthTest.getInstance(), Calendar.JULY, 7),
            arrayOf("dateMonthAug", SensorDateMonthTest.getInstance(), Calendar.AUGUST, 8),
            arrayOf("dateMonthSep", SensorDateMonthTest.getInstance(), Calendar.SEPTEMBER, 9),
            arrayOf("dateMonthOct", SensorDateMonthTest.getInstance(), Calendar.OCTOBER, 10),
            arrayOf("dateMonthNov", SensorDateMonthTest.getInstance(), Calendar.NOVEMBER, 11),
            arrayOf("dateMonthDec", SensorDateMonthTest.getInstance(), Calendar.DECEMBER, 12),
            arrayOf("dateYear", SensorDateYearTest.getInstance(), 1066, 1066),
            arrayOf("dateWeekDayMon", SensorDateWeekdayTest.getInstance(), Calendar.MONDAY, 1),
            arrayOf("dateWeekDayTue", SensorDateWeekdayTest.getInstance(), Calendar.TUESDAY, 2),
            arrayOf("dateWeekDayWed", SensorDateWeekdayTest.getInstance(), Calendar.WEDNESDAY, 3),
            arrayOf("dateWeekDayThu", SensorDateWeekdayTest.getInstance(), Calendar.THURSDAY, 4),
            arrayOf("dateWeekDayFri", SensorDateWeekdayTest.getInstance(), Calendar.FRIDAY, 5),
            arrayOf("dateWeekDaySat", SensorDateWeekdayTest.getInstance(), Calendar.SATURDAY, 6),
            arrayOf("dateWeekDaySun", SensorDateWeekdayTest.getInstance(), Calendar.SUNDAY, 7),
        )
    }
}
