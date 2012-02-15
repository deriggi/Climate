/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author wb385924
 */
@XmlRootElement
public class DroughtAffected implements MitigationData {

    private int startYear;
    private int endYear;
    private int numberAffected;
    private Country country;
    

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getNumberAffected() {
        return numberAffected;
    }

    public void setNumberAffected(int numberAffected) {
        this.numberAffected = numberAffected;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }


}
