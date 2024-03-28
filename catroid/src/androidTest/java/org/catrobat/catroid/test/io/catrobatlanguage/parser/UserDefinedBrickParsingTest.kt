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

import android.content.Context
import android.content.res.Configuration
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick
import org.catrobat.catroid.content.bricks.*
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageProjectSerializer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

@Suppress("LargeClass")
class UserDefinedBrickParsingTest {

    private val serializedProgram = """#! Catrobat Language Version 0.1
Program 'User Defined Brick Parser Test' {
  Metadata {
    Description: '',
    Catrobat version: '1.12',
    Catrobat app version: '1.1.2'
  }

  Stage {
    Landscape mode: 'false',
    Width: '1080',
    Height: '2154',
    Display mode: 'STRETCH'
  }

  Globals {
    "var1",
    "var2",
    "var3",
    *list1*,
    *list2*,
    *list3*
  }

  Scene 'Scene' {
    Background {
    }
    Actor or object 'testSprite' {
      Locals {
        "localVar1",
        "localVar2"
      }
      Scripts {
        When scene starts {
          `Testlabel 1 [Input 1] [Input 2] .` ([Input 1]: ("var1"), [Input 2]: ("localVar1"));
        }
      }
      User Defined Bricks {
        Define (user defined brick: (`Testlabel 1 [Input 1] [Input 2] .`), screen refresh: (on)) {
          Set (y: ([Input 1]));
          Wait (seconds: ([Input 2] + "var2"));
          If (condition: (true)) {
            Forever {
              Set (x: ("var3" + [Input 1] + item( 0 , *list1* )));
            }
          }
        }
      }
    }
    Actor or object 'testSprite1' {
    }
    Actor or object 'testSprite2' {
    }
    Actor or object 'testSprite3' {
    }
  }
  Scene 's1' {
    Background {
    }
  }
  Scene 's2' {
    Background {
    }
  }
  Scene 's3' {
    Background {
    }
  }
}
"""

    @Test
    fun testUserDefinedBrick() {
        val locales = listOf(Locale.ROOT, Locale.GERMAN, Locale.CHINA)
        for (locale in locales) {
            executeLocalizedTest(locale)
        }
    }

    private fun executeLocalizedTest(locale: Locale) {
        try {
            val context = CatroidApplication.getAppContext()
            var configuration = context.resources.configuration
            configuration = Configuration(configuration)
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
            val parsedProgram = CatrobatLanguageParser.parseProgramFromString(this.serializedProgram, context)
            val serializedProgram = CatrobatLanguageProjectSerializer(parsedProgram!!, context).serialize()
            assertEquals(this.serializedProgram, serializedProgram)
        } catch (throwable: Throwable) {
            throw throwable
        }
    }
}