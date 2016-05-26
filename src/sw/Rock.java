package sw;

import java.awt.Color;

public class Rock extends Terrain {

	private void setColor() {
		myColor = new Color (50,150,50,250);
	}	
	
	public Rock() {
		setColor();
	}
	
	public Rock(int e) {
		elevation = e;
		setColor();
	}
}