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
package at.tugraz.ist.catroid.content.project;

import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.Observable;
import java.util.Set;
import java.util.HashSet;

public class Project extends Observable implements Serializable {
	private static final long serialVersionUID = 1L;
	public Set<Sprite> spriteList = new HashSet<Sprite>();
	private File projectPath;
	private String projectFile;
	private String projectTitle;
	
	
	public Project(String projectName) {
		projectTitle = projectName;
		projectPath = new File(Utils.concatPaths(ConstructionSiteActivity.DEFAULT_ROOT, projectName));
		projectFile = new String(projectName);
		if (!projectFile.contains(ConstructionSiteActivity.DEFAULT_FILE_ENDING))
			projectFile = Utils.addDefaultFileEnding(projectFile);
		boolean existed = projectPath.exists();
		if (existed) {
			loadExistingProject();
		} else {
			createNewProject();
		}
	}
	
	private void loadExistingProject() {
		//TODO: load spf file, parse it and create objects
		setChanged();
		notifyObservers();
	}
	
	private void createNewProject() {
		//TODO: create new project
		Sprite stage = new Sprite("stage");
		addSprite(stage);
		setChanged();
		notifyObservers();
	}
	
	public synchronized boolean addSprite(Sprite sprite) {
		System.out.println("Added sprite " + sprite.getName());
		return spriteList.add(sprite);
	}
	
	public synchronized boolean removeSprite(Sprite sprite) {
		return spriteList.remove(sprite);
	}
	
	public int getMaxZValue() {
		int maxZValue = Integer.MIN_VALUE;
		for (Sprite s : spriteList) {
			maxZValue = s.getZPosition() > maxZValue ? s.getZPosition() : maxZValue;
		}
		return maxZValue;
	}
	
}
