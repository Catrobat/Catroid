/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;
import android.widget.ListView;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.LEDBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.SensorServerUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

/**
 * @author BerndBaumann
 *
 * To successfully execute this tests you need to make sure they are running on
 * an actual phone (not a virtual device), with a led flash next to the back camera.
 * This phone must be connected to a WLAN access point to connect to the
 * server and request its sensor values.
 */
public class LEDBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

    private static final String LOG_LEDTEST = "LEDBrickTest::";

    private static final int LED_DELAY = 8000; // 8 seconds
    private static final int WLAN_DELAY = 500; // .5 secons

    private LEDBrick ledBrick;
    private Project project;


    public LEDBrickTest() {
        super(ScriptActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        createProject();
        super.setUp();

        // create server connection
        SensorServerUtils.connectToArduinoServer();

        // disable touch screen while testing
        setActivityInitialTouchMode(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        SensorServerUtils.closeConnection();

        setActivityInitialTouchMode(true);
    }

    public void testLedBrick() {
        ListView dragDropListView = UiTestUtils.getScriptListView(solo);
        BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

        int childrenCount = adapter.getChildCountFromLastGroup();
        int groupCount = adapter.getScriptCount();

        assertEquals( "Incorrect number of bricks.", 2, dragDropListView.getChildCount() );
        assertEquals( "Incorrect number of bricks.", 1, childrenCount );

        ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
        assertEquals( "Incorrect number of bricks", 1, projectBrickList.size() );

        assertEquals( "Wrong brick instance", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0) );
        assertNotNull( "TextView does not exist.", solo.getText(solo.getString(R.string.brick_led)));

        UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_led_edit_text, SensorServerUtils.SET_LED_ON_VALUE,
                "lightValue", ledBrick);

        Log.d(LOG_LEDTEST, "LED value set to " + SensorServerUtils.SET_LED_ON_VALUE);

        // executing the script should turn on the LED
        UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
        solo.waitForActivity(StageActivity.class.getSimpleName());

        try {
            // wait a long time, then check the sensor value weather the light is really on
            Thread.sleep(LED_DELAY);
            Log.d(LOG_LEDTEST, "checking sensor value");
            SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_ON_VALUE);
            Thread.sleep(WLAN_DELAY);
            SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_ON_VALUE);
            Thread.sleep(WLAN_DELAY);
            SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_ON_VALUE);
            Thread.sleep(WLAN_DELAY);
        } catch (Exception e) {
            Log.e(LOG_LEDTEST, e.getMessage());
        }

        Log.d(LOG_LEDTEST, "pause StageActivity - this should turn off the led");
        solo.goBack();

        // pausing the activity should turn the light off. again, check the sensor value
        try {
            Thread.sleep(LED_DELAY);
            Log.d(LOG_LEDTEST, "checking sensor value");
            SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_OFF_VALUE);
            Thread.sleep(WLAN_DELAY);
            SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_OFF_VALUE);
            Thread.sleep(WLAN_DELAY);
            SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_OFF_VALUE);
            Thread.sleep(WLAN_DELAY);
        } catch (Exception e) {
            Log.e(LOG_LEDTEST, e.getMessage());
        }

        Log.d(LOG_LEDTEST, "testLedBrick() finished");
    }

    private void createProject () {
        project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
        Sprite sprite = new Sprite("cat");
        Script script = new StartScript(sprite);

        ledBrick = new LEDBrick(sprite);

        script.addBrick(ledBrick);
        sprite.addScript(script);
        project.addSprite(sprite);

        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
    }

}
