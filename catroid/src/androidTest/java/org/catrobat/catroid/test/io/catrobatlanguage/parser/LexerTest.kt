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

package org.catrobat.catroid.test.io.catrobatlanguage.parser

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.io.catlang.parser.parameter.ArgumentParserHelper
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LexerTest {
    @Test
    fun basicLexerTest() {
        val param = ArgumentParserHelper(CatroidApplication.getAppContext())
//        param.parseArgument("join('hello', 'world', '!')")
        try {
//            param.parseArgument("1&&2")
//            param.parseArgument("1×3+7/8-(9+6)×2")
//            param.parseArgument("1×3+7/8-9||2&&9")
//            param.parseArgument("index of current touch (1)")
//            param.parseArgument("14+-7")
            param.parseArgument("colour equals colour with % tolerance('#ff0000', '#ff0000', 1.0)")
        } catch (ex: Throwable) {
            println(ex.message)
        }
    }

    @Test
    fun basicProgramParserTest() {
        val project = CatrobatLanguageParser.parseProgramFromString(getTestProgramString())
        assert(project != null)
    }

    fun getTestProgramString(): String {
        return """
            #! Catrobat Language Version 0.1
            Program 'Catrobat Language Demo 1' {

                Metadata {
                    Description: '',
                    Catrobat version: '1.12',
                    Catrobat app version: '1.1.2'
                }

                Stage {
                    Landscape mode: 'false',
                    Height: '1920',
                    Width: '1080',
                    Display mode: 'maximize'
                }

                Globals {
                    "clickCounter",
                    *clickLogger*
                }

                Multiplayer variables {
                    "multiplayervar1"
                }

                Scene 'Scene 1' {
                    Background {
                        Looks {
                            'Background': 'background.png'
                        }
                    }

                    Actor or object 'Clouds1' of type 'Sprite' {
                        Looks {
                            'Clouds': 'clouds1.png'
                        }

                        Scripts {
                            Whene scene starts {
                                # Place at (x: (0), y: (0));
                                // # disabled note brick
                                # simple Note brick Glide to (x: (-1080), y: (0), seconds: (5));
                                // Place at (x: (1080), y: (0));
                                // Forever {
                                    Glide to (x: (-1080), y: (0), seconds: (10));
                                    Place at (x: (1080), y: (0));
                                // }
                            }
                        }
                    }

                    Actor or object 'Clouds2' of type 'Sprite' {
                        Looks {
                            'Clouds': 'clouds2.png'
                        }

                        Scripts {
                            When scene starts {
                                Place at (x: (1080), y: (0));
                                Forever {
                                    Glide to (x: (-1080), y: (0), seconds: (10));
                                    Place at (x: (1080), y: (0));
                                }
                                // Forever {
                                    // Glide to (x: (-1080), y: (0), seconds: (10));
                                    // Place at (x: (1080), y: (0));
                                // }
                            }
                        }
                    }

                    Actor or object 'Animal' of type 'Sprite' {
                        Locals {
                            "lastClick"
                        }

                        Looks {
                            'Wings up': 'wingsup.png',
                            'Wings down': 'wingsdown.png'
                        }

                        Sounds {
                            'Tweet1': 'tweet1.mp3',
                            'Tweet2': 'tweet2.mp3'
                        }

                        Scripts {
                            When scene starts {
                                Forever {
                                    Glide to (x: (300), y: (200), seconds: (1));
                                }
                            }

                            When scene starts {
                                Forever {
                                    Next Look;
                                    Wait (seconds: (0.2));
                                }
                            }

                            When tapped {
                                Start (sound: ('Tweet1'));
                                Set (variable: ("clickCounter"), value: ("clickCounter" + 1));
                                Set (variable: ("lastClick"), value: (join(minute, ':', second)));
                                Add (list: (*clickLogger*), item: (join(minute, ':', second)));
                                Show (variable: ("lastClick"), x: (-457), y: (816));
                                Show (variable: ("clickCounter"), x: (-460), y: (884));
                            }
                        }
                    }
                }
            }
        """.trimIndent()
    }
}