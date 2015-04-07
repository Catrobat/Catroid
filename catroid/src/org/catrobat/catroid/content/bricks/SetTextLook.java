package org.catrobat.catroid.content.bricks;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;

/**
 * Created by marc on 26.03.2015.
 */
public class SetTextLook extends Look {

	private int posX;
	private int posY;
	private String text = "";
	private float scale = 2f;
	private BitmapFont font;
	private SpriteBatch spriteBatch;

	public SetTextLook(Sprite sprite) {
		super(sprite);
		init();
	}

	public SetTextLook(Sprite sprite, String text, int posX, int posY) {
		super(sprite);

		this.text = text;
		this.posX = posX;
		this.posY = posY;
		init();
	}


	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Camera camera = getStage().getCamera();


		//spriteBatch.begin();
		font.draw(batch, text, posX, posY);
		//font.draw(batch, text, camera.viewportWidth/2F-font.getBounds(text).width/2F+posX, camera.viewportHeight/2F+posY);
		//font.drawWrapped(spriteBatch, text, posX, posY, 10f, BitmapFont.HAlignment.CENTER);
		//spriteBatch.end();
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

	@Override
	public void setScale(float scale) {
		this.scale = scale;
	}

	private void init(){
		font = new BitmapFont();
		//spriteBatch = new SpriteBatch();
		font.setColor(1.0f, 0.0f, 0.0f, 1.0f);
		font.setScale(scale);
	}
}
