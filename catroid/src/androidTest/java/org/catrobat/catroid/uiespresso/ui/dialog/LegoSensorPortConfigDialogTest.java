
package org.catrobat.catroid.uiespresso.ui.dialog;

import android.support.annotation.StringRes;
import android.support.test.espresso.Espresso;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class LegoSensorPortConfigDialogTest {

  public static final String TAG = "LegoSensorPortConfigDialogTest";

  @Parameters(name = "{4}" + "-Test")
  public static Iterable<Object[]> data(){
    ArrayList<Object[]> data = makeTestList(sensorsEV3(), portsEV3());
    data.addAll(makeTestList(sensorsNXT(), portsNXT()));
    return data;
  }
  public static Iterable<Object[]> sensorsEV3() {
    return Arrays.asList(new Object[][] {
    {R.string.formula_editor_sensor_lego_ev3_sensor_touch, R.string.ev3_sensor_touch, "EV3_touch"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_infrared, R.string.ev3_sensor_infrared, "EV3_infrared"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_color, R.string.ev3_sensor_color ,"EV3_color"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient, R.string.ev3_sensor_color_ambient, "EV3_color_ambient"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected, R.string.ev3_sensor_color_reflected, "EV3_color_reflected"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color, R.string.ev3_sensor_hitechnic_color, "EV3_hitechnic_color"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c, R.string.ev3_sensor_nxt_temperature_c, "EV3_NXT_temperature_°C"},
    {R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f, R.string.ev3_sensor_nxt_temperature_f, "EV3_NXT_temperature_°F"}
    });
  }
  public static Iterable<Object[]> sensorsNXT() {
    return Arrays.asList(new Object[][] {
      {R.string.formula_editor_sensor_lego_nxt_touch, R.string.nxt_sensor_touch, "NXT_touch"},
      {R.string.formula_editor_sensor_lego_nxt_sound, R.string.nxt_sensor_sound, "NXT_sound"},
      {R.string.formula_editor_sensor_lego_nxt_light, R.string.nxt_sensor_light, "NXT_light"},
      {R.string.formula_editor_sensor_lego_nxt_light_active, R.string.nxt_sensor_light_active, "NXT_light_active"},
      {R.string.formula_editor_sensor_lego_nxt_ultrasonic, R.string.nxt_sensor_ultrasonic, "NXT_ultrasonic"}
    });
  }

  public static Iterable<Object[]> portsNXT() {
    return Arrays.asList(new Object[][] {
      { R.string.lego_port_1, R.string.formula_editor_sensor_lego_nxt_1, "Port_1"},
      { R.string.lego_port_2, R.string.formula_editor_sensor_lego_nxt_2, "Port_2"},
      { R.string.lego_port_3, R.string.formula_editor_sensor_lego_nxt_3, "Port_3"},
      { R.string.lego_port_4, R.string.formula_editor_sensor_lego_nxt_4, "Port_4"}
    });
  }
  public static Iterable<Object[]> portsEV3() {
    return Arrays.asList(new Object[][] {
      { R.string.lego_port_1, R.string.formula_editor_sensor_lego_ev3_1, "Port_1"},
      { R.string.lego_port_2, R.string.formula_editor_sensor_lego_ev3_2, "Port_2"},
      { R.string.lego_port_3, R.string.formula_editor_sensor_lego_ev3_3, "Port_3"},
      { R.string.lego_port_4, R.string.formula_editor_sensor_lego_ev3_4, "Port_4"}
    });
  }

  private static Map<Integer, Integer> previousSensorOnPort = new TreeMap<>();

  private String portConfigDialogTitle;
  @Rule
  public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
    BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

  @Parameter
  public @StringRes int formulaEditorSensor;

  @Parameter(1)
  public @StringRes int formulaEditorLegoPort;

  @Parameter(2)
  public @StringRes int formulaEditorLegoPortResult;

  @Parameter(3)
  public @StringRes int legoSensor;
  @Parameter(4)
  public String testName;

  private static Integer whenBrickPosition = 0;
  private static Integer changeSizeBrickPosition = 1;

  @Before
  public void setUp() throws Exception {
    Script script = BrickTestUtils.createProjectAndGetStartScript("LegoSensorPortConfigDialogTest");
    script.addBrick(new ChangeSizeByNBrick(0));
    baseActivityTestRule.launchActivity();
    portConfigDialogTitle = UiTestUtils.getResourcesString(R.string.lego_sensor_port_config_dialog_title);
    if(previousSensorOnPort.isEmpty()) {
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_ev3_1, R.string.ev3_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_ev3_2, R.string.ev3_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_ev3_3, R.string.ev3_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_ev3_4, R.string.ev3_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_nxt_1, R.string.nxt_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_nxt_2, R.string.nxt_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_nxt_3, R.string.nxt_no_sensor);
      previousSensorOnPort.put(R.string.formula_editor_sensor_lego_nxt_4, R.string.nxt_no_sensor);
    }
  }

  @Test
  public void LegoSensorPortConfigDialogTest1() {
    onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
    onBrickAtPosition(changeSizeBrickPosition).checkShowsText(R.string.brick_change_size_by);
    onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
      .perform(click());

    String formulaEditorSensorString = UiTestUtils.getResourcesString(formulaEditorSensor);

    onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.DEVICE);
    onRecyclerView().performOnItemWithText(formulaEditorSensorString, click());


    // OK must be disabled**************************************************************************
    String DialogTitle = UiTestUtils.getResourcesString(R.string.lego_sensor_port_config_dialog_title,
      UiTestUtils.getResourcesString(legoSensor));
    Espresso.onView(withText(DialogTitle)).check(matches(isDisplayed()));
    Espresso.onView(withText(R.string.ok)).perform(click());
    Espresso.onView(withText(DialogTitle)).check(matches(isDisplayed()));
    //**********************************************************************************************

    int previousSensor = previousSensorOnPort.get(formulaEditorLegoPortResult);
    String formulaEditorPortString = UiTestUtils.getResourcesString(formulaEditorLegoPort)
    + ": " + UiTestUtils.getResourcesString(previousSensor);
    previousSensorOnPort.replace(formulaEditorLegoPortResult, legoSensor);
    Log.i(TAG, formulaEditorPortString);
    Espresso.onView(withText(formulaEditorPortString)).perform(click());

    Espresso.onView(withText(R.string.ok)).perform(click());

    Espresso.onView(withText(formulaEditorLegoPortResult));
    onFormulaEditor(); //Check if View is onFormulaEditor
  }
  private static ArrayList<Object[]> makeTestList(Iterable<Object[]> sensorList, Iterable<Object[]> portList)
  {
    ArrayList<Object[]> testList = new ArrayList();
    for(Object[] sensor : sensorList) {
      for(Object[] port :portList) {
        testList.add(
          new Object[]{
            sensor[0], port[0], port[1], sensor[1], sensor[2] + "-" + port[2]
          });
      }
    }
    return testList;
  }
}
