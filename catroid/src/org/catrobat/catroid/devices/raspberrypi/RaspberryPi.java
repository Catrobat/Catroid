package org.catrobat.catroid.devices.raspberrypi;

/**
 * Created by patri on 13.12.2015.
 */
public interface RaspberryPi {

    Boolean connect(String host, int port);

    void setPin(int GPIO, Boolean pinValue);

    void disconnect();

    /*void setPWM(int GPIO);

    int getPin(int GPIO);*/
}
