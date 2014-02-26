package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
//import org.catrobat.catroid.utils.LEDUtil;

/**
 * Created by bernd on 2/20/14.
 */
public class LEDAction extends TemporalAction {

    private Sprite sprite;
    private Formula ledValue;

    @Override
    protected void update(float percent) {
        //LEDUtil.setLEDValue(this.ledValue.interpretBoolean(sprite));
    }

    public void setLedValue(Formula ledValue) {
        this.ledValue = ledValue;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
}
