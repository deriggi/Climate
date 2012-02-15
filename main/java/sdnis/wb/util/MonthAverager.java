/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sdnis.wb.util;

import data.domain.MonthPoint;
import java.util.HashMap;

/**
 *
 * @author wb385924
 */
public class MonthAverager {

    private HashMap<MonthPoint,AveragerHelper> averages = new HashMap<MonthPoint,AveragerHelper>();

    public MonthAverager(){

    }

    public HashMap<MonthPoint,AveragerHelper> getAverages(){
        return averages;
    }

    public AveragerHelper getHelper(MonthPoint monthPoint){
        if(!averages.containsKey(monthPoint)){
            averages.put(monthPoint,new AveragerHelper());
        }
        return averages.get(monthPoint);
    }

    public class AveragerHelper{
        private double sum = 0;
        private double count = 0;
        
        public void addToSum(double value){
            sum += value;
            incrementCount();
        }

        public double getAverage(){
            return sum/count;
        }
        
        private void incrementCount(){
            count++;
        }
    }

    

}
