/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shapefileloader.gcm.monthly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import sdnis.wb.util.BasicAverager;
import sdnis.wb.util.ShapeWrapper;
import shapefileloader.FeatureHandler;

/**
 *
 * @author wb385924
 */
public class MonthlyFeatureHandler implements FeatureHandler {

    private static final Logger log = Logger.getLogger(MonthlyFeatureHandler.class.getName());
    private HashMap<String, List<Double>> monthValMap = new HashMap<String, List<Double>>();
    private HashMap<String, Integer> months = new HashMap<String, Integer>();
//    private HashMap<String, List<Double>> getMonthValMap() {
//        return monthValMap;
//    }

    private HashMap<String, Double> getMonthAvgMap() {
        if (monthValMap.size() < 12) {
            log.warning("monthmap has less than 12 months");
        }
        Set<String> keys = monthValMap.keySet();
        HashMap<String, Double> monthAvgMap = new HashMap<String, Double>();
        for (String s : keys) {
            BasicAverager ba = new BasicAverager();
//            log.log(Level.INFO, "size is {0} for {1}", new Object[]{monthValMap.get(s).size(), s});
            for (Double d : monthValMap.get(s)) {
                ba.update(d);
            }
            monthAvgMap.put(s, ba.getAvg());
        }
        return monthAvgMap;
    }

    public List<Double> getMonthAveragesAsList() {
        Double[] vals = new Double[12];
        HashMap<String,Double> monthAvgMap = getMonthAvgMap();

        Set<String> keys = getMonthAvgMap().keySet();
        for (String k : keys) {
            vals[months.get(k)] =  monthAvgMap.get(k);
        }
        return toList(vals);
    }

    private List<Double> toList(Double[] arrayVals){
        ArrayList<Double> list = new ArrayList<Double>();
        list.addAll(Arrays.asList(arrayVals));
        return list;
    }

    public MonthlyFeatureHandler() {
        
        months.put("Jan",0);
        months.put("Feb",1);
        months.put("Mar",2);
        months.put("Apr",3);
        months.put("May",4);
        months.put("Jun",5);
        months.put("July",6);
        months.put("Aug", 7);
        months.put("Sep", 8);
        months.put("Oct",9);
        months.put("Nov",10);
        months.put("Dec",11);

    }

    private String convertMultiPolygonToPolygon(String gon) {
        StringBuilder sb = new StringBuilder();
        String geom = gon.substring(gon.indexOf("("), gon.lastIndexOf(")"));
        sb.append("POLYGON");
        sb.append(geom.substring(1));

        return sb.toString();
    }

    public void handleFeature(ShapeWrapper wrapper) {
        HashMap<String, String> map = wrapper.getPropertyMap();
        // map size should be at least twelve
        Set<String> keys = map.keySet();
        for (String s : keys) {
            double value = Double.parseDouble(map.get(s));
            if (!monthValMap.containsKey(s)) {
                monthValMap.put(s, new ArrayList<Double>());
            }
            monthValMap.get(s).add(value);
        }


    }
}
