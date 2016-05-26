package sw;

import java.awt.Color;

public class Soil extends Terrain {
	
    private void setColor(int alpha) {
        myColor = new Color (130,250,40,alpha);
    }
	
    public Soil() {
        setColor(50);
    }
	
    public Soil(int e) {
        elevation=e;
	int ce = e*10;
        if (ce<90) {
	    ce=90;
	} 
		
	if (ce>250) {
            ce=250;
	}
	    setColor(ce);
	}
}
