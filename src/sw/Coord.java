package sw;

public class Coord {

    public static int cellSize = 30;
    public static int originX=cellSize*2;
    public static int originY=cellSize*2;
	
    private int x;
    private int y;
    
    public Coord(int ix, int iy) {
    	x=ix;
    	y=iy;
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
    	return y;
    }
    
    // Direction 0=North, 1=North East etc
    public Coord move(int direction, double cells) {
    	// quick hack for negatives
    	if (direction <0) {
    		direction+=8;
    	}
    	if (direction >7) {
    		direction-=8;
    	}
    	
    	int modx=0;
    	int mody=0;
	    switch (direction) {
	    case 0: modx=0; mody=-1;break;
	    case 1: modx =1; mody=-1;break;
	    case 2: modx=1; mody=0;break;
	    case 3: modx=1;mody=1;break;
	    case 4: modx=0;mody=1;break;
	    case 5: modx=-1;mody=+1;break;
	    case 6: modx=-1;mody=0;break;
	    case 7: modx=-1;mody=-1;break;
	    }
	    return new Coord(x+=((double)modx*(double)cellSize*cells), y += ((double)mody*(double)cellSize*cells));
    }

    public Coord move90 (Coord ori, int d, double f) {
    	return (new Coord(ori.getX(),ori.getY()).move(d, f));
    }
    
    
}
