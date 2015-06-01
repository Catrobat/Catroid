package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.drone.DroneConfigManager;

/**
 * Created by marc on 01.06.2015.
 */
public class DroneSetAltitudeAction extends TemporalAction {
    private int ressourceID;

    @Override
    protected void update(float percent) {
    }

    @Override
    protected void begin() {

        switch (ressourceID) {
            case R.string.drone_set_altitude_3m:
                DroneConfigManager.getInstance().setAltitude(3);
                break;
            case R.string.drone_set_altitude_5m:
                DroneConfigManager.getInstance().setAltitude(5);
                break;
            case R.string.drone_set_altitude_10m:
                DroneConfigManager.getInstance().setAltitude(10);
                break;
        }
    }

    public void setRessourceID(int ressourceID) {
        this.ressourceID = ressourceID;
    }
}
