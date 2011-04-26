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
package at.tugraz.ist.catroid.constructionSite.content;

import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;

/**
 * @author jib218
 * 
 */
public class ProjectValuesManager {
	private Sprite currentSprite = null;
	private Script currentScript = null;
	private List<Sprite> spriteList = null;

	private ProjectManager projectManager = ProjectManager.getInstance();

	public Sprite getCurrentSprite() {
		return this.currentSprite;
	}

	public Script getCurrentScript() {
		return this.currentScript;
	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}

	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
	}

	public boolean setCurrentScript(Script script) {
		if (script == null) {
			currentScript = null;
			return true;
		}
		if (currentSprite.getScriptList().contains(script)) {
			currentScript = script;
			return true;
		}
		return false;
	}

	public int getCurrentSpritePosition() {
		return projectManager.getCurrentProject().getSpriteList().indexOf(currentSprite);
	}

	public int getCurrentScriptPosition() {
		int currentSpritePos = this.getCurrentSpritePosition();
		if (currentSpritePos == -1) {
			return -1;
		}

		return projectManager.getCurrentProject().getSpriteList().get(currentSpritePos).getScriptList()
				.indexOf(currentScript);
	}

	public boolean setCurrentSpriteWithPosition(int position) {
		if (position >= projectManager.getCurrentProject().getSpriteList().size() || position < 0) {
			return false;
		}

		currentSprite = projectManager.getCurrentProject().getSpriteList().get(position);
		return true;

	}

	public boolean setCurrentScriptWithPosition(int position) {
		int currentSpritePos = this.getCurrentSpritePosition();
		if (currentSpritePos == -1) {
			return false;
		}

		if (position >= projectManager.getCurrentProject().getSpriteList().get(currentSpritePos).getScriptList().size()
				|| position < 0) {
			return false;
		}

		currentScript = projectManager.getCurrentProject().getSpriteList().get(this.getCurrentSpritePosition())
				.getScriptList().get(position);
		return true;

	}

	public void updateProjectValuesManager() {
		Project project = ProjectManager.getInstance().getCurrentProject();
		this.spriteList = project.getSpriteList();
	}

}
