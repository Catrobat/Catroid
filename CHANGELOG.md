Changelog
=========

version 0.9.7
-----------------
 - fix crashes on Android 4.4 when importing images and sound
 - fixes a bug where some math functions didn't work correctly
 - open websites in-app
 - stage: fix broadcast behavior
 - stage: hiding objects outside, when not in stretch-mode
 - stage: correctly show projects with different aspect ratio
 - stage: correctly taking automatic screenshots during longer sessions
 - many smaller bugfixes and user experience improvements

version 0.9.6
-----------------
- fix a bug where some projects could not be loaded

version 0.9.5
-----------------
- improved "new object" workflow
- tutorial and help 
- correct download link for Pocket Paint
- bugfixes

version 0.9.4
-----------------
- older programs should be openable again
- alert dialog style
- improved usability
- bugfixes

version 0.9.3
-----------------
- dialog redesign
- improved usability
- bugfixes

version 0.9.2
-----------------
- fixed: copying a brick sometimes crashed the app

version 0.9.1
-----------------
- Catrobat program's version will be updated now before the upload

Public beta release 0.7 (2012-10-06)
-----------------
- UI: Redesign. Now using fragments, ActionBarSherlock for Pre-ICS devices. This was done as part of a Google Summer of Code 2012 project by our GSoC student [https://github.com/atermenji Artur Termenji]. Many thanks to him.
- Fix: Better handling of the brick list. Added correct behaviour for nested loops.
- Enhancement: Better performance in the Stage.
- Enhancement: Upload and download of projects is now done in background, with notifications.
- New: A new XML format for the Catrobat programs was introduced. '''Note:''' this means that all projects done with versions prior to 0.7.0 are not compatible with this release!
- Many, many, many bugfixes. :)

Public beta release 0.6 (2012-3-10)
-----------------
- Short version: Lots and lots of new stuff ;-) Please try it out!
- Among others: Support for Lego Mindstorms robots, bricks galore (motion: place at, set x to, set y to, change x by, change y by, if on edge bounce, move n steps, turn left/right n degrees, point in direction, point towards, glide s seconds to x y, go back n layers, go to front; looks: switch to costume, next costume, sit size to, change size by, hide, show, set ghost effect to n%, change ghost effect by n, set brightness to n%, change brightness by, clear graphic effects; sound: play sound, stop all sounds, set volume to n%, change volume by, speak; control: when project starts, when tapped, wait s seconds, when I receive ABC as a message, broadcast ABC, broadcast ABC and wait, note, forever, repeat n times), a completely new design, our own sound recorder (some tablets do not have one preinstalled), better manual handling of bricks, and many more, e.g., auto scaling to different device resolutions with or without aspect ratio preservation, or a "report as inappropriate" button for uploaded projects. There are also many things behind the scene, e.g., new languages in all projects. 

Alpha release 0.5 (2011-4)
-----------------
- This was a refactoring-only release for internal use only.

Alpha release 0.4 (2010-12-27)
-----------------
- new bricks: <code>[come to front]</code>, <code>[go back x layers]</code>, and <code>[if touched do]</code> ([http://blog.catroid.org/2011/01/multitouch-piano-project.html multitouch subject to phone capabilities]) 
- coordinates range now from -1000 to +1000 in both X and Y directions, 1000 being the border and (0,0) in the center of the screen
- website that hosts catroid project files: http://www.catroid.org/ (several demo projects there)
- upload projects to http://www.catroid.org/ from inside catroid
- extract, edit, and execute projects downloaded from http://www.catroid.org/
- paintroid, a new paint editor:
  * allows to draw new pictures and edit old ones
  * save as ...
  * allows to pinch-zoom up to pixel level
  * allows to color whole areas or pixels to any color
  * colors can be chosen from a color in the picture or using a color chooser gadget
  * allows to set areas or pixels to transparent to delete unwanted background in costumes
  * many unit, functional, and regressions tests
- [http://www.youtube.com/watch?v=WTppqL6Q4Y4&hd=1 demo video]

Alpha release 0.3 (2010-09-20)
-----------------
- special background object (stage)
- new brick: <code>[scale to x %]</code>
- [http://www.youtube.com/watch?v=VZBCTjxD7Eo&feature=player_embedded tutorial]
- [http://www.youtube.com/watch?v=vOv8Eli0cVs&feature=player_embedded demo video]

Alpha release 0.2 (2010-08-09)
-----------------
- new bricks: <code>[goto x y]</code>, <code>[hide]</code>, and <code>[show]</code>
- multiple sprites
- save/load project file on SD card
- transparent backgrounds in costumes
- English/German according to phone language

Alpha release 0.1 (2010-07-21)
-----------------
- one sprite only
- German only
- supported bricks: <code>[set costume]</code>, <code>[wait]</code>, and <code>[play sound]</code>
- [http://www.youtube.com/watch?v=7AfwpKOhsos&feature=player_embedded demo video (in German)]
