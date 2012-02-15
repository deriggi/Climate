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
public class Drainage {

     private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Drainage(){

    }
    public Drainage(String name){
        this.name = name;
    }

}
