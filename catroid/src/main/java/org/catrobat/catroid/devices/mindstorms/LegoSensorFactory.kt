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

package org.catrobat.catroid.devices.mindstorms

import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3ColorSensor
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3InfraredSensor
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3LightSensorNXT
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SensorMode
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3SoundSensorNXT
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3TouchSensor
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3UltrasonicSensorNXT
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.HiTechnicColorSensor
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.TemperatureSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensorActive
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor

class LegoSensorFactory(private val connection: MindstormsConnection) {
    fun create(sensorType: Enum<*>, port: Int): LegoSensor = when (sensorType) {
        is EV3Sensor.Sensor -> createEv3Sensor(sensorType, port)
        is NXTSensor.Sensor -> createNxtSensor(sensorType, port)
        else -> throw IllegalArgumentException("Trying to create LegoSensor with invalid sensorType: " + sensorType)
    }

    private fun createEv3Sensor(sensorType: EV3Sensor.Sensor, port: Int): LegoSensor = when (sensorType) {
        EV3Sensor.Sensor.INFRARED -> EV3InfraredSensor(port, connection)
        EV3Sensor.Sensor.TOUCH -> EV3TouchSensor(port, connection)
        EV3Sensor.Sensor.COLOR -> EV3ColorSensor(port, connection, EV3SensorMode.MODE2)
        EV3Sensor.Sensor.COLOR_AMBIENT -> EV3ColorSensor(port, connection, EV3SensorMode.MODE0)
        EV3Sensor.Sensor.COLOR_REFLECT -> EV3ColorSensor(port, connection, EV3SensorMode.MODE1)
        EV3Sensor.Sensor.HT_NXT_COLOR -> HiTechnicColorSensor(port, connection, EV3SensorMode.MODE1)
        EV3Sensor.Sensor.NXT_TEMPERATURE_C -> TemperatureSensor(port, connection, EV3SensorMode.MODE0)
        EV3Sensor.Sensor.NXT_TEMPERATURE_F -> TemperatureSensor(port, connection, EV3SensorMode.MODE1)
        EV3Sensor.Sensor.NXT_LIGHT -> EV3LightSensorNXT(port, connection, EV3SensorMode.MODE1)
        EV3Sensor.Sensor.NXT_LIGHT_ACTIVE -> EV3LightSensorNXT(port, connection, EV3SensorMode.MODE0)
        EV3Sensor.Sensor.NXT_SOUND -> EV3SoundSensorNXT(port, connection, EV3SensorMode.MODE1)
        EV3Sensor.Sensor.NXT_ULTRASONIC -> EV3UltrasonicSensorNXT(port, connection, EV3SensorMode.MODE0)
        else -> throw IllegalArgumentException("Trying to create Ev3Sensor with invalid sensorType: " + sensorType)
    }

    private fun createNxtSensor(sensorType: NXTSensor.Sensor, port: Int): LegoSensor = when (sensorType) {
        NXTSensor.Sensor.TOUCH -> NXTTouchSensor(port, connection)
        NXTSensor.Sensor.SOUND -> NXTSoundSensor(port, connection)
        NXTSensor.Sensor.LIGHT_INACTIVE -> NXTLightSensor(port, connection)
        NXTSensor.Sensor.LIGHT_ACTIVE -> NXTLightSensorActive(port, connection)
        NXTSensor.Sensor.ULTRASONIC -> NXTI2CUltraSonicSensor(connection)
        else -> throw IllegalArgumentException("Trying to create NxtSensor with invalid sensorType: " + sensorType)
    }
}
