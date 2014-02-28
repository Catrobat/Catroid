package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;
import android.widget.ListView;
import junit.framework.AssertionFailedError;
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
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;

/**
 * Created by bernd on 2/28/14.
 */
public class LEDBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

    private static final String LOG_LEDTEST = "LEDBrickTest::";

    // fields to provide ethernet connection to the arduino server
    private Socket clientSocket = null;
    private DataOutputStream sendToServer;
    private BufferedReader recvFromServer;
    private static final String serverIP = "129.27.202.103";
    private static final int serverPort = 6789;
    private static final int getLightValueID = 2;

    private static final int SET_LED_ON_VALUE = 1;
    private static final int SET_LED_OFF_VALUE = 0;

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
        connectToArduinoServer();

        // disable touch screen while testing
        setActivityInitialTouchMode(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        if (clientSocket != null)
            clientSocket.close();
        clientSocket = null;
        sendToServer = null;
        recvFromServer = null;

        setActivityInitialTouchMode(true);
    }

    public void testLedBrick() {
        Log.d(LOG_LEDTEST, "testLedBrick() started");
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

        Log.d(LOG_LEDTEST, "test script initialized");

        UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_led_edit_text, SET_LED_ON_VALUE,
                "lightValue", ledBrick);

        Log.d(LOG_LEDTEST, "LED value set to " + SET_LED_ON_VALUE);

        UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
        solo.waitForActivity(StageActivity.class.getSimpleName());

        Log.d(LOG_LEDTEST, "StageActivity started");

        try {
            Thread.sleep(8000);
            Log.d(LOG_LEDTEST, "checking sensor value");
            checkSensorValue(SET_LED_ON_VALUE);
            Thread.sleep(500);
            checkSensorValue(SET_LED_ON_VALUE);
            Thread.sleep(500);
            checkSensorValue(SET_LED_ON_VALUE);
            Thread.sleep(500);
        } catch (Exception e) {
            Log.e(LOG_LEDTEST, e.getMessage());
        }

        Log.d(LOG_LEDTEST, "pause StageActivity - this should turn off the led");
        solo.goBack();

        try {
            Thread.sleep(8000);
            Log.d(LOG_LEDTEST, "checking sensor value");
            checkSensorValue(SET_LED_OFF_VALUE);
            Thread.sleep(500);
            checkSensorValue(SET_LED_OFF_VALUE);
            Thread.sleep(500);
            checkSensorValue(SET_LED_OFF_VALUE);
            Thread.sleep(500);
        } catch (Exception e) {
            Log.e(LOG_LEDTEST, e.getMessage());
        }
        Log.d(LOG_LEDTEST, "testLedBrick() finished");
    }

    private void connectToArduinoServer() throws IOException {
        Log.d(LOG_LEDTEST, "Trying to connect to server...");

        clientSocket = new Socket( serverIP, serverPort );
        clientSocket.setKeepAlive(true);

        Log.d(LOG_LEDTEST, "Connected to: " + serverIP + " on port " + serverPort);
        sendToServer = new DataOutputStream( clientSocket.getOutputStream() );
        recvFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
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

    private void checkSensorValue( int expected ) {

        char expectedChar;
        String assertString;
        String response;
        if ( expected == SET_LED_ON_VALUE ) {
            expectedChar = '1';
            assertString = "Error: LED is turned off!";
        } else {
            expectedChar = '0';
            assertString = "Error: LED is turned on!";
        }
        try {
            clientSocket.close();
            Thread.sleep(500);
            connectToArduinoServer();
            Log.d(LOG_LEDTEST, "requesting sensor value: ");
            sendToServer.writeByte(Integer.toHexString(getLightValueID).charAt(0));
            sendToServer.flush();
            Thread.sleep(500);
            response = recvFromServer.readLine();
            Log.d(LOG_LEDTEST, "response received! " + response);

            assertFalse("Wrong Command!", response.contains("ERROR"));
            assertTrue( "Wrong data received!", response.contains( "LIGHT_END" ) );
            assertTrue( assertString, response.charAt(0) == expectedChar );

        } catch ( IOException ioe ) {
            throw new AssertionFailedError( "Data exchange failed! Check server connection!" );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
