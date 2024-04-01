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

import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.io.catlang.parser.parameter.error.FormulaParsingException
import org.catrobat.catroid.io.catlang.parser.parameter.error.UnkownSensorOrFunctionException
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("LargeClass")
class FailingParserTests {

    @Test
    fun unkownUserDefinedBrickTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          `Testlabel 1 [Input 1] .` ([Input 1]: (0));
        }
      }
      User Defined Bricks {
        Define (user defined brick: (`Testlabel 1 [Input 1] [Input 2] .`), screen refresh: (on)) {
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(CatrobatLanguageParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("User defined brick Testlabel 1[Input 1]. must be defined before it can be used.", exception.message)
        }
    }

    @Test
    fun unkownUserDefinedBrickParameterTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          `Testlabel 1 [Input 1] [Input 2] .` ([Input 1]: (0), [Input 2]: (1));
        }
      }
      User Defined Bricks {
        Define (user defined brick: (`Testlabel 1 [Input 1] [Input 2] .`), screen refresh: (on)) {
          Set (x: ([Input]));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(FormulaParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("Unknown user defined brick parameter: Input", exception.message)
        }
    }

    @Test
    fun unkownVariableTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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
    "globalVar1"
  }

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          Show (variable: ("var1"), x: (0), y: (0));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(CatrobatLanguageParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("No variable found with name: var1", exception.message)
        }
    }

    @Test
    fun unkownListTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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
    "globalVar1"
  }

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          Add (list: (*globalVar1*), item: (0));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(CatrobatLanguageParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("Unkown list: globalVar1", exception.message)
        }
    }

    @Test
    fun unkownBrickTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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
    "globalVar1"
  }

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          Set x (value: (0));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(CatrobatLanguageParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("Unknown brick: Set x", exception.message)
        }
    }

    @Test
    fun tooManyParametersTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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
    "list1"
  }

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          Set (x: (0), y: (0));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(CatrobatLanguageParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("Set requires either parameter 'bounce factor percentage', 'particle color', 'brightness percentage', 'transparency percentage', 'variable', 'x', 'pen size', 'text', 'Raspberry Pi PWM~ pin', 'mass in kilograms', 'color', 'y', 'volume percentage', 'tempo', 'instrument', 'thread color', 'Raspberry Pi pin', 'motion type', 'rotation style', 'size percentage' or 'friction percentage'", exception.message)
        }
    }

    @Test
    fun missingParametersTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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
    "globalVar1"
  }

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          Replace (value: (0), position: (1));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(CatrobatLanguageParsingException::class.simpleName, exception::class.simpleName)
            assertEquals("Replace requires the following arguments: list, position, value", exception.message)
        }
    }

    @Test
    fun unkownFormulaFunctionTest() {
        val programString = """#! Catrobat Language Version 0.1
Program 'SpinnerSerializationTest' {
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
    "globalVar1"
  }

  Scene 'Scene' {
    Background {
      Scripts {
        When scene starts {
          Set (x: (sinus(0)));
        }
      }
    }
  }
}"""
        try {
            CatrobatLanguageParser.parseProgramFromString(programString, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: Exception) {
            assertEquals(UnkownSensorOrFunctionException::class.simpleName, exception::class.simpleName)
            assertEquals("Unknown sensor, property or method: sinus", exception.message)
        }
    }
}
