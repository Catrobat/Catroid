/*
 * Version
 *
 *  Created on: Apr 14, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.utils;

import java.util.StringTokenizer;

public class Version 
{
	private int major;
	private int minor;
	private int release;
	
	/**
	 * Creates new Version object.
	 * @param version should match the "#.#.#" pattern.
	 */
	public Version(String version)
	{
		StringTokenizer tokenizer = new StringTokenizer(version, ".");
		
		int counter = 0;
		
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int intValue = Integer.parseInt(token);
			
			if (counter == 0) {
				major = intValue;
			} else if (counter == 1) {
				minor = intValue;
			} else if (counter == 2) {
				release = intValue;
			}
			
			counter += 1;
		}
	}
	

	public boolean isGreater(Version compareTo)
	{
		if (major > compareTo.major)
			return true;
		else if (major < compareTo.major) 
			return false;
		
		if (minor > compareTo.minor)
			return true;
		else if (minor < compareTo.minor)
			return false;
		
		if (release > compareTo.release)
			return true;
		else if (release < compareTo.release)
			return false;
		
		return false;
	}
	
	
	public boolean isLower(Version compareTo)
	{
		if (major < compareTo.major)
			return true;
		else if (major > compareTo.major)
			return false;
		
		if (minor < compareTo.minor)
			return true;
		else if (minor > compareTo.minor)
			return false;
		
		if (release < compareTo.release)
			return true;
		else if (release > compareTo.release)
			return false;
		
		return false;
	}
}
