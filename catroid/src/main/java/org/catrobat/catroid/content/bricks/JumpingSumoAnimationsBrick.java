package org.catrobat.catroid.content.bricks;

import android.view.View;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

/**
 * Created by chris on 13.09.16.
 */
public class JumpingSumoAnimationsBrick extends JumpingSumoBasicBrick{

    private static final long serialVersionUID = 1L;

    @Override
    public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createJumpingSumoAnimationAction());
        return null;
    }

    @Override
    protected String getBrickLabel(View view) {
        return view.getResources().getString(R.string.brick_jumping_sumo_animation);
    }
}
