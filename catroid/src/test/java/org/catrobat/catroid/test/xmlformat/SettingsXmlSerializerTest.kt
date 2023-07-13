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
package org.catrobat.catroid.test.xmlformat

import org.catrobat.catroid.content.Setting
import org.catrobat.catroid.io.XstreamSerializer
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.api.mockito.PowerMockito
import java.io.Serializable
import java.util.ArrayList

@RunWith(Parameterized::class)
class SettingsXmlSerializerTest(
    private val name: String,
    private val componentClass: Class<Serializable>
) {
    @Test
    @kotlin.jvm.Throws(IllegalAccessException::class, InstantiationException::class)
    fun testSettingAlias() {
        if (isComponentNoSetting(componentClass)) {
            return
        }
        val xml = mockAndSerialize(componentClass)
        Assert.assertThat(
            xml,
            Matchers.startsWith("<setting type=\"${componentClass.simpleName}\"/>")
        )
    }

    @Test
    @kotlin.jvm.Throws(InstantiationException::class, IllegalAccessException::class)
    fun testMissingAliasInComponent() {
        val xml = mockAndSerialize(componentClass)
        Assert.assertThat(xml, Matchers.not(Matchers.containsString("org.catrobat.catroid")))
    }

    private fun mockAndSerialize(componentClass: Class<Serializable>): String {
        val component = PowerMockito.mock<Serializable>(componentClass)
        return XstreamSerializer.getInstance().xstream.toXML(component)
    }

    private fun isComponentNoSetting(componentClass: Class<Serializable>): Boolean {
        val settingsClasses = ClassDiscoverer.getAllSubClassesOf(Setting::class.java) ?: return true
        return !settingsClasses.contains<Serializable>(componentClass)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters(): Iterable<Array<Any>> {
            val parameters: MutableList<Array<Any>> = ArrayList()

            ClassDiscoverer.getAllSubClassesOf(Setting::class.java)?.forEach { settingClassesElement ->
                parameters.add(arrayOf(settingClassesElement.name, settingClassesElement))
                val portClasses = settingClassesElement.declaredClasses
                portClasses.forEach { portClass ->
                    parameters.add(arrayOf(portClass.name, portClass))
                }
            }
            return parameters
        }
    }
}
