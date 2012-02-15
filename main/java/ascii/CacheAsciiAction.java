/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ascii;

import java.sql.Connection;
import java.util.Date;

/**
 *
 * @author wb385924
 */
public class CacheAsciiAction implements AsciiAction{
    private Double min = null;
    private Double max = null;
    private double average;
    private double count = 0;
    private double sum = 0;

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
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

    public void handleNonNullData(double y, double x,  Date date, double data) {
        count++;
        sum += data;
        average = sum/count;
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
