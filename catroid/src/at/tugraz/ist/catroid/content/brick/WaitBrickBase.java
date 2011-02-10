package at.tugraz.ist.catroid.content.brick;

import at.tugraz.ist.catroid.content.sprite.Sprite;

public abstract class WaitBrickBase implements BrickBase {
	private static final long serialVersionUID = 1L;
	protected int timeToWaitInMilliseconds;

	public WaitBrickBase(int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
	}

	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public Sprite getSprite() {
		return null;
	}

}
