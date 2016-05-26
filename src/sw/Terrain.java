package sw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public abstract class Terrain {
	
    protected Color myColor;
    protected static World myWorld;
    protected int elevation = 0;
	
    public boolean isLand() {
        return true;
    }
	
    public boolean isWater() {
        return false;
    }
	
    public static void setWorld(World w) {
        myWorld = w;
    }
	
    public Color getColor() {
    	return myColor;
    }
    
    public void draw(int x, int y, Graphics2D g2D) {
    	g2D.setColor(myColor);
        g2D.fillRect((x*myWorld.cellSize)+myWorld.getGxo(),
   	             (y*myWorld.cellSize)+myWorld.getGyo(),
   	              myWorld.cellSize,
   	              myWorld.cellSize);
    }
}
