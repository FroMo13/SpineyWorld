package sw;

import java.awt.Color;

public class Water extends Terrain {

    public boolean isWater() {
        return true;
    }
	
    public boolean isLand() {
        return false;
    }

    private void setColor(int alpha) {
        myColor = new Color (0,0,200,Math.abs(alpha));
    }	
	
    public Water() {
        setColor(50);
    }
	
    public Water(int e) {
        elevation = e;
	int ce = +e*20;
	if (ce <100) {
	    ce=100;
	}
	    setColor(ce);
    }
}
