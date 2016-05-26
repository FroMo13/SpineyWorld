package sw;

import java.io.File;
import java.net.URL;
import java.util.*;

public class World extends Thread {

    // Control values for animation interpolation
    public static  int interpolationSteps = 3;
    public static  int interpolationFrame = 1;	
	
    // Thread control variable
    public static boolean running = true;
	
    // Store timing information of previous calc and frame draw
    public    long   calcTime;
    public    long   drawTime;
	
    // Wait time between frames.
    public    int    interval;	
	
    // Turn Counter
    protected int    turnNo =0;
	
    // World Dimensions
    protected int    x;
    protected int    y;
    protected int    cellSize = 20;

    // String to hold worthy world events
    protected String news;

    // Starting Occupants
    protected int    initOccupants = 1; 
	
    // Time before dead organisms disappear
    protected int    decomposeTurns = 10;  
	
    // Terrain generation variables
    protected int    landPCT = 10;      //  Seed Max elevation as 5% of tiles
    protected int    seaPCT = 5;        // Seed Min Elevation - sea - note that sea "smoothing" greatly increases the amount of water to approx 10 times this pct !
    protected int    elevationRange = 20; // Max height /depth
    
    // Handle to window object
    protected WorldWindow   ww;
	
    // List of world occupants
    protected ArrayList<Al> occupants;
	
    // Working list used for births
    protected ArrayList<Al> newOccupants;

    // Terrain array.
    protected Terrain[][] worldTerrain;
        
    // Image Handler
    protected ImageHandler iHal;

    protected boolean worldWanted=true;
    
	/*
	 * Start of Methods
	 */
	
	public void setCellSize(int c) {
		cellSize=c;
		Coord.cellSize=c;
        interpolationSteps = cellSize/2;
        iHal.scaleImages(cellSize);
	}
	
	public void addNews(String n) {
		news = " ["+n+"] "+news;
	}
	
	public String showTurn() {
		return Integer.toString(turnNo);
	}
	
	// TO help with Drawing - This are the Origin Points.
	public int getGxo() {
		return 2*cellSize;
	}
	
	public int getGyo() {
		return 2*cellSize;
	}
	
	public int gridHeight() {
		return y*cellSize;
	}
	
	public int gridWidth() {
		return x*cellSize;
	}
	
	public int windowWidth() {
		return (x+4)*cellSize+WorldWindow.panelWidth;
	}
	
	public int windowHeight() {
		return (y+4)*cellSize+WorldWindow.statusHeight;
	}
	
	public int worldXmax() {
		return x-1;
	}
	
	public int worldYmax() {
		return y-1;
	}
	
	/*
	 * Start of World Management Methods
	 */
	
	public ArrayList<Al> askAt(int x, int y) {
		ArrayList<Al> rAl = new ArrayList<Al>();
        for (Al v:occupants) {
        	if (v.locationX==x && v.locationY==y && !v.isDead() && v.isMature()) {
        		rAl.add(v);
        	}
        }
        return rAl;
	}
	
	protected void populate() {
		// Populate an array of occupants.
		Random generator = new Random();
		for (int i=1; i <= initOccupants; i++) {
			occupants.add(new Spikey(i,generator.nextInt(x),generator.nextInt(y),this));
		}
	}

	protected void listStatus() {
        for (Al v:occupants) {
			System.out.println("  "+v.tellStatus());
		}
	}
	
	// For each AL, calculate its intention
	protected void doIntentions() {
        for (Al v:occupants) {
        	v.alIntention();
		}		
	}

	protected void doExecutions() {
        for (Al v:occupants) {
			v.alExecution();
		}		
	}
	
	// Bring a new Al into the world.
	public void newAl(Al a, Al b) {
		Spikey s = new Spikey(occupants.size()+1 ,a.locationX ,a.locationY,this);
		s.flyer = a.flyer||b.flyer;
		s.walker =a.walker||b.walker;
		s.swimmer=a.swimmer||b.swimmer;
		
		// Lose non-essential locomotion ?
		Random generator = new Random();
		if (generator.nextInt(100) > 50) {
		    if (this.worldTerrain[a.locationX][a.locationY].isLand()) {
			    s.swimmer=false;
		    } else {
			    s.walker=false;
		    }
		}
		newOccupants.add(s);		
	}
	
