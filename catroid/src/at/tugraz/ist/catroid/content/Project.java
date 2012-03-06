/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Sprite> spriteList = new ArrayList<Sprite>();
    private String projectName;

    // Only used for Catroid website
    @SuppressWarnings("unused")
    private String deviceName;
    @SuppressWarnings("unused")
    private int androidVersion;
    @SuppressWarnings("unused")
    private String catroidVersionName;
    @SuppressWarnings("unused")
    private int catroidVersionCode;

    @XStreamAlias("screenWidth")
    public int virtualScreenWidth = 0;
    @XStreamAlias("screenHeight")
    public int virtualScreenHeight = 0;

    public String description;

    public Project(Context context, String name) {
        this.projectName = name;

        ifLandscapeSwitchWidthAndHeight();
        virtualScreenWidth = Values.SCREEN_WIDTH;
        virtualScreenHeight = Values.SCREEN_HEIGHT;
        setDeviceData(context);

        if (context == null) {
            return;
        }

        Sprite background = new Sprite(context.getString(R.string.background));
        background.costume.zPosition = Integer.MIN_VALUE;
        addSprite(background);
    }

    private void ifLandscapeSwitchWidthAndHeight() {
        if (Values.SCREEN_WIDTH > Values.SCREEN_HEIGHT) {
            int tmp = Values.SCREEN_HEIGHT;
            Values.SCREEN_HEIGHT = Values.SCREEN_WIDTH;
            Values.SCREEN_WIDTH = tmp;
        }
    }

    public synchronized void addSprite(Sprite sprite) {
        if (spriteList.contains(sprite)) {
            return;
        }
        spriteList.add(sprite);
    }

    public synchronized boolean removeSprite(Sprite sprite) {
        return spriteList.remove(sprite);
    }

    public List<Sprite> getSpriteList() {
        return spriteList;
    }

    public void setName(String name) {
        this.projectName = name;
    }

    public String getName() {
        return projectName;
    }

    public void setDeviceData(Context context) {
        deviceName = Build.MODEL;
        androidVersion = Build.VERSION.SDK_INT;

        if (context == null) {
            catroidVersionName = "unknown";
            catroidVersionCode = 0;
        } else {
            catroidVersionName = Utils.getVersionName(context);
            catroidVersionCode = Utils.getVersionCode(context);
        }
    }
}
