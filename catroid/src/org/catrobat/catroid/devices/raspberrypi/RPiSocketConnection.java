package org.catrobat.catroid.devices.raspberrypi;

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.BroadcastAction;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RPiSocketConnection {

    private Socket clientSocket;
    private String RPiVersion;
    private String host;

    private boolean isConnected;
    private OutputStream outToServer;
    private DataOutputStream outStream;
    private BufferedReader reader;
    private ArrayList<Integer> available_GPIOs;
    private int interrupt_receiver_port;
    private Thread receiverThread;


    public RPiSocketConnection() {
    }

    public void connect(String host, int port) throws Exception {
        if (isConnected) {
            disconnect();
        }

        this.host = host;
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(host, port), 2000); // 2s
        // timeout


        outToServer = clientSocket.getOutputStream();
        outStream = new DataOutputStream(outToServer);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String hello = reader.readLine();

        if(hello.startsWith("quit"))
            throw new NoConnectionException("Server refused to accept our connection!");
        else if(hello.startsWith("hello"));
        {
            isConnected = true;

            respondVersion();
            readServerPort();

            receiverThread = new Thread(new RPiSocketReceiver());
            receiverThread.start();
        }
    }

    public void disconnect() throws IOException {

        if (!isConnected) {
            return;
        }

        try {
            processCommand("quit");
        } catch (NoConnectionException e) {} // impossible

        isConnected = false;
        clientSocket.close();
        receiverThread.interrupt();

    }



    private void respondVersion() throws Exception {

        String received_line = processCommand("rev");

        RPiVersion = received_line.split(" ")[1];

        available_GPIOs = RaspberryPiService.getInstance().getGpioList(RPiVersion);
    }

    private void readServerPort() throws Exception {

        String received_line = processCommand("serverport");
        interrupt_receiver_port = Integer.parseInt(received_line.split(" ")[1]);
    }

    private String processCommand(String command) throws IOException, NoConnectionException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }

        Log.d(getClass().getSimpleName(), "Sending:  " + command);

        outStream.write(command.getBytes());
        String received_line = reader.readLine();

        Log.d(getClass().getSimpleName(), "Received: " + received_line);

        if (received_line == null || !received_line.startsWith(command.split(" ")[0])) {
            throw new IOException("Error with response");
        }

        return received_line;
    }

    private void callEvent(String broadcastMessage) {
        Sprite dummySenderSprite = new Sprite();
        dummySenderSprite.setName("raspi_interrupt_dummy");
        BroadcastAction action = ExtendedActions.broadcast(dummySenderSprite,broadcastMessage);
        action.act(0);


    }

    public void setPin(int pin, boolean value) throws NoConnectionException, IOException, NoGPIOException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }

        if (!available_GPIOs.contains(pin)) {
            throw new NoGPIOException("Pin out of range on this model!");
        }

        short value_short = (short) (value ? 1 : 0);

        String setRequestMessage = "set " + pin + " " + value_short;
        String received_line = processCommand(setRequestMessage);
        String[] tokens = received_line.split(" ");

        if (tokens.length != 3) {
            throw new IOException("setRequest: Error with response");
        }
    }

    public boolean getPin(int pin) throws NoConnectionException, IOException, NoGPIOException {

        if (!available_GPIOs.contains(pin)) {
            throw new NoGPIOException("Pin out of range on this model!");
        }

        String readRequestMsg = "read " + pin;
        String received_line = processCommand(readRequestMsg);
        String[] tokens = received_line.split(" ");

        if (tokens.length != 3) {
            throw new IOException("readRequest: Error with response");
        }

        if (tokens[2].equals("1")) {
            return true;
        } else if (tokens[2].equals("0")) {
            return false;
        } else {
            throw new IOException("readRequest: Error with response");
        }

    }

    public void activatePinInterrupt(int pin) throws NoConnectionException, IOException, NoGPIOException {
        if (!available_GPIOs.contains(pin)) {
            throw new NoGPIOException("Pin out of range on this model!");
        }

        String readRequestMsg = "readint " + pin;
        String received_line = processCommand(readRequestMsg);
        String[] tokens = received_line.split(" ");

        if (tokens.length != 3) {
            throw new IOException("readRequest: Error with response");
        }
    }

    public void setPWM(int pin, double d, double e) throws NoConnectionException, IOException {

        // TODO: check if pin is PWM enabled
        String pwmRequestMessage = "pwm " + pin + " " + Math.round(d * 100) / 100.0d + " "
                + Math.round(e * 100) / 100.0d;
        String received_line = processCommand(pwmRequestMessage);
        // TODO
    }


    private class RPiSocketReceiver implements Runnable {

        @Override
        public void run() {
            Socket recv = null;
            try {
                recv = new Socket(host, interrupt_receiver_port);
                BufferedReader receive_reader = new BufferedReader(new InputStreamReader(recv.getInputStream()));
                while (!Thread.interrupted()) {
                    String received_line = receive_reader.readLine();
                    if (received_line == null)
                        break;

                    Log.d(getClass().getSimpleName(), "Interrupt: " + received_line);

                    callEvent(Constants.RASPI_BROADCAST_PREFIX + received_line);
                }
                recv.close();
                Log.d(getClass().getSimpleName(), "RPiSocketReceiver closed");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    public String getVersion() throws NoConnectionException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }

        return RPiVersion;
    }

    public ArrayList<Integer> getAvailableGPIOs() throws NoConnectionException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }
        return available_GPIOs;
    }

    public class NoGPIOException extends Exception {

        private static final long serialVersionUID = 1L;

        public NoGPIOException(String msg) {
            super(msg);
        }
    }

    public class NoConnectionException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public NoConnectionException(String msg) {
            super(msg);
        }
    }


    public boolean isConnected() {
        return isConnected;
    }
}