/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * units are thousands of metric tons of carbon dioxide
 */
@XmlRootElement
public class PerCaptiaCo2 implements MitigationData{

    private float co2;
    private int year;
    private Country country;
    
    public PerCaptiaCo2(){

    }
    
    public float getCo2() {
        return co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


}
