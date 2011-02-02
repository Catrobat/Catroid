package at.tugraz.ist.catroid.content.project;

import at.tugraz.ist.catroid.content.sprite.Sprite;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;



public class Project implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Set<Sprite> spriteList = new HashSet<Sprite>();
	private static Project instance;
	
	public static Project getInstance() {
		if(instance == null) {
			instance = new Project();
		}
		return instance;
	}
	
	private Project() {
	}
	
	
	public synchronized boolean addSprite(Sprite sprite) {
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
