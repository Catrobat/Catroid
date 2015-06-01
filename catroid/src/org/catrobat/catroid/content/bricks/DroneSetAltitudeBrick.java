package org.catrobat.catroid.content.bricks;

import android.util.Log;
import android.view.View;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.ArrayList;
import java.util.List;

public class DroneSetAltitudeBrick extends DroneSpinnerBrick {

    private final ArrayList<String> list;

    public DroneSetAltitudeBrick() {
        list = new ArrayList<>();
    }

    @Override
    protected String getBrickLabel(View view) {
        return view.getResources().getString(R.string.brick_drone_set_altitude);
    }

    @Override
    protected ArrayList<String> getSpinnerItems(View view) {
        list.clear();
        list.add(view.getResources().getString(R.string.drone_set_altitude_3m));
        list.add(view.getResources().getString(R.string.drone_set_altitude_5m));
        list.add(view.getResources().getString(R.string.drone_set_altitude_10m));
        return (ArrayList<String>) list.clone();
    }

    @Override
    public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
        switch (spinnerPosition) {
            case 0:
                sequence.addAction(ExtendedActions.droneSetAltitudeAction(R.string.drone_set_altitude_3m));
                break;
            case 1:
                sequence.addAction(ExtendedActions.droneSetAltitudeAction(R.string.drone_set_altitude_5m));
                break;
            case 2:
                sequence.addAction(ExtendedActions.droneSetAltitudeAction(R.string.drone_set_altitude_10m));
                break;
        }
        return null;
    }
}
