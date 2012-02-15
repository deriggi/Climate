/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import shapefileloader.oldclimate.OldMonthlyCellularConfig;

/**
 *
 * @author wb385924
 */
public class GcmCache {

    private static GcmCache cache = new GcmCache();
    private static  Logger log = Logger.getLogger(GcmCache.class.getName());
    
    public  HashMap<String,HashMap<OldMonthlyCellularConfig,List<Double>>> dataMap = new HashMap<String,HashMap<OldMonthlyCellularConfig,List<Double>>>();

    private GcmCache(){
        
    }
    
    public static GcmCache get(){
        return cache;
    }

    public List<Double> getValues(String iso, OldMonthlyCellularConfig config){
        if(iso == null || iso.trim().length() == 0){
            return new ArrayList<Double>(0);
        }
        iso = iso.toUpperCase().trim();

        if(dataMap.containsKey(iso)){
            return dataMap.get(iso).get(config);
        }
        return new ArrayList<Double>(0);
    }
    public void addToCache(String iso, OldMonthlyCellularConfig config, List<Double> vals){
        if(iso == null || iso.trim().length() == 0){
            return;
        }
        iso = iso.toUpperCase().trim();
        
        if (!dataMap.containsKey(iso)){
            HashMap<OldMonthlyCellularConfig,List<Double>> configMap = new HashMap<OldMonthlyCellularConfig,List<Double>>();
            dataMap.put(iso,configMap);
        }
        dataMap.get(iso).put(config, vals);
        log.log(Level.FINE, "size of cache is : {0}", dataMap.size());
        
    }





}
