package org.catrobat.catroid.formulaeditor;

import java.io.Serializable;

import org.catrobat.catroid.ProjectManager;


public class UserVariableScope implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private ScopeType scopeType;

	public static enum ScopeType {
		SPRITE, PROJECT
	}

	public UserVariableScope(String name, ScopeType scopeType) {
		this.scopeType = scopeType;
		this.name = name;
	}

	public boolean checkScope(String spriteName) {

		switch (scopeType) {
			case SPRITE:
				if (spriteName.equals(name)) {
					return true;
				}
				break;
			case PROJECT:
				if (ProjectManager.getInstance().getCurrentProject().getName().equals(name)) {
					return true;
				}
				break;
		}

		return false;

	}

}
