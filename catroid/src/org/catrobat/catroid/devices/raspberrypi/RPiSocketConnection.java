package org.catrobat.catroid.devices.raspberrypi;

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
import java.util.Map;

public class RPiSocketConnection {

    private Socket clientSocket;
    private String RPiVersion;

    private boolean isConnected;
    private OutputStream outToServer;
    private DataOutputStream outStream;
    private BufferedReader reader;
    private ArrayList<Integer> available_GPIOs = new ArrayList<Integer>();
    private String host;
    private int port;
    private int interrupt_receiver_port;
    private Thread receiverThread;
    private Method method;
    private Object obj;

    public void setMethod(Method method, Object obj) {
        this.method = method;
        this.obj = obj;
    }

    // 0 = small GPIO, 1 = big GPIO, 2 = ComputeModule
    private Map<String, Integer> GpioVersionMap = new HashMap<String, Integer>();

    public RPiSocketConnection() {
        initGpioVersionMap();
    }

    public void connect(String host, int port) throws Exception {
        if (isConnected) {
            disconnect();
        }

        this.host = host;
        this.port = port;

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
        if (getGpioRevision() == 2) {
            // TODO: Support Raspberry Compute module!
            disconnect();
            throw new UnsupportedException("This version of the Raspberry Pi is not yet supported...");
        }
        initGpioList(getGpioRevision());
    }

    private void readServerPort() throws Exception {

        String received_line = processCommand("serverport");
        interrupt_receiver_port = Integer.parseInt(received_line.split(" ")[1]);
    }

    private String processCommand(String command) throws IOException, NoConnectionException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }

        System.out.println("Sending:  " + command);

        outStream.write(command.getBytes());
        String received_line = reader.readLine();

        System.out.println("Received: " + received_line);
        System.out.println();

        if (received_line == null || !received_line.startsWith(command.split(" ")[0])) {
            throw new IOException("Error with response");
        }

        return received_line;
    }

    private void appendMessage(String message) {
        if (method == null || obj == null)
            return;

        try {
            method.invoke(obj, message);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            // TODO Auto-generated method stub
            Socket recv = null;
            try {
                recv = new Socket(host, interrupt_receiver_port);
                BufferedReader receive_reader = new BufferedReader(new InputStreamReader(recv.getInputStream()));
                while (!Thread.interrupted()) {
                    String received_line = receive_reader.readLine();
                    if (received_line == null)
                        break;

                    System.out.println("Interrupt: " + received_line);
                    System.out.println();

                    appendMessage(received_line);
                }
                recv.close();
                System.out.println("RPiSocketReceiver closed");
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

    public int getGpioRevision() throws NoConnectionException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }
        return GpioVersionMap.get(RPiVersion);
    }

    public ArrayList<Integer> getAvailableGPIOs() throws NoConnectionException {
        if (!isConnected) {
            throw new NoConnectionException("No active connection!");
        }
        return available_GPIOs;
    }

    public class UnsupportedException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public UnsupportedException(String msg) {
            super(msg);
        }
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

    private void initGpioVersionMap() {
        GpioVersionMap.put("a01041", 1);
        GpioVersionMap.put("a21041", 1);
        GpioVersionMap.put("0013", 1);
        GpioVersionMap.put("0012", 1);
        GpioVersionMap.put("0011", 2);
        GpioVersionMap.put("0010", 1);
        GpioVersionMap.put("000f", 0);
        GpioVersionMap.put("000e", 0);
        GpioVersionMap.put("000d", 0);
        GpioVersionMap.put("0009", 0);
        GpioVersionMap.put("0008", 0);
        GpioVersionMap.put("0007", 0);
        GpioVersionMap.put("0006", 0);
        GpioVersionMap.put("0005", 0);
        GpioVersionMap.put("0004", 0);
        GpioVersionMap.put("0003", 0);
        GpioVersionMap.put("0002", 0);
        GpioVersionMap.put("Beta", 0);
    }

    private void initGpioList(int version) {
        available_GPIOs.clear();
        if (version == 0) { // small GPIO
            available_GPIOs.add(3);
            available_GPIOs.add(5);
            available_GPIOs.add(7);
            available_GPIOs.add(8);
            available_GPIOs.add(10);
            available_GPIOs.add(11);
            available_GPIOs.add(12);
            available_GPIOs.add(13);
            available_GPIOs.add(15);
            available_GPIOs.add(16);
            available_GPIOs.add(18);
            available_GPIOs.add(19);
            available_GPIOs.add(21);
            available_GPIOs.add(22);
            available_GPIOs.add(23);
            available_GPIOs.add(24);
            available_GPIOs.add(26);
        } else if (version == 1) {
            available_GPIOs.add(3);
            available_GPIOs.add(5);
            available_GPIOs.add(7);
            available_GPIOs.add(8);
            available_GPIOs.add(10);
            available_GPIOs.add(11);
            available_GPIOs.add(12);
            available_GPIOs.add(13);
            available_GPIOs.add(15);
            available_GPIOs.add(16);
            available_GPIOs.add(18);
            available_GPIOs.add(19);
            available_GPIOs.add(21);
            available_GPIOs.add(22);
            available_GPIOs.add(23);
            available_GPIOs.add(24);
            available_GPIOs.add(26);
            available_GPIOs.add(29);
            available_GPIOs.add(31);
            available_GPIOs.add(32);
            available_GPIOs.add(33);
            available_GPIOs.add(35);
            available_GPIOs.add(36);
            available_GPIOs.add(37);
            available_GPIOs.add(38);
            available_GPIOs.add(40);
        } else { // legacy mode, try to support if we don't know the version
            // TODO: Support Compute Module
            available_GPIOs.add(3);
            available_GPIOs.add(5);
            available_GPIOs.add(7);
            available_GPIOs.add(8);
            available_GPIOs.add(10);
            available_GPIOs.add(11);
            available_GPIOs.add(12);
            available_GPIOs.add(13);
            available_GPIOs.add(15);
            available_GPIOs.add(16);
            available_GPIOs.add(18);
            available_GPIOs.add(19);
            available_GPIOs.add(21);
            available_GPIOs.add(22);
            available_GPIOs.add(23);
            available_GPIOs.add(24);
            available_GPIOs.add(26);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}