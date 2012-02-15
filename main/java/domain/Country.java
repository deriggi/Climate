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
public class Country implements Comparable<Country> {

    private String name;
    private String iso3;
    private String iso2;
    private int id;

    public int getId() {
        return id;
    }
    private TotalCo2 totalCo2;
    private CarbonIntensityWri carbonIntensityWri;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Country other = (Country) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.iso3 == null) ? (other.iso3 != null) : !this.iso3.equals(other.iso3)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.iso3 != null ? this.iso3.hashCode() : 0);
        return hash;
    }

    public int compareTo(Country o) {
        if (o == null) {
            return 1;
        }
        return this.getName().compareTo(o.getName());
    }
    private PerCaptiaCo2 perCapitaCo2;
    private DroughtAffected droughtAffected;

    public DroughtAffected getDroughtAffected() {
        return droughtAffected;
    }

    public void setDroughtAffected(DroughtAffected droughtAffected) {
        this.droughtAffected = droughtAffected;
    }

    public CarbonIntensityWri getCarbonIntensityWri() {
        return carbonIntensityWri;
    }

    public void setCarbonIntensityWri(CarbonIntensityWri carbonIntensityWri) {
        this.carbonIntensityWri = carbonIntensityWri;
    }

    public PerCaptiaCo2 getPerCapitaCo2() {
        return perCapitaCo2;
    }

    public void setPerCapitaCo2(PerCaptiaCo2 perCapitaCo2) {
        this.perCapitaCo2 = perCapitaCo2;
    }

    public TotalCo2 getTotalCo2() {
        return totalCo2;
    }

    public void setTotalCo2(TotalCo2 totalCo2) {
        this.totalCo2 = totalCo2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Country(String name, int id, String iso3, String iso2) {
        this.name = name;
        this.id = id;

        if (iso3 != null) {
            this.iso3 = iso3;
        }

        if (iso2 != null) {
            this.iso2 = iso2;
        }
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    
}
