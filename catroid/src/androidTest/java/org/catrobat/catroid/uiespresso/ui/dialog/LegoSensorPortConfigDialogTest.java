
package org.catrobat.catroid.uiespresso.ui.dialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;
import static org.catrobat.catroid.uiespresso.ui.dialog.utils.LegoSensorPortConfigDialogWrapper.onLegoSensorPortConfigDialog;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

@Category({Cat.AppUi.class, Level.Smoke.class})
public class LegoSensorPortConfigDialogTest {

  @Rule
  public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
    BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

  public @StringRes int formulaEditorSensor = R.string.formula_editor_sensor_lego_nxt_touch;
  public @StringRes int formulaEditorLegoPortResult = R.string.formula_editor_sensor_lego_nxt_1;
  public @StringRes int legoSensor = R.string.nxt_sensor_touch;

  private static Integer changeSizeBrickPosition = 1;

  private NXTSensor.Sensor[] sensorMapping;
  private boolean legoNXTBrickSetting;

  @Before
  public void setUp() throws Exception {
    Script script = BrickTestUtils
      .createProjectAndGetStartScript("LegoSensorPortConfigDialogTest");
    script.addBrick(new ChangeSizeByNBrick(0));

    legoNXTBrickSetting = getNXTBrickSetting();
    setNXTBrickSetting(true);

    sensorMapping = SettingsFragment.getLegoNXTSensorMapping(InstrumentationRegistry.getTargetContext());

    SettingsFragment.setLegoMindstormsNXTSensorMapping(InstrumentationRegistry.getTargetContext(),
      new NXTSensor.Sensor[] { NXTSensor.Sensor.NO_SENSOR, NXTSensor.Sensor.NO_SENSOR,
          NXTSensor.Sensor.NO_SENSOR, NXTSensor.Sensor.NO_SENSOR});

    baseActivityTestRule.launchActivity();

    onBrickAtPosition(changeSizeBrickPosition).onChildView(withId(R.id.brick_change_size_by_edit_text))
      .perform(click());

    String formulaEditorSensorString = UiTestUtils.getResourcesString(formulaEditorSensor);
    onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.DEVICE);
    onRecyclerView().performOnItemWithText(formulaEditorSensorString, click());
  }

  @Test
  public void pressOKTest() {
    onLegoSensorPortConfigDialog(legoSensor)
      .performClickOnOK();
    onLegoSensorPortConfigDialog(legoSensor);
  }

  @Test
  public void checkDialogTest() {
    onLegoSensorPortConfigDialog(legoSensor)
      .performClickOnPort(1, R.string.nxt_no_sensor);
    onLegoSensorPortConfigDialog(legoSensor).performClickOnOK();
    Espresso.onView(withText(formulaEditorLegoPortResult));

    onFormulaEditor().performOpenCategory(FormulaEditorWrapper.Category.DEVICE);
    String formulaEditorSensorString = UiTestUtils.getResourcesString(formulaEditorSensor);
    onRecyclerView().performOnItemWithText(formulaEditorSensorString, click());

    onLegoSensorPortConfigDialog(legoSensor)
      .checkPortDisplayed(1, legoSensor);
  }

  @After
  public void tearDown() {
    SettingsFragment.setLegoMindstormsNXTSensorMapping(InstrumentationRegistry.getTargetContext(),
      sensorMapping);
    setNXTBrickSetting(legoNXTBrickSetting);
  }

  private void setNXTBrickSetting(boolean bricksEnabled) {
    SharedPreferences.Editor editor = PreferenceManager
      .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();
    editor.putBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, bricksEnabled)
      .commit();
  }

  private boolean getNXTBrickSetting(){
    SharedPreferences sharedPreferences = PreferenceManager
      .getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
    return sharedPreferences.
      getBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_ENABLED, false);
  }
}
