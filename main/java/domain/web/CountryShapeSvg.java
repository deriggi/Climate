/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domain.web;



/**
 *
 * @author wb385924
 */

public class CountryShapeSvg extends ShapeSvg {

    
    private String iso3;

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public CountryShapeSvg(String shape, String name, String iso3) {
        setSvg(shape);
        setName(name);
        this.iso3 = iso3;
    }

    
    
}
