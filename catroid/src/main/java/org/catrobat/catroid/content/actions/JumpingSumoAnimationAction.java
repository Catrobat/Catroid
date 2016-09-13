package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.parrot.arsdk.arcommands.ARCOMMANDS_JUMPINGSUMO_ANIMATIONS_SIMPLEANIMATION_ID_ENUM;
import com.parrot.arsdk.arcontroller.ARDeviceController;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.drone.JumpingSumoDeviceController;


public class JumpingSumoAnimationAction extends TemporalAction {
      private ARDeviceController deviceController;
      private JumpingSumoDeviceController controller;
      private static final String TAG = JumpingSumoAnimationAction.class.getSimpleName();

      @Override
      protected void begin() {
          super.begin();
          controller = JumpingSumoDeviceController.getInstance();
          deviceController = controller.getDeviceController();
          if (deviceController != null) {
              deviceController.getFeatureJumpingSumo().sendAnimationsSimpleAnimation(ARCOMMANDS_JUMPINGSUMO_ANIMATIONS_SIMPLEANIMATION_ID_ENUM.ARCOMMANDS_JUMPINGSUMO_ANIMATIONS_SIMPLEANIMATION_ID_SLALOM);
              Log.d(TAG, "send animation command JS down");
          } else {
              Log.d(TAG, "error: send animaton command JS");
          }
      }
      @Override
      protected void update(float percent) {
          //Nothing to do
      }
}
