package org.osivia.portal.administration.ejb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class MediaData implements Serializable{

    private static final long serialVersionUID = 1L;
    Integer Width=110;
    Integer Height=50;
    Color Background=new Color(190, 214, 248);
    Color DrawColor=new Color(0,0,0);
    Font font = new Font("Serif", Font.TRUETYPE_FONT, 30);
    
	public Integer getWidth() {
		return Width;
	}
	public void setWidth(Integer width) {
		Width = width;
	}
	public Integer getHeight() {
		return Height;
	}
	public void setHeight(Integer height) {
		Height = height;
	}
	public Color getBackground() {
		return Background;
	}
	public void setBackground(Color background) {
		Background = background;
	}
	public Color getDrawColor() {
		return DrawColor;
	}
	public void setDrawColor(Color drawColor) {
		DrawColor = drawColor;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    

    /* Corresponding getters and setters */


}