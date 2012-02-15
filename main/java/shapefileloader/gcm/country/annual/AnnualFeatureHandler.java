/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.country.annual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.ShapeWrapper;
import shapefileloader.FeatureHandler;
import shapefileloader.gcm.monthly.MonthlyFeatureHandler;

/**
 *
 * @author wb385924
 */
public class AnnualFeatureHandler implements FeatureHandler {

    private static final Logger log = Logger.getLogger(MonthlyFeatureHandler.class.getName());
    private BasicAverager ba = new BasicAverager();
  
   

   

    private List<Double> toList(double[] arrayVals){
        ArrayList<Double> list = new ArrayList<Double>();
        for(Double d: arrayVals){
            list.add(d);
        }
        return list;
    }

    public AnnualFeatureHandler() {
        
    }

    public BasicAverager getAverager(){
        return ba;
    }

    public void handleFeature( ShapeWrapper wrapper) {
        HashMap<String, String> map = wrapper.getPropertyMap();
        
        Set<String> keys = map.keySet();
        
        for (String s : keys) {
//            log.info(s);
            double value = Double.parseDouble(map.get(s));
            ba.update(value);
        }


    }
}
