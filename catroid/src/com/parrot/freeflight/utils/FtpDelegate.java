package com.parrot.freeflight.utils;

public interface FtpDelegate 
{
	public void ftpOperationSuccess(String contents);
	public void ftpOperationFailure();
}
