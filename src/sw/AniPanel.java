package sw;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class AniPanel extends JPanel {

    Font font;
    FontMetrics fontMetrics;
    Font newsFont=new Font("Dialog",Font.BOLD ,12);
    WorldWindow ww;

    protected int gXo;
    protected int gYo;
	
	int yellowx = 0;
	
    AniPanel() {
        font=new Font("Dialog",Font.BOLD,20);
        fontMetrics=getFontMetrics(font);
    }
		  
    public void paintAls(Graphics g) {
        for (Al v:ww.thisWorld.occupants) {
			  v.paintMe(g);
		 }
    }
    
    public void paintTerrain(Graphics2D g2D) {
		for (int r =0; r < ww.thisWorld.worldTerrain.length; r++ ) {
			for (int c = 0; c < ww.thisWorld.worldTerrain[r].length; c++) {
				ww.thisWorld.worldTerrain[r][c].draw(r,c,g2D);
			}
		}
    }
		  
    @Override
     public void paintComponent(Graphics g)  {

	 long startTime= System.nanoTime();    	 
    	 
	 Image buf = createImage(ww.thisWorld.windowWidth(),ww.thisWorld.windowHeight());
	 Graphics2D g2D = (Graphics2D)buf.getGraphics();
	 drawGrid(g2D);
	 paintTerrain(g2D);

	 paintAls(g2D);

         g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                      RenderingHints.VALUE_ANTIALIAS_ON);
         g2D.setFont(font);
         setTitle(g2D,15,35);  
         ww.thisWorld.iHal.splat(g2D);
	     g.drawImage(buf,0,0,null);
	     
	     if (ww.writeImages()) {
		     BufferedImage bi = (BufferedImage) buf; // retrieve image
		     File outputfile = new File("c:/ai_"+ww.thisWorld.turnNo+".png");
		     try {
		         ImageIO.write(bi, "png", outputfile);	   
		     } catch (Exception ex) {
		    	 
		     }
	     }
	     
	     ww.thisWorld.drawTime = System.nanoTime() - startTime;
	     //System.out.println("Draw Time :"+ww.thisWorld.drawTime);
    }
		  
     public void drawGrid(Graphics2D g2D) {
			  //g2D.clearRect(this.getX(), this.getY(), this.getWidth(),this.getHeight());
         gXo=ww.thisWorld.getGxo(); 
		 gYo=ww.thisWorld.getGyo();
			  
	     for (int x=gXo; x<= ww.thisWorld.gridWidth()+gXo; x+=ww.thisWorld.cellSize) {
		     for (int y=gYo; y<=ww.thisWorld.gridHeight()+gYo; y+=ww.thisWorld.cellSize) {
				  g2D.drawLine(x, gYo, x,ww.thisWorld.gridHeight()+gYo); // cols
				  g2D.drawLine(gXo, y, ww.thisWorld.gridWidth()+gXo, y); // rows
			  }
		  }
	  }
		  
	  public void setTitle(Graphics2D g2D,int x,int y) {
		  ww.setTitle("Turn: "+ww.thisWorld.showTurn()+" Alive: "+ww.thisWorld.leftAlive()+" CalcTime: "+ww.thisWorld.calcTime+" DrawTime: "+ww.thisWorld.drawTime);
	  }

	  /*
	   * Some Text Gfx Example code - not used.
	   */
	  public void sayGraphics(Graphics2D g2D,int x,int y) {//textured
          Rectangle r=new Rectangle(0,0,5,5);
		  BufferedImage bi=new BufferedImage(5,5,BufferedImage.TYPE_INT_RGB);
		  TexturePaint tp=new TexturePaint(bi,r); //texture needs image
		  Graphics2D big=bi.createGraphics();     //allow graphics work
		  big.setColor(Color.magenta);
		  big.fillRect(0,0,5,5);
		  big.setColor(Color.black);
		  big.fillOval(0,0,5,5);
		  g2D.setPaint(tp); 
		  g2D.drawString("Graphics",x,y);
	  }
		  
	  public void sayWorld(Graphics2D g2D, int x, int y) {//outlined
          FontRenderContext frc=new FontRenderContext(null,false,false);
		  TextLayout tl=new TextLayout("World!",font,frc);
		  AffineTransform textAt=new AffineTransform();
		  textAt.translate(0,(float)tl.getBounds().getHeight());
		  textAt.translate(x,y); textAt.shear(-0.5,0.0);
		  Shape outline=tl.getOutline(textAt);g2D.setColor(Color.blue);
		  BasicStroke wideStroke=new BasicStroke(2);
		  g2D.setStroke(wideStroke);g2D.draw(outline);
	  }
}
