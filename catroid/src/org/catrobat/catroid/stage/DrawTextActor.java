package org.catrobat.catroid.stage;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by marc on 14.04.2015.
 */
public class DrawTextActor extends Actor{

	private int posX;
	private int posY;
	private String text;
	private float scale = 2f;
	private BitmapFont font;

	public DrawTextActor(String text, int posX, int posY) {
		this.text = text;
		this.posX = posX;
		this.posY = posY;
		init();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		font.draw(batch, text, posX, posY);
	}

	private void init(){
		font = new BitmapFont();
		font.setColor(1.0f, 0.0f, 0.0f, 1.0f);
		font.setScale(scale);
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public void setText(String text) {
		this.text = text;
	}
}
