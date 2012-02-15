/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package domain;


/**
 *
 * @author wb385924
 */
public class Basin {

    private Country country;
    private int id;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
   // private Set<Study> studies;

    public Basin(){

    }
    public Basin(int id, int code ){
        this.code = code;
        this.id = id;


    }

}
