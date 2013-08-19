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
package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.Command;
import org.catrobat.catroid.io.Command.commandType;
import org.catrobat.catroid.io.Confirmation;
import org.catrobat.catroid.io.Confirmation.ConfirmationState;
import org.catrobat.catroid.io.Connection;
import org.catrobat.catroid.io.CustomKeyboard;
import org.catrobat.catroid.io.PcConnectionManager;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.SimulatedSocket;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import java.util.ArrayList;

public class SendToPcConnectionTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private PcConnectionManager connectionManager;
	private SimulatedSocket simulatedSocket;
	private ObjectInputStream is;
	private Connection connection;

	public SendToPcConnectionTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (connection != null) {
			connection.stopThread();
		}
		super.tearDown();
		UiTestUtils.clearProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
	}

	private byte[] receivedObject = new byte[1000];

	public void testSetUpConnectionSuccess() throws InterruptedException, IOException, ClassNotFoundException {

		String ip = "192.163.27.3";
		Object[] toSend = new Object[2];
		int startRegistration = 1;
		Confirmation confirmation = new Confirmation(ConfirmationState.LEGAL_VERSION_ID);
		confirmation.setVersionId(PcConnectionManager.getInstance(null).getServerVersionId());
		toSend[0] = startRegistration;
		toSend[1] = confirmation;
		setUpMocks(toSend);

		Object answer = startConnection(ip);

		assertTrue("Invalid incoming object to start syncronisation.", answer instanceof Integer);
		assertTrue("Invalid incoming param to start syncronisation.", startRegistration == 1);
		assertTrue("ConnectionThread didn't start.", connection.isAlive());
	}

	public void testSetUpConnectionFail() throws InterruptedException, IOException, ClassNotFoundException {

		String ip = "192.163.27.3";
		Object[] toSend = new Object[2];
		int startRegistration = 1;
		Confirmation confirmation = new Confirmation(ConfirmationState.ILLEGAL_VERSION_ID);
		confirmation.setVersionId(PcConnectionManager.getInstance(null).getServerVersionId() + 1);
		toSend[0] = startRegistration;
		toSend[1] = confirmation;
		setUpMocks(toSend);
		Object answer = startConnection(ip);
		assertTrue("Invalid incoming object to start syncronisation.", answer instanceof Integer);
		assertTrue("Invalid incoming param to start syncronisation.", startRegistration == 1);
		assertTrue("ConnectionThread shouldn't have started.", !connection.isAlive());

	}

	public void testSendCommand() throws SocketException, IOException, InterruptedException, ClassNotFoundException {

		String ip = "192.163.27.3";
		Object[] toSend = new Object[3];
		int startRegistration = 1;
		toSend[0] = startRegistration;
		toSend[1] = new Confirmation(ConfirmationState.LEGAL_VERSION_ID);
		toSend[2] = new Confirmation(ConfirmationState.COMMAND_SEND_SUCCESSFULL);
		setUpMocks(toSend);
		startConnection(ip);
		assertTrue("ConnectionThread didn't start.", connection.isAlive());

		Command command = new Command(CustomKeyboard.keyControl, commandType.SINGLE_KEY);
		ArrayList<Command> commandList = new ArrayList<Command>();
		commandList.add(command);
		Reflection.setPrivateField(connection, "commandList", commandList);
		Thread.sleep(2000);

		ByteArrayInputStream in = new ByteArrayInputStream(receivedObject);
		if (is == null) {
			is = new ObjectInputStream(in);
		}
		Object answer = deserialize(is);
		assertTrue("Sent command was not of type command.", answer instanceof Command);
		assertTrue("Command was changed.", ((Command) answer).getKey() == command.getKey());
		assertTrue("ConnectionThread should still be alive.", connection.isAlive());
	}

	public void testWhenConnectionBreaks() throws Exception {

		String ip = "192.163.27.3";
		Object[] toSend = new Object[1];
		int startRegistration = 1;
		toSend[0] = startRegistration;
		setUpMocks(toSend);

		StageActivity stageActivity = new StageActivity();
		Reflection.setPrivateField(connectionManager, "stageActivity", stageActivity);
		Command command = new Command(CustomKeyboard.keyControl, commandType.SINGLE_KEY);
		ArrayList<Command> commandList = new ArrayList<Command>();
		commandList.add(command);

		startConnection(ip);
		assertTrue("ConnectionThread didn't start.", connection.isAlive());
		Reflection.setPrivateField(connection, "commandList", commandList);

		Thread.sleep(1000);

		assertTrue("ConnectionThread should still be alive.",
				Reflection.getPrivateField(connection, "errorDialogOnScreen").equals(true));
		assertTrue("ConnectionThread shouldn't still be alive.", !connection.isAlive());
	}

	public void startProject() {
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
	}

	public void setUpMocks(Object[] toSend) throws SocketException, IOException {

		connectionManager = PcConnectionManager.getInstance(null);
		simulatedSocket = new SimulatedSocket(serialize(toSend), new OutputStream() {

			private int byteCount = 0;

			@Override
			public void write(int b) throws IOException {
				receivedObject[byteCount] = (byte) b;
				byteCount++;
			}
		});
	}

	public Object startConnection(String ip) throws InterruptedException, StreamCorruptedException, IOException,
			ClassNotFoundException {
		connection = new Connection(ip, connectionManager, "testServer");
		Reflection.setPrivateField(connection, "client", simulatedSocket);
		connection.start();
		Thread.sleep(1000);

		ByteArrayInputStream in = new ByteArrayInputStream(receivedObject);
		if (is == null) {
			is = new ObjectInputStream(in);
		}

		return deserialize(is);
	}

	private InputStream serialize(Object[] object) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int size = object.length;
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		for (int i = 0; i < size; i++) {
			objectOutputStream.writeObject(object[i]);
		}
		objectOutputStream.flush();
		objectOutputStream.close();
		InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		return inputStream;
	}

	private Object deserialize(Object data) throws IOException, ClassNotFoundException {

		if (data instanceof byte[]) {
			ByteArrayInputStream in = new ByteArrayInputStream((byte[]) data);
			if (is == null) {
				is = new ObjectInputStream(in);
			}
		} else {
			is = (ObjectInputStream) data;
		}
		return is.readObject();
	}
}