	public void reproduce(Al a, Al b) {
	    Random generator = new Random();
	   	int broodSize = generator.nextInt(Al.maxBrood);
        for (int n = 0; n <= broodSize; n++) {
    		if (leftAlive() < (x*y)/4) {
        	    newAl(a,b);
    		}
        }
    	addNews(a.getDid()+"&"+b.getDid()+(broodSize+1)+" babies");
	}
	
    public void eat(Al a, Al b) {
    	// Give advantage to native species
    	int amod=1;
    	int bmod=1;
    	if (this.worldTerrain[a.locationX][a.locationY].isWater()) {
    		if (a.isSwimmer() && !a.isWalker()) {
    			amod=2;
    		}
    		if (b.isSwimmer() && !b.isWalker()) {
    			bmod=2;
    		} else {
    			if (a.isWalker() && !a.isSwimmer()) {
    				amod=2;
    			}
    			if (b.isWalker() && !b.isSwimmer()) {
    				bmod=2;
    			}
    		}
    	}
    	
	    if (a.attackPower()*amod > b.attackPower()*bmod) {
			a.eats(b);
		} else {
			b.eats(a);
		}
    }
    
    public int leftAlive() {
    	int c=0;
    	for (Al a:occupants) {
    		if (!a.isDead()) {
    			c++;
    		}
    	}
    	return c;
    }
	
	// Handle Al interaction
	public void interact(Al a, Al b) {
		Random generator = new Random();
		if (generator.nextInt(100) < Al.repFactor || leftAlive()==2) {
			reproduce(a,b);
		} else {
			eat(a,b);
		}
	}
	
	// Calculate outcome for collided Als
	protected void doCollisions() {
		newOccupants = new ArrayList<Al>();
		for (Al a:occupants) {
			for (Al b:occupants) {
				if (a.collide(b)) {
					if (a.isMature() && b.isMature()) {
						interact(a,b);
					}
				}
			}
		}
		
		// Merge newOccupants into occupants;
		for (Al newAl:newOccupants) {
			occupants.add(newAl);
		}
	}
	
    // Draw simple character representation - Now Obsolete
	protected void drawWorldC(int t) {
		System.out.println(Misc.padLeft("Turn: "+t,4));
		String g; // Grid
		String gv;
		String n; // Note
		String h = "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		ArrayList<Al> here;
		System.out.println("    "+h.substring(0,y));
		for (int dy=0;dy<= x-1; dy++) {
			g="";
			n="  ";
			for (int dx=0; dx <= y-1; dx++) {
			    // Ask each Al if they are here 
				here = askAt(dx,dy);
				if (here.isEmpty()) {
				    g+='_';
				} else {
					gv="";
				    for (Al v:here) {
			            if (v.isDead()) {
					        gv="*";
					    } else {
					        gv= v.getDid();
					    }
					    n+= v.tellStatus();
				    }
				    g+=gv;
				}
			}
			System.out.println(Misc.padRight(Integer.toString(dy), 4)+ g+n);
		}
	}	
	
	// Delete deads Als
	protected void removeDead() {
		ArrayList<Al> theDead = new ArrayList<Al>();
        for (Al d:occupants) {
            if (d.decomposed()) {
            	theDead.add(d);
            }
        }
        occupants.removeAll(theDead);
	}
	
	// Calculate each ALs intentions, then execute them
	protected void doTurn() {
		long startTime = System.nanoTime();
        turnNo++;
		doIntentions();
        doExecutions();
        doCollisions();
        removeDead();
		addNews("Turn: "+turnNo);
		calcTime = System.nanoTime() - startTime;
		ww.setNews(readNews());
		//drawWorldC(turnNo);
	}

    // Draw a series of interpolated frames.
	public void draw() {
        for (interpolationFrame= 1; interpolationFrame <= interpolationSteps; interpolationFrame++) {
            ww.updateWindow();
            try{
    	        Thread.sleep(interval);
            } catch (Exception ex) {
    	        System.out.println("Sleep Exception"+ex);
            }
        }
	}
	
	public String readNews() {
		int limit = 100;
        if (news.length() > limit ) {
        	news = news.substring(0,limit-1);
        }
        return news;
	}
	
	public void doTurnAndDraw() {
			doTurn();
			draw();
	}
	
	public void run() {
		while (worldWanted) {
	        if (running) {
	       		doTurnAndDraw();
	       	} else {
      	        try {
	        	    Thread.sleep(1000);
	        	} catch (Exception ex) {
	            }
		    }
		}
		System.out.println("Goodbye Cruel World!");
	}	
	
