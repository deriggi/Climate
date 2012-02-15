/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package shapefileloader.oldclimate;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.ShapeWrapper;
import shapefileloader.FeatureHandler;

/**
 *
 * @author wb385924
 */
public class OldMaxMinFeatureHandler implements FeatureHandler{
    private static final Logger log = Logger.getLogger(OldMaxMinFeatureHandler.class.getName());
    public BasicAverager ba = new BasicAverager();

     private String convertMultiPolygonToPolygon(String gon){
        StringBuilder sb = new StringBuilder();
        String geom = gon.substring(gon.indexOf("("),gon.lastIndexOf(")"));
        sb.append("POLYGON");
        sb.append(geom.substring(1));

        return sb.toString();
    }

    public void handleFeature(ShapeWrapper wrapper) {
        

        HashMap<String,String> map = wrapper.getPropertyMap();

        // map size should be at least twelve
        
        Set<String> keys = map.keySet();
       
        
        for(String s: keys){

           double value = Double.parseDouble(map.get(s));
           ba.update(value);
        }
        
        
    }

}
