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
package at.tugraz.ist.catroid.content.brick;

import at.tugraz.ist.catroid.content.sprite.Costume;
import at.tugraz.ist.catroid.content.sprite.Sprite;


public abstract class SetCostumeBrickBase implements BrickBase{
    private static final long serialVersionUID = 1L;
    private Sprite sprite;
    private Costume costume;
    
    
    public SetCostumeBrickBase(Sprite sprite, String imagePath) { 
        //TODO: is imagepath from project allowed or only gallery?
        //if from gallery allowed we have to check if this costume is already added
        this.sprite = sprite;
        this.costume = new Costume(sprite,imagePath);
        this.sprite.getCostumeList().add(costume);
    }

    public void execute() {
        this.sprite.setCurrentCostume(costume); 
    }
    
    public Sprite getSprite(){
        return sprite;
    }
}
