/*
 * FTPClientStatus
 *
 *  Created on: Sep 1, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.ftp;

public class FTPClientStatus 
{
	public enum FTPStatus
	{
	    FTP_FAIL,
	    FTP_BUSY,
	    FTP_SUCCESS,
	    FTP_TIMEOUT,
	    FTP_BADSIZE,
	    FTP_SAMESIZE,
	    FTP_PROGRESS,
	    FTP_ABORT,
	};
	
	
	/**
	 * Translates status code to FTPStatus
	 * @param state
	 * @return one FTPStatus values.
	 */
	public static FTPStatus translateStatus(int status)
	{
		if (status < 0)
			throw new IllegalArgumentException();
		
		return FTPStatus.values()[status];
	}
	
	
	/**
	 * Check whether status is success.
	 * @param status FTPStatus
	 * @return true if status is successful, false otherwise.
	 */
	public static boolean isSuccess(FTPStatus status) 
	{
		switch (status) {
		case FTP_SUCCESS:
		case FTP_SAMESIZE:
			return true;
		default:
			return false;
		}
	}
	
	
	/**
	 * Check whether status is failure.
	 * @param status FTPStatus
	 * @return true if status is failure, false otherwise.
	 */
	public static boolean isFailure(FTPStatus status) 
	{
		switch (status) {
		case FTP_ABORT:
		case FTP_BUSY:
		case FTP_BADSIZE:
		case FTP_FAIL:
		case FTP_TIMEOUT:
			return true;
		default:
			return false;
		}
	}
}
