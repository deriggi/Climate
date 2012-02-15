/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author wb385924
 */
public class AnnualMapData implements MapData {

    private transient static HashMap<Integer, String> months = null;
    

    private double annual;


    public void assignValuesFromMap(HashMap<Integer, Double> map) {
//        HashMap<String, Double> monthMap = new HashMap<String, Double>();

        if(map != null && !map.isEmpty()){
            Collection<Double> vals = map.values();
            if(!vals.isEmpty()){
                this.annual = vals.iterator().next();
            }
            return;
        }
       


    }
}
