/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ascii;

import data.domain.DatePoint;
import java.sql.Connection;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class CacheDataStoreAsciiAction implements AsciiAction{
    private static final Logger log = Logger.getLogger(AsciiAction.class.getName());
    
    private Double min = null;
    private Double max = null;
    private double average;
    
    private double sum = 0;
    private HashSet<DatePoint> datePoints = new HashSet<DatePoint>();

    public HashSet<DatePoint> getDatePoints() {
        return datePoints;
    }



    public String getCSVHeader(){

        char comma = ',';

        StringBuilder sb = new StringBuilder();


        sb.append("avg");
        sb.append(comma);



        sb.append("max");
        sb.append(comma);



        sb.append("min");
        sb.append(comma);



        sb.append("num non null points");
        sb.append(comma);

        return sb.toString();
    }

    public String toCSV(){
        
        char tab='\t';
        
        char comma = ',';

        StringBuilder sb = new StringBuilder();

        sb.append(tab);
        sb.append(getAverage());
        sb.append(comma);


        sb.append(tab);
        sb.append(getMax());
        sb.append(comma);


        sb.append(tab);
        sb.append(getMin());
        sb.append(comma);


        sb.append(tab);
        sb.append(datePoints.size());
        sb.append(comma);

        return sb.toString();
    }

    @Override
    public String toString(){
        String lineSeparator = System.getProperty("line.separator");
        char tab = '\t';
        char comma = ',';

        StringBuilder sb = new StringBuilder();
        sb.append("average:");
        sb.append(tab);
        sb.append(getAverage());
        sb.append(lineSeparator);

        sb.append("max:");
        sb.append(tab);
        sb.append(getMax());
        sb.append(lineSeparator);

        sb.append("min:");
        sb.append(tab);
        sb.append(getMin());
        sb.append(lineSeparator);

        sb.append("num points:");
        sb.append(tab);
        sb.append(datePoints.size());
        sb.append(lineSeparator);
        
        return sb.toString();
    }


    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
    public int getCount(){
        return datePoints.size();
    }

    public void handleNonNullData(double y, double x,  Date date, double data) {
        DatePoint dp = new DatePoint(y,x,date, data);
        if(datePoints.contains(dp)){
           log.log(Level.WARNING, "trying to enter duplicate DatePoint {0}", dp.toString());
           return;
        }
        log.log(Level.FINE, "adding {0}", dp.toString());
        log.log(Level.FINE, "adding {0}", data);
        datePoints.add(dp);
        int size = datePoints.size();
        sum += data;
        average = sum/size;
        log.log(Level.FINE, "computing average from {0} {1}", new Object[]{sum, size});
        
        if(min == null){
            min = data;
        }else{
            if(data < min){
                min = data;
            }
        }

        if(max == null){
            max = data;
        }else{
            if(data > max){
                max = data;
            }
        }


    }
}