	/*
	 * Start of Terrain Logic
	 */
	public int emptyTerrain() {
		int empty = 0;
		for (int r =0; r < worldTerrain.length; r++ ) {
			for (int c = 0; c < worldTerrain[r].length; c++) {
				try {
					int e = worldTerrain[r][c].elevation;
				} catch (Exception ex) {
					empty++;
				}
			}
		}
		return empty;
	}
	
	public void fillTile(int r, int c) {
	// Look at surrounding tiles, sum elevation on all non-null tiles
	   int populated=0;
	   int totalElevation=0;
	   int myElevation;
	   
	   // Look around
       for (int ra = -1; ra <=1; ra++) {
    	   for (int ca = -1; ca <= 1; ca++) {
    		   if (r+ra < 0 || c+ca < 0 || r+ra >= worldTerrain.length || c+ca >= worldTerrain[0].length) {
    			   // out of range - do nothing !
    		   } else if (r+ra != r && c+ca != c) { // Exclude my location
    		       if (worldTerrain[r+ra][c+ca] != null) {
     		           populated++;
       		           totalElevation += worldTerrain[r+ra][c+ca].elevation;     		        	   
    		           }
  	    		   }
        	   }
            }
       if (populated == 0) {
    	   // Nothing nearby - leave it for now
       } else {
           myElevation = (int)Math.ceil((double)totalElevation / (double)populated);
           if (myElevation > 0 ) {
        	   worldTerrain[r][c] = new Soil(myElevation);
           } else {
        	   worldTerrain[r][c] = new Water(myElevation);
           }
       }
	}
	
	public void fillTerrain() {
		for (int r =0; r < worldTerrain.length; r++ ) {
			for (int c = 0; c < worldTerrain[r].length; c++) {
		        if (worldTerrain[r][c]==null) {
		        	fillTile(r,c);
		        }
			}
		}		
	}
	
	public int countWater(int r, int c) {
		int water = 0;
		for (int a = -1; a <=1; a++) {
            // No diagonals on this check.
			// Up/Down
			if ( r+a >= 0 && r+a < worldTerrain.length && worldTerrain[r+a][c] != null && worldTerrain[r+a][c].elevation < 0 ) {
				water ++;
			}
			// Left/Right
			if (c+a > 0 && c+a < worldTerrain[0].length && worldTerrain[r][c+a] != null && worldTerrain[r][c+a].elevation < 0 ) {
				water++;
			}
	    }
	    return water;
	}

	// Make any tile with t or more water tiles next to it into water
	public void fixWater(int t) {    	   
		for (int r=0; r < worldTerrain.length; r++ ) {
			for (int c = 0; c < worldTerrain[r].length; c++) {
                if (countWater(r,c) >= t) {
                	worldTerrain[r][c]=new Water(-7);
                }
			}
		}
	}

    // Step One - Calculate Elevation Highs (Rocks) and Lows (Water)
	public void buildTerrain() {
		Random generator = new Random();
		int ef = 0;
		for (int r =0; r < worldTerrain.length; r++ ) {
			for (int c = 0; c < worldTerrain[r].length; c++) {
				ef = generator.nextInt(100);
				if (ef < seaPCT) {
				    worldTerrain[r][c] = new Water(-elevationRange);	
				} else if (ef >= 98-landPCT ) {
					worldTerrain[r][c]=new Rock(elevationRange);
				}
			}
		}
		// Now we have the high points, fill the rest.
		while (emptyTerrain() != 0) {
			fillTerrain();
		}
		fixWater(3);
	}
	
	/*
	 * End of Terrain Logic
	 */
	
    public World(int ix, int iy, int io, int ic) {
        x = ix;
        y = iy;
        initOccupants = io;
        interpolationFrame=1;

        // Locate base directory
        URL s = getClass().getProtectionDomain().getCodeSource().getLocation();
        // append the image location -- This worlds outside a JAR
        iHal=new ImageHandler();
        
        
        setCellSize(ic);
        
        // Set some static values for the coordinate system
        Coord.cellSize = cellSize;
        Coord.originX  = getGxo();
        Coord.originY  = getGyo();
        
        news = "Welcome";
        // Set up Terrain
        worldTerrain = new Terrain[ix][iy];
        buildTerrain();
        occupants =  new ArrayList<Al>();
        populate();

        //drawWorldC(0);
    }
}
