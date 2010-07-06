package com.scratchPlayer.app.objectReader;

public class MyColor {

	private float red;
	private float blue;
	private float green;
	private float alpha;
	public float rgb;
	
	MyColor(float r, float g, float b, float a) 
	{
		red =r;
		blue =b;
		green =g;
		alpha =a;
	}
	MyColor(float Rgb, float a) 
	{
		rgb =Rgb;
		alpha =a;
	}
	
	public float getRed()
	{
		return red;
	}

	public float getBlue()
	{
		return blue;
	}
	public float getGreen()
	{
		return green;
	}
	public float getAlpha()
	{
		return alpha;
	}
	public float getRgb()
	{
		return rgb;
	}
	public void setAlpha(float Alpha)
	{
		alpha =Alpha;
	}
	public void setRgb(float Rgb)
	{
		rgb =Rgb;
	}
}
