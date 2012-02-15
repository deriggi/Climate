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
public class CarbonIntensityWri implements MitigationData {

    private float intensity;
    private int year;
    private Country country;

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public CarbonIntensityWri() {
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
