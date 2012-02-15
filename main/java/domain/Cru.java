/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author wb385924
 */
public class Cru implements Comparable<Cru>, CruType{

    private int year = -1;
    private int month = -1;
    private float data;

    public Cru(float data, int year, int month) {
        this.data = data;
        this.year = year;
        this.month = month;
    }
    public Cru(float data, int yearOrMonth) {
        this.data = data;
        if(yearOrMonth >= 1900){
            this.year = yearOrMonth;
        }else{
            this.month = yearOrMonth;
        }
        
    }

    public double getData() {
        return data;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int compareTo(Cru o) {
        if(o == null){
            return 1;
        }
        if(getYear() != o.getYear()){
            return getYear() - o.getYear();
        }else{
            return getMonth() - o.getMonth();
        }
    }
}
