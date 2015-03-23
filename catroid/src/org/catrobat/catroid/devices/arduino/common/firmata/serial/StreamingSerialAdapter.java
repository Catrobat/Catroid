package org.catrobat.catroid.devices.arduino.common.firmata.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adapts InputStream and OutputStream to be used as ISerial
 */
public class StreamingSerialAdapter implements ISerial {

    private transient InputStream inStream;
    private transient OutputStream outStream;

    public InputStream getInStream() {
        return inStream;
    }

    public void setInStream(InputStream inStream) {
        this.inStream = inStream;
    }

    public OutputStream getOutStream() {
        return outStream;
    }

    public void setOutStream(OutputStream outStream) {
        this.outStream = outStream;
    }

    public StreamingSerialAdapter() {
        // InputStream and OutputStream should be set before start()
    }

    public StreamingSerialAdapter(InputStream inStream, OutputStream outStream) {
        this();
        setInStream(inStream);
        setOutStream(outStream);
    }

    private List<ISerialListener> listeners = new ArrayList<ISerialListener>();

    public void addListener(ISerialListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ISerialListener listener) {
        listeners.remove(listener);
    }

    private ReadingThread thread;

    private AtomicBoolean shouldStop = new AtomicBoolean();

    /**
     * Threads, that reads InputStream
     */
    private class ReadingThread extends Thread implements Thread.UncaughtExceptionHandler{

        public ReadingThread() {
            setUncaughtExceptionHandler(this);
        }

        public void uncaughtException(Thread t, Throwable e) {
            handleException(e);
        }

        private void handleException(Throwable e) {
            if (!shouldStop.get())
                for (ISerialListener eachListener : listeners)
                    eachListener.onException(e);
        }

        @Override
        public void run() {
            while (!shouldStop.get()) {
                try {
                    if (inStream.available() > 0)
                        for (ISerialListener eachListener : listeners)
                            eachListener.onDataReceived(StreamingSerialAdapter.this);
                } catch (IOException e) {
                    handleException(e);
                    break;
                }
            }

            try {
                inStream.close();
            } catch (IOException e) {}
        }
    }
    
    public void start() throws SerialException {
        if (thread != null)
            return;

        thread = new ReadingThread();
        shouldStop.set(false);
        thread.start();
    }

    public void stop() throws SerialException {
        if (thread == null)
            return;

        setStopReading();
        thread = null;
        try {
            outStream.close();
        } catch (IOException e) {}
    }

    protected void setStopReading() {
        shouldStop.set(true);
    }

    public boolean isStopping() {
        return shouldStop.get();
    }

    public int available() throws SerialException {
        try {
            return inStream.available();
        } catch (IOException e) {
            return checkIsStoppingOrThrow(e, 0);
        }
    }

    public void clear() throws SerialException {
        try {
            inStream.reset();
        } catch (IOException e) {
            checkIsStoppingOrThrow(e);
        }
    }

    private void checkIsStoppingOrThrow(IOException e) throws SerialException {
        checkIsStoppingOrThrow(e, 0);
    }

    private int checkIsStoppingOrThrow(IOException e, int value) throws SerialException {
        if (shouldStop.get()) {
            return value;
        } else {
            throw new SerialException(e);
        }
    }

    public int read() throws SerialException {
        try {
            return inStream.read();
        } catch (IOException e) {
            return checkIsStoppingOrThrow(e, -1);
        }
    }

    public void write(int outcomingByte) throws SerialException {
        try {
            outStream.write(outcomingByte);
        } catch (IOException e) {
            checkIsStoppingOrThrow(e);
        }
    }

    public void write(byte[] outcomingBytes) throws SerialException {
        try {
            outStream.write(outcomingBytes);
        } catch (IOException e) {
            checkIsStoppingOrThrow(e);
        }
    }
}
