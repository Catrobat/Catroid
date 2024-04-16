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

package org.catrobat.catroid.test.io.catrobatlanguage.parser

import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageProjectSerializer
import org.junit.Assert
import org.junit.Test

class ProgramParsingTest {
    @Test
    fun krishnaParserTest() {
        val projectAssetName = "binding-of-krishna"
        val folderName = "catrobatLanguageTests/"
        val context = CatroidApplication.getAppContext()
        val krishnaProgramFile = InstrumentationRegistry.getInstrumentation().context.assets.open(folderName + projectAssetName + ".txt")
        val serializedReferenceKrishnaProject = krishnaProgramFile.bufferedReader().use { it.readText() }
        val project = CatrobatLanguageParser.parseProgramFromString(serializedReferenceKrishnaProject, context)
        Assert.assertNotNull(project)
        val serializedKrishnaProject = CatrobatLanguageProjectSerializer(project!!, context).serialize()
        val referenceLines = serializedReferenceKrishnaProject.split('\n')
        val serializedLines = serializedKrishnaProject.split('\n')
        Assert.assertEquals("Equal Line Count", referenceLines.size, serializedLines.size)
        for (i in referenceLines.indices) {
            Assert.assertEquals("Error in Line " + (i + 1), referenceLines[i], serializedLines[i])
        }
    }
}
