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
import org.catrobat.catroid.io.catlang.parser.parameter.ParameterParser
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageProjectSerializer
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParserTest {
    @Test
    fun basicProgramParserTest() {
        val project = CatrobatLanguageParser.parseProgramFromString(getTestProgram2(), CatroidApplication.getAppContext())
        assert(project != null)

        try {
            val serializer = CatrobatLanguageProjectSerializer(project!!, CatroidApplication.getAppContext())
            val serializedProgram = serializer.serialize()
            assert(serializedProgram == getTestProgram2())
        } catch (t: Throwable) {
            println(t)
        }
    }

    fun getTestProgram2(): String {
        return """#! Catrobat Language Version 0.1
Program 'My project' {
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
    "Variable",
    *List*
  }

  Scene 'Scene' {
    Background {
      Looks {
        'Background': 'img.png'
      }
      Sounds {
        'Untitled song': 'MUS-1664320882.midi'
      }
      Scripts {
        When tapped {
        }
        When scene starts {
        }
        When tapped {
        }
        When stage is tapped {
        }
        When you receive (message: ('new message')) {
          Broadcast (message: ('new message'));
          Broadcast and wait (message: ('new message'));
        }
        When condition becomes true (condition: (1 < 2)) {
        }
        When background changes to (look: ('Background')) {
        }
        When you start as a clone {
          Create clone of (actor or object: (yourself));
          Delete this clone;
          Wait (seconds: (1));
          # add comment here…
          Forever {
          }
          If (condition: (1 < 2)) {
          } else {
          }
          If (condition: (1 < 2)) {
          }
          Wait until (condition: (1 < 2));
          Repeat (times: (10)) {
          }
          Repeat until (condition: (1 < 2)) {
          }
          For (value: ("Variable"), from: (1), to: (10)) {
          }
          For each value in list (value: ("Variable"), list: (*List*)) {
          }
          Continue (scene: ('2nd Scene'));
          Start (scene: ('Scene'));
          Finish stage;
          Stop (script: (this script));
          Wait until all other scripts have stopped;
        }
        When you start as a clone {
          Create clone of (actor or object: (yourself));
          Delete this clone;
        }
        When you receive (message: ('new message')) {
          Broadcast (message: ('new message'));
          Broadcast and wait (message: ('new message'));
          Single tap at (x: (- 100), y: (- 200));
          Touch at position for seconds (x: (- 100), y: (- 200), seconds: (0.3));
          Touch at position and slide to position in seconds (start x: (- 100), start y: (- 200), to x: (100), to y: (200), seconds: (0.3));
          Open in browser (url: ('https://catrobat.org/'));
          Place at (x: (100), y: (200));
          Set (x: (100));
          Set (y: (200));
          Change x by (value: (10));
          Change y by (value: (10));
          Go to (target: (touch position));
          Move (steps: (10));
          Turn (direction: (left), degrees: (15));
          Turn (direction: (right), degrees: (15));
          Point in direction (degrees: (90));
          Point towards (actor or object: ('Clouds1'));
          Set (rotation style: (left-right only));
          Glide to (x: (100), y: (200), seconds: (1));
          Become focus point with flexibility in percent (horizontal: (0), vertical: (0));
          Vibrate for (seconds: (1));
          Set (motion type: (moving and bouncing under gravity));
          Set velocity to (x steps/second: (0), y steps/second: (0));
          Spin (direction: (left), degrees/second: (15));
          Spin (direction: (right), degrees/second: (15));
          Set gravity for all actors and objects to (x steps/second²: (0), y steps/second²: (- 10));
          Set (mass in kilograms: (1));
          Set (bounce factor percentage: (80));
          Set (friction percentage: (20));
          Fade particle (effect: (in));
          Start (sound: ('Untitled song'));
          Start sound and wait (sound: ('Untitled song'));
          Start sound and skip seconds (sound: ('Untitled song'), seconds: (0.5));
          Stop (sound: ('Untitled song'));
          Stop all sounds;
          Set (volume percentage: (60));
          Change volume by (value: (- 10));
          Set (instrument: (piano));
          Play (note: (70), number of beats: (1));
          Play (drum: (snare drum), number of beats: (1));
          Set (tempo: (60));
          Change tempo by (value: (10));
          Pause for (number of beats: (1));
          Next look;
          Previous look;
          Set (size percentage: (60));
          Change size by (value: (10));
          Hide;
          Show;
          Ask (question: ('What\'s your name?'), answer variable: ("Variable"));
          Show (variable: ("Variable"), x: (100), y: (200));
          Show (variable: ("Variable"), x: (100), y: (200), size: (120), color: ('#FF0000'), alignment: (centered));
          Set (transparency percentage: (50));
          Change transparency by (value: (25));
          Set (brightness percentage: (50));
          Change brightness by (value: (25));
          Set (color: (#000000));
          Change color by (value: (25));
          Fade particle (effect: (in));
          Turn (particle effect additivity: (on));
          Set (particle color: ('#ff0000'));
          Clear graphic effects;
          Become focus point with flexibility in percent (horizontal: (0), vertical: (0));
        }
        When background changes to (look: ('Background')) {
          Set background to (look: ('Background'));
          Set background to (look by number: (1));
          Set background and wait (look: ('Background'));
          Set background and wait (look by number: (1));
          Turn (camera: (on));
          Use (camera: (front));
          Turn (flashlight: (on));
          Get image from source and use as background (url: ('https://catrob.at/HalloweenPortrait'));
          Paint new look (name: ('name of new look'));
          Edit look;
          Copy look (name of copy: ('name of copied look'));
          Delete look;
          Open in browser (url: ('https://catrobat.org/'));
          Clear;
          Return (value: (0));
          Set (variable: ("Variable"), value: (1));
          Change (variable: ("Variable"), value: (1));
          Show (variable: ("Variable"), x: (100), y: (200));
          Show (variable: ("Variable"), x: (100), y: (200), size: (120), color: ('#FF0000'), alignment: (centered));
          Hide (variable: ("Variable"));
          Write on device (variable: ("Variable"));
          Read from device (variable: ("Variable"));
          Write to file (variable: ("Variable"), file: ('variable.txt'));
          Read from file (variable: ("Variable"), file: ('variable.txt'), action: (keep the file));
          Add (list: (*List*), item: (1));
          Delete item at (list: (*List*), position: (1));
          Delete all items (list: (*List*));
          Insert (list: (*List*), position: (1), value: (1));
          Replace (list: (*List*), position: (1), value: (1));
          Write on device (list: (*List*));
          Read from device (list: (*List*));
          Store column of comma-separated values to list (list: (*List*), csv: ('kitty,cute\npuppy,naughty\noctopus,intelligent'), column: (1));
          Send web request (url: ('https://catrob.at/joke'), answer variable: ("Variable"));
          Get image from source and use as background (url: ('https://catrob.at/HalloweenPortrait'));
          Ask (question: ('What\'s your name?'), answer variable: ("Variable"));
          Reset timer;
        }
        When tapped {
        }
        When stage is tapped {
          Send web request (url: ('https://catrob.at/joke'), answer variable: ("Variable"));
          Get image from source and use as background (url: ('https://catrob.at/HalloweenPortrait'));
          Open in browser (url: ('https://catrobat.org/'));
          Vibrate for (seconds: (1));
          Turn (camera: (on));
          Use (camera: (front));
          Turn (flashlight: (on));
          Write on device (variable: ("Variable"));
          Read from device (variable: ("Variable"));
          Write to file (variable: ("Variable"), file: ('variable.txt'));
          Read from file (variable: ("Variable"), file: ('variable.txt'), action: (keep the file));
          Write on device (list: (*List*));
          Read from device (list: (*List*));
          Single tap at (x: (- 100), y: (- 200));
          Touch at position for seconds (x: (- 100), y: (- 200), seconds: (0.3));
          Touch at position and slide to position in seconds (start x: (- 100), start y: (- 200), to x: (100), to y: (200), seconds: (0.3));
          Turn NXT (motor: (A), degrees: (180));
          Stop NXT (motor: (A));
          Set NXT (motor: (A), speed percentage: (100));
          Play NXT tone (seconds: (1), frequency x100Hz: (2));
          Turn EV3 (motor: (A), degrees: (180));
          Set EV3 (motor: (A), speed percentage: (100));
          Stop EV3 (motor: (A));
          Play EV3 tone (seconds: (1), frequency x100Hz: (2), volume: (100));
          Set EV3 (status: (green));
          Set Arduino (digital pin: (13), value: (1));
          Set Arduino (PWM~ pin: (3), value: (255));
          Take off / land AR.Drone 2.0;
          Emergency AR.Drone 2.0;
          Move AR.Drone 2.0 (direction: (up), seconds: (1), power percentage: (20));
          Move AR.Drone 2.0 (direction: (down), seconds: (1), power percentage: (20));
          Move AR.Drone 2.0 (direction: (left), seconds: (1), power percentage: (20));
          Move AR.Drone 2.0 (direction: (right), seconds: (1), power percentage: (20));
          Move AR.Drone 2.0 (direction: (forward), seconds: (1), power percentage: (20));
          Move AR.Drone 2.0 (direction: (backward), seconds: (1), power percentage: (20));
          Turn AR.Drone 2.0 (direction: (left), seconds: (1), power percentage: (20));
          Turn AR.Drone 2.0 (direction: (right), seconds: (1), power percentage: (20));
          Flip AR.Drone 2.0;
          Play AR.Drone 2.0 (flash animation: (blink green));
          Switch AR.Drone 2.0 camera;
          Move Jumping Sumo (direction: (forward), steps: (1), power percentage: (80));
          Move Jumping Sumo (direction: (backward), steps: (1), power percentage: (80));
          Start Jumping Sumo (animation: (spin));
          Play Jumping Sumo (sound: (normal), volume: (50));
          Stop Jumping Sumo sound;
          Jump Jumping Sumo (type: (long));
          Jump Jumping Sumo (type: (high));
          Turn Jumping Sumo (direction: (left), degrees: (90));
          Turn Jumping Sumo (direction: (right), degrees: (90));
          Flip Jumping Sumo;
          Take picture with Jumping Sumo;
          Move Phiro (motor: (left), direction: (forward), speed percentage: (100));
          Move Phiro (motor: (left), direction: (backward), speed percentage: (100));
          Stop Phiro (motor: (both));
          Play Phiro (tone: (do), seconds: (1));
          Set Phiro (light: (both), color: (#00ffff));
          If (activated phiro: (front left sensor)) {
          } else {
          }
          Set (variable: ("Variable"), value: (phiro front left sensor));
          Set (variable: ("Variable"), value: (phiro front right sensor));
          Set (variable: ("Variable"), value: (phiro side left sensor));
          Set (variable: ("Variable"), value: (phiro side right sensor));
          Set (variable: ("Variable"), value: (phiro bottom left sensor));
          Set (variable: ("Variable"), value: (phiro bottom right sensor));
        }
        When tapped (gamepad button: (A)) {
        }
        When Raspberry Pi pin changes to (pin: (3), position: (high)) {
          If (Raspberry Pi pin: (3)) {
          } else {
          }
          Set (Raspberry Pi pin: (3), value: (1));
          Set (Raspberry Pi PWM~ pin: (3), percentage: (50), Hz: (100));
          Stitch;
          Set (thread color: ('#ff0000'));
          Start running stitch (length: (10));
          Start zigzag stitch (length: (2), width: (10));
          Start triple stitch (length: (10));
          Sew up;
          Stop current stitch;
          Write embroidery data to (file: ('embroidery.dst'));
          Assert equals (actual: (0), expected: (0));
          Assert lists (actual: (*List*), expected: (*List*));
          For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: (), value: (0), reference list: (*List*)) {
          }
          Wait until all other scripts have stopped;
          Single tap at (x: (- 100), y: (- 200));
          Touch at position for seconds (x: (- 100), y: (- 200), seconds: (0.3));
          Touch at position and slide to position in seconds (start x: (- 100), start y: (- 200), to x: (100), to y: (200), seconds: (0.3));
          Finish tests;
          Store column of comma-separated values to list (list: (*List*), csv: ('kitty,cute\npuppy,naughty\noctopus,intelligent'), column: (1));
          Send web request (url: ('https://catrob.at/joke'), answer variable: ("Variable"));
        }
      }
    }
    Actor or object 'Clouds1' {
      Looks {
        'Clouds': 'img_#0.png'
      }
      Scripts {
        When scene starts {
          Place at (x: (0), y: (0));
          Glide to (x: (- 1080), y: (0), seconds: (5));
          Place at (x: (1080), y: (0));
          Forever {
            Glide to (x: (- 1080), y: (0), seconds: (10));
            Place at (x: (1080), y: (0));
          }
        }
      }
    }
    Actor or object 'Clouds2' {
      Looks {
        'Clouds': 'img_#1.png'
      }
      Scripts {
        When scene starts {
          Place at (x: (1080), y: (0));
          Forever {
            Glide to (x: (- 1080), y: (0), seconds: (10));
            Place at (x: (1080), y: (0));
          }
        }
      }
    }
    Actor or object 'Animal' {
      Looks {
        'Wings up': 'img_#2.png',
        'Wings down': 'img_#3.png'
      }
      Sounds {
        'Tweet1': 'snd.wav',
        'Tweet2': 'snd_#0.wav'
      }
      Scripts {
        When scene starts {
          Forever {
            Glide to (x: (random value from to( - 300 , 300 )), y: (random value from to( - 200 , 200 )), seconds: (1));
          }
        }
        When scene starts {
          Forever {
            Next look;
            Wait (seconds: (0.2));
          }
        }
        When tapped {
          Start (sound: ('Tweet1'));
        }
      }
    }
  }
  Scene '2nd Scene' {
    Background {
    }
  }
}
""".trimIndent()
    }

//    fun getTestProgram2(): String {
//        return """#! Catrobat Language Version 0.1
//Program 'My project' {
//  Metadata {
//    Description: '',
//    Catrobat version: '1.12',
//    Catrobat app version: '1.1.2'
//  }
//
//  Stage {
//    Landscape mode: 'false',
//    Width: '1080',
//    Height: '2154',
//    Display mode: 'STRETCH'
//  }
//
//
//  Scene 'Scene' {
//    Background {
//      Looks {
//        'Background': 'img.png'
//      }
//      Scripts {
//        When tapped {
//          Set (x: (100));
//          Forever {
//            Set (y: (200));
//            If (condition: (1 < 2)) {
//              Change x by (value: (490 + 600 / 900));
//            } else {
//              Change y by (value: (10));
//            }
//            Move (steps: (10));
//          }
//          Turn (direction: (right), degrees: (15));
//        }
//      }
//    }
//    Actor or object 'Clouds1' {
//      Looks {
//        'Clouds': 'img_#0.png'
//      }
//      Scripts {
//        When scene starts {
//          # add comment here…
//          Place at (x: (0), y: (0));
//          Emergency AR.Drone 2.0;
//          Glide to (x: (- 1080), y: (0), seconds: (5));
//          Place at (x: (1080), y: (0));
//          // Forever {
//            Glide to (x: (- 1080), y: (0), seconds: (10));
//          // }
//          `Label text [Name of input]` ([Name of input]: (2));
//        }
//      }
//      User Defined Bricks {
//        Define `Label text [Name of input]` with screen refresh as {
//          If (condition: (1 < 2)) {
//            // Change y by (value: (10));
//          } else {
//            // If on edge, bounce;
//          }
//          `Label text [Name of input]` ([Name of input]: (1));
//          Place at (x: (1080), y: (0));
//        }
//      }
//    }
//    Actor or object 'Clouds2' {
//      Looks {
//        'Clouds': 'img_#1.png'
//      }
//      Scripts {
//        When scene starts {
//          // Place at (x: (1080), y: (0));
//          Forever {
//            Glide to (x: (- 1080), y: (0), seconds: (10));
//            Place at (x: (1080), y: (0));
//          }
//        }
//      }
//    }
//    Actor or object 'Animal' {
//      Looks {
//        'Wings up': 'img_#2.png',
//        'Wings down': 'img_#3.png'
//      }
//      Sounds {
//        'Tweet1': 'snd.wav',
//        'Tweet2': 'snd_#0.wav'
//      }
//      Scripts {
//        When scene starts {
//          Forever {
//            Glide to (x: (random value from to( - 300 , 300 )), y: (random value from to( - 200 , 200 )), seconds: (1));
//          }
//        }
//        When scene starts {
//          Forever {
//            Next look;
//            Wait (seconds: (0.2));
//          }
//        }
//        When tapped {
//          Start (sound: ('Tweet1'));
//        }
//      }
//    }
//  }
//}
//""".trimIndent()
//    }

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