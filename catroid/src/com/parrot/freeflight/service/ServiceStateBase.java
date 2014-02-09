/*
 * ServiceStateBase
 *
 *  Created on: May 5, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.service;

import com.parrot.freeflight.service.commands.DroneServiceCommand;


public abstract class ServiceStateBase 
{
	public enum EServiceStateResult {
		SUCCESS,
		FAILED
	}
	
	private EServiceStateResult result;
	protected DroneControlService context;

	public ServiceStateBase(DroneControlService context)
	{
		this.context = context;
	}
	
	public abstract void connect();
	public abstract void disconnect();
	public abstract void resume();
	public abstract void pause();
	public abstract void onCommandFinished(DroneServiceCommand command);
	
	
	protected void onPrepare()
	{}
	
	protected void onFinalize()
	{}
	
	public String getStateName()
	{
		return getClass().getSimpleName();
	}
	
	
	public EServiceStateResult getResult()
	{
		return result;
	}
	
	
	protected void setResult(EServiceStateResult result)
	{
		this.result = result;
	}
	
	
	protected void setState(ServiceStateBase state)
	{
		context.setState(state);
	}
	
	
	protected void startCommand(DroneServiceCommand cmd)
	{
		context.startCommand(cmd);
	}
	
	
	protected void onConnected()
	{
		context.onConnected();
	}
	
	
	protected void onDisconnected()
	{
		context.onDisconnected();
	}
	
	
	protected void onPaused()
	{
		context.onPaused();
	}
	
	
	protected void onResumed()
	{
		context.onResumed();
	}

	
}
