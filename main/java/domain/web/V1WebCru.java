/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

import domain.CruType;

/**
 *
 * @author wb385924
 */
public class V1WebCru implements Comparable<V1WebCru> ,CruType{

    private Integer year;
    private Integer month;
    private float data;

    public V1WebCru(float data, int year, int month) {
        this.data = data/10.0f;
        this.year = year;
        this.month = month;
    }

    public V1WebCru(float data, int yearOrMonth) {
        this.data = data/10.0f;
        if (yearOrMonth >= 1900) {
            this.year = yearOrMonth;
        } else {
            this.month = yearOrMonth;
        }

    }

    

    public double getData() {
        return data;
    }

    public int getMonth() {
        if(month == null){
            return -1;
        }
        return month;
    }

    public int getYear() {
        if(year == null){
            return -1;
        }
        return year;
    }

    public int compareTo(V1WebCru o) {
        if (o == null) {
            return 1;
        }
        if (getYear() != o.getYear()) {
            return getYear() - o.getYear();
        } else {
            return getMonth() - o.getMonth();
        }
    }
}
