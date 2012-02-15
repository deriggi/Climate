/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain.emdat;

import emdat.Emdat;


/**
 *
 * @author wb385924
 */
public class EmdatData {
    
    private Emdat type;
    private double data;
    private int year;

    public EmdatData(Emdat type, double data, int year) {
        this.type = type;
        this.data = data;
        this.year = year;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public Emdat getType() {
        return type;
    }

    public void setType(Emdat type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
