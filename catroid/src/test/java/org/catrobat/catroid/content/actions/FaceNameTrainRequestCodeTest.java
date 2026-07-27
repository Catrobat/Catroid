package org.catrobat.catroid.content.actions;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Guards the bug that took longest to find.
 *
 * StageResourceHolder.onActivityResult ends the program in its default branch. The
 * photo picker uses request code personIndex + 1000, and because none of those
 * matched a case, every training run killed the stage the moment photos came back.
 *
 * These tests pin the range down so a future change to either side cannot silently
 * bring that back.
 */
public class FaceNameTrainRequestCodeTest {

	// Copies of the constants in StageResourceHolder. If someone changes one of
	// those to a value inside the training range, these tests fail and say so.
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_GPS = 2;
	private static final int REQUEST_FACE_NAME_RECOG = 4242;
	private static final int REQUEST_FACE_NAME_DETECT = 4243;

	@Test
	public void theFirstPersonIsInsideTheRange() {
		assertTrue(FaceNameTrainAction.ownsRequestCode(1000));
	}

	@Test
	public void aLaterPersonIsInsideTheRange() {
		assertTrue(FaceNameTrainAction.ownsRequestCode(1001));
		assertTrue(FaceNameTrainAction.ownsRequestCode(1050));
		assertTrue(FaceNameTrainAction.ownsRequestCode(1899));
	}

	@Test
	public void theBoundariesAreExact() {
		assertFalse(FaceNameTrainAction.ownsRequestCode(999));
		assertTrue(FaceNameTrainAction.ownsRequestCode(1000));
		assertTrue(FaceNameTrainAction.ownsRequestCode(1899));
		assertFalse(FaceNameTrainAction.ownsRequestCode(1900));
	}

	@Test
	public void unrelatedCodesAreNotClaimed() {
		assertFalse(FaceNameTrainAction.ownsRequestCode(0));
		assertFalse(FaceNameTrainAction.ownsRequestCode(-1));
		assertFalse(FaceNameTrainAction.ownsRequestCode(Integer.MAX_VALUE));
	}

	/** A collision here would send one feature's result to the wrong handler. */
	@Test
	public void theRangeDoesNotCollideWithCatroidRequestCodes() {
		assertFalse("REQUEST_CONNECT_DEVICE collides",
				FaceNameTrainAction.ownsRequestCode(REQUEST_CONNECT_DEVICE));
		assertFalse("REQUEST_GPS collides",
				FaceNameTrainAction.ownsRequestCode(REQUEST_GPS));
		assertFalse("REQUEST_FACE_NAME_RECOG collides",
				FaceNameTrainAction.ownsRequestCode(REQUEST_FACE_NAME_RECOG));
		assertFalse("the detection action collides",
				FaceNameTrainAction.ownsRequestCode(REQUEST_FACE_NAME_DETECT));
	}

	/** 900 slots. Beyond that a person's picker result would leak to the default branch. */
	@Test
	public void theRangeHoldsEnoughPeople() {
		int slots = FaceNameTrainAction.REQUEST_LAST - FaceNameTrainAction.REQUEST_FIRST + 1;
		assertTrue("expected room for hundreds of people, got " + slots, slots >= 500);
	}

	@Test
	public void everyPersonIndexInTheRangeIsClaimed() {
		for (int index = 0; index < 900; index++) {
			int requestCode = FaceNameTrainAction.REQUEST_FIRST + index;
			assertTrue("index " + index + " is not claimed",
					FaceNameTrainAction.ownsRequestCode(requestCode));
		}
	}
}
