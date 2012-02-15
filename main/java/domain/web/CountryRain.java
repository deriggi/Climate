/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author wb385924
 */
@XmlRootElement
public class CountryRain {
    int countryId;

//    public int getCountryId() {
//        return countryId;
//    }
//
//    public void setCountryId(int countryId) {
//        this.countryId = countryId;
//    }
    
    private Date rainDate;

    public CountryRain(){
        
    }
    


    public float getRainAverage() {
        return rainAverage;
    }

    public void setRainAverage(float rainAverage) {
        this.rainAverage = rainAverage;
    }

    public Date getRainDate() {
        return rainDate;
    }

    public void setRainDate(Date rainDate) {
        this.rainDate = rainDate;
    }
    float rainAverage;

    public CountryRain(Date rainDate, float rainAverage) {
        this.rainDate = rainDate;
        this.rainAverage = rainAverage;
    }

  public CountryRain(Date rainDate, float rainAverage, int countryId) {
      
        this.countryId = countryId;
        this.rainDate = rainDate;
        this.rainAverage = rainAverage;
    }
}
