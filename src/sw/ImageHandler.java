package sw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ImageHandler {

    protected ArrayList<ImageMember> images;
    private int lastCellSize;
    
    public ImageMember findImageMember(String n) {
        ImageMember r =null;
        for (ImageMember i:images) {
            if (i.imageName.equalsIgnoreCase(n)) {
                r=i;    
            }
        }
        if (r != null) {
            return r;
        } else {
            // WARNING !!! RECURSIVE FAILURE DEATHTRAP
            if (n.equalsIgnoreCase("default.gif")) {
                return null;
            } else {
                return findImageMember("default.gif");
            }
        }
    }
    
    public ImageHandler() {
        // Open inventory.txt, then read each named file !
        images = new ArrayList<ImageMember>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(getClass().getResource("images/inventory.txt").getFile()));

            String ln;
            String [] items;
            while ((ln=br.readLine()) != null ) {
                // Break string into OBJECT_NAME, FRAME and Filename.
                if (!ln.startsWith("#")) {
                    items = ln.split("\\|");                     
                    //System.out.println(ln);
                    //System.out.println("ObjName:"+items[0]);
                    //System.out.println("Frame:"+items[1]);
                    //System.out.println("Filename:"+items[2]);
                    
                    ImageMember i = new ImageMember(items[2]);  
                    images.add(i);                    
                }
            }
        
        } catch (Exception ex) {
            System.out.println("inventory.txt not found");
            System.exit(-1);
        }
        
 /*       
        
        if (!directory.isDirectory()) {
            System.out.println("Not a directory");
            return;
        }
        images = new ArrayList<ImageMember>();
        File[] files = directory.listFiles();
        try {
        for (int x=0; x<= files.length-1; x++) {
            ImageMember i = new ImageMember(files[x]);
            images.add(i);
            System.out.println("Loaded Image: "+files[x].getCanonicalPath());
    
        }
        } catch (IOException ex) {
            System.out.println("Broken"+ex);
        }
 * 
 */
    }
    
    public void scaleImages(int cSize) {
        for (ImageMember i:images) {
        	i.scaleImage(cSize);
        	lastCellSize = cSize;
        }
    }
    
    public void splat(Graphics2D g) {
    	int x=5; int y =5;
    	for (ImageMember i:images) {
    		g.drawImage(i.getScaledImage(),x,y,null);
    		y+=lastCellSize;
    	}
    }
}
