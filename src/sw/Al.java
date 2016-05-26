package sw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public abstract class Al {
    
    // UID counter
    public static int   lastID = 0;
	
    // Chance of reproduction / brood max size
    public static int   repFactor = 35;
    public static final int   maxBrood = 3;
	
    // My base graphics co-ordinates - top-right
    public int meXgfx; 
    public int meYgfx;
	
    // Identifiers
    protected String    name;
    protected int       id;        
    protected char      Did;

    // Age in turns / Time Dead / Dead or alive ?
    protected int       age;       
    protected int       turnsDead;
    protected String    status;

    // Controls sleep cycle
    protected int       energy;    // will need to sleep once depleted
    protected int       maxEnergy; // energy increased when asleep until max is reached;
	
    // Vision cone.	
    protected int       vision;    // number of units ahead Al can see
    protected int       visionFOV; // width of vision tunnel. 
    protected Facing    facing;    // 0= North, 2 = NE... 7 = NW

    // Location in Cells, now and previous
    protected int       locationX;
    protected int       locationY;
    protected int       prevLocationX;
    protected int       prevLocationY;
	
    // Change of changing direction
    protected int       turnChance = 30;
    protected int       baseTurnChance = 30;
	
    // Age (turns) at which interaction commences
    protected int       matureAge = 10;
	
    // Evolution points compose number of times an Al must fail movement before evolving
    protected int evolutionPoints=0;
    protected int evolutionTarget=50;	

    // Handle to my world object
    protected World     myWorld;
	
    // Handle to intention object
    protected Intention myIntention;
	
    // Locomotion attributes
    protected boolean swimmer =false;
    protected boolean walker  =true;
    protected boolean flyer   =false;
	
    // Status colors
    Color aliveColor = new Color(0,255,0,255);
    Color deadColor  = new Color(0,0,0,50);
    Color sleepColor = new Color(0,0,240,255);
    Color huntColor  = new Color(255,0,0,255);
    Color lookColor  = new Color(0,150,0,75);

    // Images replace colors
    ImageMember meIM;
    
    // Font for drawing Al name, intention
    Font nameFont=new Font("Dialog",Font.PLAIN ,8);
    
    // List of other Als I can see
    protected ArrayList<Al> visibleAls;
	
    /*
    * Start of methods
    */
    public boolean isSwimmer() {
	return swimmer;
    }
	
    public boolean isWalker() {
	return walker;
    }
	
    public boolean isFlyer() {
	return flyer;
    }
	
    // If we are starting on a sea square we need to be a swimmer
    public void checkSwimmer() {
	if (myWorld.worldTerrain[locationX][locationY].isWater()) {
            swimmer=true;
	    walker=false;
	}
    } 	
	
    public boolean decomposed() {
	if (turnsDead >= myWorld.decomposeTurns) {
            return true;
	}
	    return false;
    }
	
    public boolean isMature() {
	if (age >= matureAge) {
            return true;
	}
	    return false;
    }
	
    public void die() {
        meIM=myWorld.iHal.findImageMember("dead.gif");   
	status = "Dead";
	prevLocationX=locationX;
	prevLocationY=locationY;
	energy=0;
    }
	
    public boolean isDead() {
	if (status.equalsIgnoreCase("Dead")) {
	    return true;
	} 
	    return false;
	}
	
    public boolean isAsleep() {
	if (myIntention.myIntention.equalsIgnoreCase("SLEEP")) {
	    return true;
	}
	    return false;
	}
	
    public int attackPower() {
    	if (myIntention.myIntention.equalsIgnoreCase("Hunt")) {
            return energy+2;
    	}
    	return energy;
    }
	
    public String getDid() {
	return Character.toString(Did);
    }
	
    public String tellIntention() {
	return myIntention.tellIntention();
    }
	
    public boolean collide(Al t) {
	if (t.locationX == locationX && 
            t.locationY == locationY && 
	    Did != t.Did &&
	    !t.isDead() && 
	    !isDead()) {
	        return true;
	}
	return false;
    }
	
    public boolean canSee(Al t) {
	if (visibleAls != null && !visibleAls.isEmpty()) {
            return visibleAls.contains(t);
	} else {
	    return false;
	}
    }
	
    public String canSee() {
    	String r="";
    	if (visibleAls==null || visibleAls.isEmpty()) {
            r = "[None ";
    	} else {
    	    r="[";
            for (Al v:visibleAls) {
                r +=Character.toString(v.Did)+",";
            }
	}
        return r.substring(0,r.length()-1)+"]";
    }
    
    public String tellStatus() {
    	if (!isDead()) {
    	    return "["+Character.toString(Did)+":"+"xy"+locationX+","+locationY+" Fc"+facing.description+" Sees"+canSee()+" E"+energy+"Int:"+myIntention.tellIntention()+"]";
    	} else {
    	    return "["+Character.toString(Did)+":"+"xy"+locationX+","+locationY+" Dead "+"]";
    	}
    }
    
    public void alIntention() {
    	if (!isDead()) {
            myIntention.calcIntention();
    	}
    }
    
    public void alExecution() {
    	if (!isDead()) {
    		age++;
    	    myIntention.action();
    	} else {
    		turnsDead++;
    	}
    	if (evolutionPoints> evolutionTarget){
    		swimmer=true;
    		walker=true;
    	}
    }
    
    public void moveForward() {
    	if (prevLocationX!=locationX && prevLocationY != locationY) {
    		turnLR();
    	} else {
		    turnRandom();
        }
    	
    	int nx =locationX+facing.modx;
    	int ny =locationY+facing.mody;
    	
    	turnChance+=5;
    	
    	// I have bumped into the X limit 
    	if (nx < 0 || nx > myWorld.worldXmax()) {
    		turnChance+=10;
    		nx = locationX;
    	}
    	
    	// I have bumped into the Y limit
    	if (ny < 0 || ny > myWorld.worldYmax()) {
    		turnChance+=10;
    		ny = locationY;
    	}
    	
    	// If have haven't moved at all I'm probably stuck in a corner.
    	if (nx==locationX && ny==locationY) {
    		turnChance+=50;
    	}
    	// Check if we actually can move here
    	
    	if ((myWorld.worldTerrain[nx][ny].isWater() && isSwimmer())
    		 ||( myWorld.worldTerrain[nx][ny].isLand() && isWalker())
    		 ||isFlyer()) {
                 prevLocationX = locationX;
        	 prevLocationY = locationY;
    	         locationX = nx;
    	         locationY = ny;
    	         energy--;
    	} else {
    		// Tried to do something but couldn't -- add evolution point!
            prevLocationX = locationX;
   	    prevLocationY = locationY;		
    	    turnChance+=50;
    	    evolutionPoints++;
    	}
    }
    
    public void turnLR() {
    	Random generator = new Random();
    	if (turnChance > generator.nextInt(100)) {
    		facing.setDirection( facing.direction+((generator.nextInt(2)*2)-1));
        	turnChance =baseTurnChance;
    	}
    }
    
    public void turnRandom() {
    	Random generator = new Random();
    	if (turnChance > generator.nextInt(100)) {
     		facing.setDirection(generator.nextInt(7)); 
     		turnChance=baseTurnChance;
    	}
    }
    
    public void hunt(Al t) {
        meIM=myWorld.iHal.findImageMember("hunt.gif");           
    	int nx=locationX;
    	int ny=locationY;
    	if (nx < t.locationX) {
    		nx++;
    	} else if (nx > t.locationX) {
    		nx--;
    	}
    	
    	if (ny < t.locationY) {
    		ny++;
    	} else if (ny > t.locationY) {
    		ny--;
    	}
    	
    	// Hunting is NOT inhibited by terrain 
    	prevLocationX = locationX;
    	prevLocationY = locationY;
    	locationX=nx;
    	locationY=ny;
    	energy--;
    }
    
    public void graze() {
        meIM=myWorld.iHal.findImageMember("graze.gif");   
    	moveForward();
    }
    
    public void sleep() {
        meIM=myWorld.iHal.findImageMember("sleep.gif");        
    	energy+=2;
    	prevLocationX=locationX;
    	prevLocationY=locationY;
    }
    
    public void eats(Al b) {
    	myWorld.addNews(getDid()+" Eats "+b.getDid());
    	energy += b.energy+2;
    	b.die();
    }
    
    public int look() {
    	// Based around direction, calculate a number of co-ordinates, then ask each Al in turn if they are there.
    	int lx,ly;
    	visibleAls = new ArrayList<Al>();
    	for (int ld=0; ld < vision;ld++ ) {
    	    for (int lw= -visionFOV; lw <= visionFOV;lw++) {
    	        // This loop modifies the origin position, then looks ahead from there, so as to create a vision tunnel	
    	        lx = locationX+facing.modx+facing.visionX(lw)+(facing.modx*ld);
    	        ly = locationY+facing.mody+facing.visionY(lw)+(facing.mody*ld);
        	   
    	        // Are there any Als at this location ?
    	        ArrayList<Al> v = myWorld.askAt(lx,ly);
    	        if (v != null) {
    	    	    for (Al vi:v) {
        	        visibleAls.add(vi);
    	    	    }
    	        }
            }
    	}
   	return visibleAls.size();
    }
    
    /*
     * Start of Graphics methods
     */
    public int paintCentreX() {
    	if (isMature()) {
    	    return meXgfx+myWorld.cellSize/2;
    	}
    	return+meXgfx+myWorld.cellSize/4;
    }
    
    public int paintCentreY() {
    	if (isMature()) {
            return meYgfx+myWorld.cellSize/2;
        }
        return+meYgfx+myWorld.cellSize/4;
    }
    
    public void paintName(Graphics2D g2D) {
    	g2D.setFont(nameFont);
    	g2D.setColor(Color.black);
    	g2D.drawString(getDid(),
    		       meXgfx,
    		       meYgfx+myWorld.cellSize/2);
    	g2D.drawString(myIntention.tellIntention(),
                       meXgfx,
	               meYgfx+myWorld.cellSize);
    }
    
    public void paintFacing(Graphics2D g2D) {
    	if (!isDead() && !isAsleep()) {
     	    Coord o = new Coord(paintCentreX(), paintCentreY());
     	    // For diagonals we have to shorten draw distance
     	    int visionAdj=vision;
     	    if (facing.direction%2d==1) {
     	    	visionAdj--;
     	    }
            
            Coord m = new Coord(paintCentreX()+((facing.modx*myWorld.cellSize)*visionAdj),
		                paintCentreY()+(facing.mody*myWorld.cellSize)*visionAdj);
    	    Coord l = m.move90(m, facing.direction-2, visionFOV+.5);
    	    Coord r = m.move90(m, facing.direction+2, visionFOV+.5);
    	
    	    Misc.paintTriangle(g2D, o.getX(), o.getY(), l.getX(),l.getY(),r.getX(), r.getY(), lookColor);
    	}
   }
    
   public void paintFeet(Graphics2D g2D) {
       //   g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

       if (isWalker()) {
           if (isMature()) {    
               g2D.drawImage(myWorld.iHal.findImageMember("feet.gif").getScaledImage(),meXgfx,meYgfx+myWorld.cellSize,null);
           } else {
               g2D.drawImage(myWorld.iHal.findImageMember("feet.gif").getScaledImageJuvi(),meXgfx,meYgfx+(myWorld.cellSize/2),null);
           }
       }
           /*
           g2D.drawLine(paintCentreX()-(myWorld.cellSize/4),
	                paintCentreY()+(myWorld.cellSize/4),
		        paintCentreX()-(myWorld.cellSize/4),
		        paintCentreY()+(int)(myWorld.cellSize*.8));

    	   g2D.drawLine(paintCentreX()+(myWorld.cellSize/4),
	                paintCentreY()+(myWorld.cellSize/4),
		        paintCentreX()+(myWorld.cellSize/4),
		        paintCentreY()+(int)(myWorld.cellSize*.8));
    	   */	
    }
    
    public void paintFin(Graphics2D g2D) {
    	if (isSwimmer()) {
            if (isMature()) {
               g2D.drawImage(myWorld.iHal.findImageMember("fin.gif").getScaledImage(),meXgfx,meYgfx-myWorld.cellSize,null);
            } else {
               g2D.drawImage(myWorld.iHal.findImageMember("fin.gif").getScaledImageJuvi(),meXgfx,meYgfx-(myWorld.cellSize/2),null);
            }
            
            /*
            g2D.drawLine( paintCentreX(),
    	    paintCentreY(),
    	    paintCentreX(),
    	    paintCentreY()-(int)(myWorld.cellSize*.8));
             */
    	}
    }
    
    // interpolate an x adjustment based upon pre, target and frame
    public int xOffset() {
    	int pixels = ((locationX-prevLocationX)*myWorld.cellSize);
    	double ratio = (double)myWorld.interpolationFrame/(double)myWorld.interpolationSteps;
    	double r = pixels * ratio;
    	return (int)r;
    }
   
    public int yOffset() {
    	int pixels = ((locationY-prevLocationY)*myWorld.cellSize);
    	double ratio = (double)myWorld.interpolationFrame/(double)myWorld.interpolationSteps;
    	double r = pixels * ratio;
    	return (int)r;
    }
    
    public void paintMe(Graphics g) {
		
        if (isMature()) {
	    meXgfx = ((prevLocationX*myWorld.cellSize)+myWorld.getGxo())+xOffset();
	    meYgfx = ((prevLocationY*myWorld.cellSize)+myWorld.getGyo())+yOffset();
	} else {
	    meXgfx= ((prevLocationX*myWorld.cellSize)+myWorld.getGxo()+myWorld.cellSize/4)+xOffset();
	    meYgfx= ((prevLocationY*myWorld.cellSize)+myWorld.getGyo()+myWorld.cellSize/4)+yOffset();
	}
		
	Graphics2D g2D=(Graphics2D) g;
	
        if (isDead()) {
	    g2D.setPaint(deadColor);
	} else if (myIntention.myIntention.equalsIgnoreCase("Sleep")){
	    g2D.setPaint(sleepColor);
	} else if (myIntention.myIntention.equalsIgnoreCase("Hunt")) {
	    g2D.setPaint(huntColor);
	} else {
	    g2D.setPaint(aliveColor);
	}
	if (isMature()) {
            /*
	    g2D.fill3DRect(meXgfx,
	                   meYgfx,
		           myWorld.cellSize,
		           myWorld.cellSize,
		           true);
            */
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2D.drawImage(meIM.getScaledImage(),meXgfx,meYgfx,null);
	} else {
            /*
	    g2D.fill3DRect(meXgfx,
	                   meYgfx,
		           myWorld.cellSize/2,
		           myWorld.cellSize/2,
		           true);
            */
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2D.drawImage(meIM.getScaledImageJuvi(),meXgfx,meYgfx,null);
	}
                
	paintFacing(g2D);
        paintName(g2D);	
        paintFin(g2D);
        paintFeet(g2D);
    }    
    /*
    * End of Graphics Methods
    */
    public Al(int n, int x, int y, World w) {
  
    	// World Info
        locationX = x;
	locationY = y;
		
	// initially, previous is the same as current
	prevLocationX = x;
	prevLocationY = y;
	
	myWorld   = w;
    	
	// Id Info
    	id  = lastID;
    	Did = (char)(97+id); // Assign id a,b etc
    	lastID++;
	name ="Al:"+Did;
    	
    	// Attributes
	vision    = 3; // Randomize this later
    	visionFOV = 1; // 0 - no FOV, 1= 1 extra pos each side, 2 = +2 etc.
    	energy    = myWorld.cellSize; // Can cross the map before resting.
    	maxEnergy = energy;
    	status    = "Alive";
    	age       = 0;

    	facing = new Facing();
    	myIntention = new Intention(this);
    	checkSwimmer();
        meIM = myWorld.iHal.findImageMember("ball.gif");
    }        
}

