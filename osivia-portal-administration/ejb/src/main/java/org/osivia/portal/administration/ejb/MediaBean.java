package org.osivia.portal.administration.ejb;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import javax.imageio.ImageIO;


public class MediaBean {
	/*
    public void paint(OutputStream out, Object data) throws IOException{
        Integer high = 9999;
        Integer low = 1000;
        Random generator = new Random();
        Integer digits = generator.nextInt(high - low + 1) + low;
        if (data instanceof MediaData) {            
            MediaData paintData = (MediaData) data;
            BufferedImage img = new BufferedImage(paintData.getWidth(),paintData.getHeight(),BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = img.createGraphics();
            graphics2D.setBackground(paintData.getBackground());
            graphics2D.setColor(paintData.getDrawColor());
            graphics2D.clearRect(0,0,paintData.getWidth(),paintData.getHeight());
            graphics2D.setFont(paintData.getFont());
            graphics2D.drawString(digits.toString(), 20, 35);
            ImageIO.write(img,"png",out);
        }
    }
    */
    
    public void paint(OutputStream out, Object data) throws IOException{

        if (data instanceof MediaData) 
        {            
            MediaData paintData = (MediaData) data;
            
            String xmlStr = "";
        	xmlStr = "<?xml version='1.0' encoding = 'UTF-8'?>";
        	xmlStr += "<ROOT>";
        	xmlStr += "</ROOT>";
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	try{
        		System.out.print("-----------------------Gen fichier conf via MediaBean---------------------------");
	        	baos.write(xmlStr.getBytes());
	        	out.write(xmlStr.getBytes());
	        	System.out.print("--------------------------------------------------");
        	}
        	catch(Exception e)
        	{
        		System.out.print(e.toString());
        	}
        	//byte[] content = baos.toByteArray();
        }
    }
    
    

}