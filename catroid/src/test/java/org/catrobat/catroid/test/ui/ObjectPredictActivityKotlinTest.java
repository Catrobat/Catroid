package org.catrobat.catroid.test.ui;

import static org.junit.Assert.*;

import org.catrobat.catroid.ObjectTrainAndRecognition.ui.ObjectPredictActivityKotlin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class ObjectPredictActivityKotlinTest {

    @Test
    public void lifecycle_runsWithoutCrash() {
        ActivityController<ObjectPredictActivityKotlin> ctl =
                Robolectric.buildActivity(ObjectPredictActivityKotlin.class).create().start().resume().visible();
        assertNotNull(ctl.get());
        ctl.pause().stop().destroy();
    }
}