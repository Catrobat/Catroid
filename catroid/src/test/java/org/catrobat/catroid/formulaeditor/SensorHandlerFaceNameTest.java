package org.catrobat.catroid.formulaeditor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The face name sensor.
 *
 * The original code kept this value in sensorValueMap, an instance field.
 * destroy() sets instance to null and the map goes with it, so the name was
 * stored and then silently deleted, and the sensor returned the number 0.
 *
 * survivesDestroy is the test for that. It is why the value has to live in a
 * static field.
 */
@RunWith(RobolectricTestRunner.class)
public class SensorHandlerFaceNameTest {

	@Before
	public void setUp() {
		SensorHandler.setFaceNameRecognitionResult("Unknown");
	}

	@After
	public void tearDown() {
		SensorHandler.setFaceNameRecognitionResult("Unknown");
	}

	// ---------------- Storing ----------------

	@Test
	public void aNameIsStoredAndReadBack() {
		SensorHandler.setFaceNameRecognitionResult("salah");
		assertEquals("salah",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void aLaterNameReplacesTheEarlierOne() {
		SensorHandler.setFaceNameRecognitionResult("salah");
		SensorHandler.setFaceNameRecognitionResult("karim");
		assertEquals("karim",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void nullBecomesUnknown() {
		SensorHandler.setFaceNameRecognitionResult(null);
		assertEquals("Unknown",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void anEmptyNameBecomesUnknown() {
		SensorHandler.setFaceNameRecognitionResult("");
		assertEquals("Unknown",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void whitespaceOnlyBecomesUnknown() {
		SensorHandler.setFaceNameRecognitionResult("    ");
		assertEquals("Unknown",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void surroundingSpacesAreTrimmed() {
		SensorHandler.setFaceNameRecognitionResult("  salah  ");
		assertEquals("salah",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void namesWithSpacesInsideAreKept() {
		SensorHandler.setFaceNameRecognitionResult("Kazi Jahid");
		assertEquals("Kazi Jahid",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	// ---------------- The bug ----------------

	@Test
	public void theNameSurvivesDestroy() {
		SensorHandler.setFaceNameRecognitionResult("salah");
		SensorHandler.destroy();

		assertEquals("destroy() must not delete the detected name",
				"salah", SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void theNameCanBeSetAfterDestroy() {
		SensorHandler.destroy();
		SensorHandler.setFaceNameRecognitionResult("salah");
		assertEquals("salah",
				SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	@Test
	public void readingAfterDestroyDoesNotThrow() {
		SensorHandler.destroy();
		assertNotNull(SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
	}

	// ---------------- Type ----------------

	@Test
	public void theSensorReturnsTextNotANumber() {
		SensorHandler.setFaceNameRecognitionResult("salah");
		Object value = SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition);

		assertTrue("a name must come back as text, a Double here means the value "
						+ "was lost and getOrDefault returned 0.0",
				value instanceof String);
	}

	@Test
	public void anUnsetSensorStillReturnsText() {
		Object value = SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition);
		assertTrue(value instanceof String);
		assertFalse(((String) value).isEmpty());
	}

	@Test
	public void readingManyTimesIsStable() {
		SensorHandler.setFaceNameRecognitionResult("salah");
		for (int i = 0; i < 100; i++) {
			assertEquals("salah",
					SensorHandler.getSensorValue(Sensors.On_Device_Face_Recognition));
		}
	}
}
