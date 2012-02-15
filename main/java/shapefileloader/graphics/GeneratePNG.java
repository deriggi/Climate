/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 *
 * @author wb385924
 */
public class GeneratePNG {

  

    public static void saveImage(MapContext map, final String file, final int imageWidth) {
        GTRenderer renderer = new StreamingRenderer();
        renderer.setContext(map);
        Rectangle imageBounds = null;
        ReferencedEnvelope mapBounds = null;
        try {
            mapBounds = map.getLayerBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(
                    0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

        } catch (Exception e) {
            // failed to access map layers
            throw new RuntimeException(e);
        }

        BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

        Graphics2D gr = image.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);

        try {
            renderer.paint(gr, imageBounds, mapBounds);
            File fileToSave = new File(file);
            ImageIO.write(image, "png", fileToSave);

        } catch (IOException e) {
            throw new RuntimeException(e);
           
        }finally{
             map.dispose();
        }
    }

    public static void writeImageToStream(MapContext map, OutputStream os, final int imageWidth) {
        GTRenderer renderer = new StreamingRenderer();
        renderer.setContext(map);
        Rectangle imageBounds = null;
        ReferencedEnvelope mapBounds = null;
        try {
            mapBounds = map.getLayerBounds();
            double heightToWidth = mapBounds.getSpan(1) / mapBounds.getSpan(0);
            imageBounds = new Rectangle(
                    0, 0, imageWidth, (int) Math.round(imageWidth * heightToWidth));

        } catch (Exception e) {
            // failed to access map layers
            throw new RuntimeException(e);
        }

        BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D gr = image.createGraphics();
        gr.setPaint(Color.WHITE);
        gr.fill(imageBounds);
        renderer.paint(gr, imageBounds, mapBounds);
        try {
            ImageIO.write(image, "png", os);
        } catch (IOException ex) {
            Logger.getLogger(GeneratePNG.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if(os != null){
                try {
                    os.close();
                } catch (IOException ex) {
                    Logger.getLogger(GeneratePNG.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
}
