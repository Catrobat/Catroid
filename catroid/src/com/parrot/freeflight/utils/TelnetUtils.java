/*
 * TelnetUtils
 *
 *  Created on: Sep 1, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TelnetUtils 
{
	/**
	 * Performs execution of the shell command on remote host.
	 * @param host ip address or name of the host.
	 * @param port telnet port
	 * @param command shell command to be executed.
	 * @return true if success, false on error.
	 */
	public static final boolean executeRemotely(String host, int port, String command)
	{
		Socket socket = null;
		OutputStream os = null;
		
		try {
			socket = new Socket(host, port);
			
			os = socket.getOutputStream();
		
			os.write(command.getBytes());
			os.flush();
			
	        return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
