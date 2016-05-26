package sw;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class ImageMember {
    
    public String imageName;
    private BufferedImage masterImage;
    private BufferedImage scaledImage;
    private BufferedImage scaledImageJuvi; 

    
    public String getImageName() {
        return imageName;
    }
    
    public BufferedImage getScaledImage() {
        return scaledImage;
    }
    
    public BufferedImage getScaledImageJuvi() {
        return scaledImageJuvi;
    }
    
    public ImageMember() {
    }
    
    public ImageMember(String f) {
        imageName =  f;
        try {
            System.out.println("f:"+f);
            URL imgURL = getClass().getResource("images/"+f);
            masterImage= ImageIO.read(imgURL);
            
        } catch (Exception ex ) {
            System.out.println("Failed to load: "+f);
        }
    }
    
    // rescale from master
    public void scaleImage(int c) {
        int type = (masterImage.getTransparency() == Transparency.OPAQUE) ?
        BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        
        scaledImage = new BufferedImage(c,c,type);
        Graphics2D g2D = scaledImage.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2D.drawImage(masterImage, 0, 0, c, c, null);

        scaledImageJuvi = new BufferedImage(c/2,c/2,type);
        g2D = scaledImageJuvi.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2D.drawImage(masterImage, 0, 0, c/2, c/2, null);
        g2D.dispose();
    }
}
