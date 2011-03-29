/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid;


public final class Consts {

    public static final String DIRECTORY_NAME = "catroid";
    public static final String PROJECT_EXTENTION = ".spf";
    
    public static final String DEFAULT_ROOT = "/sdcard/catroid";
    public static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
    public static final String IMAGE_DIRECTORY = "/images";
    public static final String SOUND_DIRECTORY = "/sounds";
    
    //dialogs:
    public static final int DIALOG_NEW_PROJECT = 0;
    public static final int DIALOG_LOAD_PROJECT = 1;
    public static final int DIALOG_ABOUT = 2;
    public static final int DIALOG_NEW_SPRITE = 3;
    public static final int DIALOG_RENAME_SPRITE = 4;
    public static final int DIALOG_NEW_SCRIPT = 5;
    public static final int DIALOG_RENAME_SCRIPT = 6;
    public static final int DIALOG_ADD_BRICK = 7;
    
    //Costume:
    public static final int MAX_REL_COORDINATES = 1000;
    public static final int THUMBNAIL_WIDTH = 80;
    public static final int THUMBNAIL_HEIGHT = 80;
    
}
