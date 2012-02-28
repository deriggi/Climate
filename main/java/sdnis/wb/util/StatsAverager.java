/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sdnis.wb.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 *
 * @author Johnny
 */
public class StatsAverager extends BasicAverager{
    
    private static Logger log = Logger.getLogger(BasicAverager.class.getName());
    private List<Double> allNumbers = new ArrayList<Double>();
    
    
    /**
     * The average of the squared difference from the mean
     * 
     * @return 
     */
    public double getVariance(){
       double average = getAvg();
       BasicAverager ba = new BasicAverager();
       
       for(Double d: allNumbers){
           
           double squaredDifference = Math.pow(average  - d, 2);
           ba.update(squaredDifference);
           
       }
       
       return ba.getAvg();
    }
    
    
    /**
     * The square root of the variance
     * 
     * @return 
     */
    public double getStandardDeviation(){
        return Math.sqrt(getVariance());
    }
    
    
    @Override
    public void update(double val) {
        super.update(val);
        allNumbers.add(val);
    }
    
    public double getMedian(){
        
        if(allNumbers.size() == 1){
            return allNumbers.get(0);
        }
        if(allNumbers.isEmpty()){
            return 0;
        }
        
        Collections.sort(allNumbers);
        double returnValue = 0;
        if(allNumbers.size()%2 == 0){
            int firstIndex = allNumbers.size()/2;
            int secondIndex = firstIndex - 1;
            returnValue = allNumbers.get(firstIndex) + allNumbers.get(secondIndex);
            returnValue = returnValue/2.0;
        }
        else{
            returnValue = allNumbers.get(allNumbers.size()/2);
        }
        
        return returnValue;
    }
    
    public double getPercentile(int percentile){
        
        if(allNumbers.size() == 1){
            return allNumbers.get(0);
        }
        if(allNumbers.isEmpty()){
            return 0;
        }
        
        Collections.sort(allNumbers);
        double returnValue = allNumbers.get(allNumbers.size() * (percentile/100));
        
        return returnValue;
    }
    
}

