package sw;

import java.awt.Color;
import java.awt.Graphics2D;

public class Misc {
	
	public static String padRight(String s, int n) {
	    return String.format("%1$-" + n + "s", s);  
	}	
	
	public static String padLeft(String s, int n) {
	    return String.format("%1$#" + n + "s", s);  
	}
	
	public static void paintTriangle(Graphics2D g2D,int x1, int y1, int x2, int y2, int x3, int y3, Color c) {
		  int [] xAr = {x1,x2,x3};
		  int [] yAr = {y1,y2,y3};
		  
		  g2D.setColor(c);
		  g2D.fillPolygon(xAr, yAr, 3);
		  g2D.drawPolygon(xAr, yAr, 3);
	  }	
	
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception ex) {
			
		}
	}
}
