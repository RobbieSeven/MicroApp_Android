package it.unisa.microapp.editor;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Shape extends StateManagement
{
	public abstract String getText();
	public abstract void setIconin(String icon, int id);
	public abstract String showInfo();
	public abstract String showInfo2();
	
	public abstract void draw(Canvas panel, Paint paint, boolean iconified, int col, int row);
}
