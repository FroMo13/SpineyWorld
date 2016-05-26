package sw;

import java.util.Random;

public class Facing {
	
	protected String description;
	protected int modx;
	protected int mody;
	protected int direction; //(0= North, 2 = East, 4 = South etc)
	
	protected int visionX(int pFOV) {
		// p 0 = North, p7 = North West,
		int r;
		if (direction == 0 || direction == 4) { // North, South
	    	r = pFOV;
		} else {  
	    	r = 0;
	    }
		return r;
	}

	protected int visionY(int pFOV) {
		// p 0 = North, p7 = North West,
		int r;
		if (direction == 0 || direction == 4) { // 
	    	r = 0;
		} else {  
	    	r = pFOV;
	    }
		return r;
	}
	
	public void setDirection(int d) {
    	// quick hack for negatives
    	if (d <0) {
    		d+=8;
    	}
    	if (d >7) {
    		d-=8;
    	}
		direction = d;
	    switch (d) {
	    case 0: description = "North"; modx=0; mody=-1;break;
	    case 1: description = "North-East"; modx =1; mody=-1;break;
	    case 2: description = "East"; modx=1; mody=0;break;
	    case 3: description = "South-East"; modx=1;mody=1;break;
	    case 4: description = "South"; modx=0;mody=1;break;
	    case 5: description = "South-West"; modx=-1;mody=+1;break;
	    case 6: description = "West"; modx=-1;mody=0;break;
	    case 7: description = "North-West"; modx=-1;mody=-1;break;
	    }
	}
	
	public Facing() {
	    // Randomise
	    Random generator = new Random();
    	    setDirection(generator.nextInt(7));
	}
}
