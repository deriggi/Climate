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
public class ExtDataHelper {
    private int droughtAffected;

    public float getCarbonIntensity() {
        return carbonIntensity;
    }

    public void setCarbonIntensity(float carbonIntensity) {
        this.carbonIntensity = carbonIntensity;
    }

    public int getDroughtAffected() {
        return droughtAffected;
    }

    public void setDroughtAffected(int droughtAffected) {
        this.droughtAffected = droughtAffected;
    }

    public float getTotalCo2() {
        return totalCo2;
    }

    public void setTotalCo2(float totalCo2) {
        this.totalCo2 = totalCo2;
    }
    private float totalCo2;
    private float carbonIntensity;
    
    public ExtDataHelper(){

    }
    
    public void init(Country c) {
        this.droughtAffected = c.getDroughtAffected().getNumberAffected();
        this.totalCo2 = c.getTotalCo2().getCo2();
        //this.carbonIntensity = c.getCarbonIntensityWri().getIntensity();
    }

}
