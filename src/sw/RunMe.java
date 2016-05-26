package sw;

public class RunMe {
    /**
     * @param args
     */

    // Delay between screen refresh (ms)
    public static int interval = 50;
	
    // World Dimensions
    static int worldX = 40;
    static int worldY = 40;
    static int cellSize =20;

    // Initial Als
    static int startOccupants = 50;
	
    public static void main(String[] args) {
        // Initialise World
        World w = new World(worldX,worldY,startOccupants, cellSize);
        w.interval=interval;
        Terrain.setWorld(w);
        WorldWindow ww = new WorldWindow(w); 
        w.ww=ww; // World's world window = this world window !
    }
}
