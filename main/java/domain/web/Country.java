/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;

/**
 *
 * @author wb385924
 */
public class Country implements Comparable<Country>{

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
        int hash = 7;
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 11 * hash + (this.iso3 != null ? this.iso3.hashCode() : 0);
        return hash;
    }

    private String name;
    private String iso3;

    public Country() {
    }

    public Country(String name, String iso3) {
        this.iso3 = name;
        this.name = iso3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public int compareTo(Country o) {
        if(o == null){
            return 1;
        }
        return this.getName().compareTo(o.getName());
    }
}
