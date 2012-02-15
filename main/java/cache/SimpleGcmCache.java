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
public class SimpleGcmCache {

    private static SimpleGcmCache cache = new SimpleGcmCache();
    private static  Logger log = Logger.getLogger(SimpleGcmCache.class.getName());
    
    public  HashMap<String,HashMap<String,List<Double>>> dataMap = new HashMap<String,HashMap<String,List<Double>>>();

    private SimpleGcmCache(){
        
    }
    
    public static SimpleGcmCache get(){
        return cache;
    }

    public List<Double> getValues(String iso, String config){
        if(iso == null || iso.trim().length() == 0){
            return new ArrayList<Double>(0);
        }
        iso = iso.toUpperCase().trim();

        if(dataMap.containsKey(iso)){
            return dataMap.get(iso).get(config);
        }
        return new ArrayList<Double>(0);
    }
    public synchronized void addToCache(String iso, String config, List<Double> vals){
        if(iso == null || iso.trim().length() == 0){
            return;
        }
        iso = iso.toUpperCase().trim();
        
        if (!dataMap.containsKey(iso)){
            HashMap<String,List<Double>> configMap = new HashMap<String,List<Double>>();
            dataMap.put(iso,configMap);
        }
        dataMap.get(iso).put(config, vals);
        log.log(Level.FINE, "size of cache is : {0}", dataMap.size());
        log.log(Level.FINE, "size of last entry is : {0}", dataMap.get(iso).get(config).size());
        
    }





}
