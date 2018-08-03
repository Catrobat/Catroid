package org.catrobat.catroid.uiespresso.ui.dialog.utils;

import android.support.test.espresso.Espresso;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;

public final class LegoSensorPortConfigDialogWrapper{
  private static String legoSensorConfigDialogTitle;

  private final int[] FORMULA_EDITOR_LEGO_PORTS =
    { R.string.lego_port_1,  R.string.lego_port_2,
    R.string.lego_port_3,  R.string.lego_port_4 };
  public static final Matcher<View> FORMULA_EDITOR_TEXT_FIELD_MATCHER = withId(R.id.formula_editor_edit_field);

  private LegoSensorPortConfigDialogWrapper() {
    onView(withText(legoSensorConfigDialogTitle)).check(matches(isDisplayed()));
  }

  public static LegoSensorPortConfigDialogWrapper onLegoSensorPortConfigDialog(String legoSensor) {
    legoSensorConfigDialogTitle = UiTestUtils.getResourcesString(R.string.lego_sensor_port_config_dialog_title,
      legoSensor);
    return new LegoSensorPortConfigDialogWrapper();
  }

  public static LegoSensorPortConfigDialogWrapper onLegoSensorPortConfigDialog(int legoSensor) {
    String sensor = UiTestUtils.getResourcesString(legoSensor);
    return onLegoSensorPortConfigDialog(sensor);
  }

  public void performClickOn(Matcher<View> matcher) {
    onView(matcher)
      .perform(click());
  }

  public void performClickOnOK() {
    onView(withText(R.string.ok)).perform(click());
  }

  public void performClickOnPort(int number, String previousSensor) {
    assertPortNumber(number);

    String formulaEditorPortString =
      UiTestUtils.getResourcesString(FORMULA_EDITOR_LEGO_PORTS[number - 1]) + ": " + previousSensor;
    onView(withText(formulaEditorPortString)).perform(click());
  }

  public void performClickOnPort(int number, int previousSensor) {
    String sensor = UiTestUtils.getResourcesString(previousSensor);
    performClickOnPort(number, sensor);
  }

  public void checkPortDisplayed(int number, String previousSensor) {
    assertPortNumber(number);
    String formulaEditorPortString =
      UiTestUtils.getResourcesString(FORMULA_EDITOR_LEGO_PORTS[number - 1]) + ": " + previousSensor;
    onView(withText(formulaEditorPortString)).check(matches(isDisplayed()));
  }

  public void checkPortDisplayed(int number, int previousSensor) {
    String sensor = UiTestUtils.getResourcesString(previousSensor);
    checkPortDisplayed(number, sensor);
  }

  private void assertPortNumber(int number) {
    assertTrue("Port Number must be <= 4", number < 5);
    assertTrue("Port Number must be >= 1", number > 0);
  }
}
